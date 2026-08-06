package com.example.infralerta;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tela_Detalhes extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;
    MaterialCardView mcvImagem, mcvBtSel;
    Button btImagem;
    FloatingActionButton btVoltarDetalhe, btEnviar;
    TextView txtlocalselecionado;
    EditText txtDetalhamento;
    ImageView imgDenuncia;
    Uri imageUri, photoUri;
    String localselecionado;
    String caminhoImagemSalva = null;
    LinearLayout layoutproblemas;
    private Bitmap bitmapParaSalvar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_detalhes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btImagem = findViewById(R.id.btImagem);
        btVoltarDetalhe = findViewById(R.id.btvoltarsituacao);
        btEnviar = findViewById(R.id.btEnvio);
        imgDenuncia = findViewById(R.id.imgDenuncia);
        mcvImagem = findViewById(R.id.mcvImagem);
        mcvBtSel = findViewById(R.id.mcvBtSel);
        txtlocalselecionado = findViewById(R.id.txtlocalselecionado);
        txtDetalhamento = findViewById(R.id.inDetalhamento);
        layoutproblemas = findViewById(R.id.layoutproblemas);

        Intent detalhes = getIntent();
        String data = detalhes.getStringExtra("data");
        String local = detalhes.getStringExtra("local");
        String coordenadas = detalhes.getStringExtra("coordenadas");
        ArrayList<String> problemas = detalhes.getStringArrayListExtra("problemasSelecionados");

        if (local != null) {
            txtlocalselecionado.setText(local);
        }

        if (problemas != null && !problemas.isEmpty()) {
            for (String problema : problemas) {
                TextView textview = new TextView(this);
                textview.setText(problema);
                textview.setTextSize(16);
                layoutproblemas.addView(textview);
            }
        }

        btImagem.setOnClickListener(v -> mostrarOpcoesImagem());

        btVoltarDetalhe.setOnClickListener(v -> finish());

        btEnviar.setOnClickListener(v -> {
            //salva localmente para garantir o arquivo
            if (bitmapParaSalvar != null) {
                salvarImagemComprimida(bitmapParaSalvar);
                bitmapParaSalvar = null;
            }

            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            assert problemas != null;
            String problemasStr = problemasParaString(problemas);
            String descricao = txtDetalhamento.getText().toString();

            Denuncia novaDenuncia = new Denuncia(userId, data, local, coordenadas, problemasStr, descricao);
            novaDenuncia.setCaminhoImagem(caminhoImagemSalva); //inicialmente o caminho local

            //inicia o processo de envio (Upload -> Banco)
            enviarDenunciaCompleta(novaDenuncia);
        });
    }

    private void enviarDenunciaCompleta(Denuncia denuncia) {
        if (caminhoImagemSalva != null) {
            File arquivoImagem = new File(caminhoImagemSalva);
            uploadImagemParaSupabase(denuncia, arquivoImagem);
        } else {
            //se não tem imagem, salva direto no Supabase
            salvarNoSupabase(denuncia);
        }
    }

    private void uploadImagemParaSupabase(Denuncia denuncia, File arquivo) {
        RequestBody requestBody = RequestBody.create(arquivo, MediaType.parse("image/jpeg"));
        String nomeArquivoNoServidor = arquivo.getName();

        SupabaseClient.getApi().uploadImagem(
                SupabaseClient.ANON_KEY,
                "Bearer " + SupabaseClient.ANON_KEY,
                "image/jpeg",
                "true", // x-upsert: true
                nomeArquivoNoServidor,
                requestBody
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                //se for sucesso (200/201) ou conflito (409 - imagem já existe), prosseguimos
                if (response.isSuccessful() || response.code() == 409) {
                    String urlPublica = SupabaseClient.BASE_URL + "/storage/v1/object/public/imagens_denuncias/" + nomeArquivoNoServidor;
                    denuncia.setCaminhoImagem(urlPublica);
                    salvarNoSupabase(denuncia);
                } else {
                    Toast.makeText(Tela_Detalhes.this, "Erro no upload da imagem: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(Tela_Detalhes.this, "Falha na rede ao subir imagem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarNoSupabase(Denuncia denuncia) {
        SupabaseClient.getApi().insertDenuncia(
                SupabaseClient.ANON_KEY,
                "Bearer " + SupabaseClient.ANON_KEY,
                denuncia
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    BancoControllerDenuncias bd = new BancoControllerDenuncias(Tela_Detalhes.this);
                    bd.criarDenuncia(denuncia);

                    Toast.makeText(Tela_Detalhes.this, "Denúncia enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(Tela_Detalhes.this, Tela_Mapas.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(Tela_Detalhes.this, "Erro ao salvar no Supabase: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(Tela_Detalhes.this, "Falha na conexão com Supabase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarOpcoesImagem() {
        String[] opcoes = {"Câmera", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione a origem da imagem");
        builder.setItems(opcoes, (dialog, which) -> {
            if (which == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    abrirCamera();
                }
            } else {
                abrirGaleria();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirGaleria() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = criarArquivoImagemTemporario();
            } catch (IOException ex) {
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.infralerta.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    private File criarArquivoImagemTemporario() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uriSelecionada = null;
            if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                uriSelecionada = data.getData();
            } else if (requestCode == TAKE_PHOTO) {
                uriSelecionada = photoUri;
            }

            if (uriSelecionada != null) {
                processarImagem(uriSelecionada);
            }
        }
    }

    private void processarImagem(Uri uri) {
        imageUri = uri;
        try {
            Bitmap bitmapOriginal = carregarBitmapOtimizado(uri);
            Bitmap bitmapRotacionado = rotacionarImagemSeNecessario(bitmapOriginal, uri);
            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmapRotacionado);

            mcvImagem.setVisibility(View.VISIBLE);
            imgDenuncia.setImageBitmap(bitmapRedimensionado);
            bitmapParaSalvar = bitmapRedimensionado;

            if (bitmapOriginal != bitmapRotacionado) bitmapOriginal.recycle();
            if (bitmapRotacionado != bitmapRedimensionado) bitmapRotacionado.recycle();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Falha ao carregar a imagem.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap carregarBitmapOtimizado(Uri uri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try (InputStream input = getContentResolver().openInputStream(uri)) {
            if (input != null) {
                BitmapFactory.decodeStream(input, null, options);
            }
        }

        int larguraOriginal = options.outWidth;
        int alturaOriginal = options.outHeight;
        int inSampleSize = 1;

        if (larguraOriginal > 1080 || alturaOriginal > 1080) {
            final int metadeLargura = larguraOriginal / 2;
            final int metadeAltura = alturaOriginal / 2;
            while ((metadeLargura / inSampleSize) >= 1080 && (metadeAltura / inSampleSize) >= 1080) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        try (InputStream input = getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(input, null, options);
        }
    }

    private Bitmap rotacionarImagemSeNecessario(Bitmap img, Uri selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(Objects.requireNonNull(getContentResolver().openInputStream(selectedImage)));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotacionarBitmap(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotacionarBitmap(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotacionarBitmap(img, 270);
            default:
                return img;
        }
    }


    public static Bitmap rotacionarBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private Bitmap redimensionarBitmap(Bitmap bitmap) {
        //se a imagem já for pequena o suficiente, não faz nada
        if (bitmap.getHeight() <= 1080 && bitmap.getWidth() <= 1080) {
            return bitmap;
        }

        int largura = bitmap.getWidth();
        int altura = bitmap.getHeight();
        float proporcaoBitmap = (float) largura / (float) altura;

        //define as novas dimensões baseadas no lado maior da imagem
        if (largura > altura) {
            largura = 1080;
            altura = (int) (largura / proporcaoBitmap);
        } else {
            altura = 1080;
            largura = (int) (altura * proporcaoBitmap);
        }
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true);
    }

    private void salvarImagemComprimida(Bitmap bitmapComprimido) {
        try {
            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            File directory = getFilesDir();
            String fileName = "user_" + userId + "_denuncia_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);

            //comprime e salva o bitmap que já foi redimensionado, 85 é um bom equilíbrio entre qualidade e tamanho.
            bitmapComprimido.compress(Bitmap.CompressFormat.JPEG, 85, fos);

            fos.close();

            //guarda o caminho do arquivo salvo para enviar ao banco de dados
            caminhoImagemSalva = file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Falha ao salvar a imagem.", Toast.LENGTH_SHORT).show();
        }
    }



    public static String problemasParaString(ArrayList<String> _problemas) {
        StringBuilder sbProblemas = new StringBuilder();
        for (String prob : _problemas) {
            sbProblemas.append(prob).append(";");
        }
        return sbProblemas.toString();
    }
}