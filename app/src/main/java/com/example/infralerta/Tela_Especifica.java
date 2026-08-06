package com.example.infralerta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AlphaAnimation;
import java.io.File;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tela_Especifica extends AppCompatActivity {
    ImageView imgDenuncia;
    TextView txtLocal, txtData, txtProblemas, txtDetalhamento;
    FloatingActionButton fabVoltar;
    ProgressBar loading;
    NestedScrollView layoutConteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_especifica);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent it = getIntent();
        int denunciaId = it.getIntExtra("denuncia_id", -1);

        if (denunciaId != -1) {
            buscarDetalhesSupabase(denunciaId);
        }

        imgDenuncia = findViewById(R.id.imgDenuncia);

        txtLocal = findViewById(R.id.txtLocal);
        txtData = findViewById(R.id.txtData);
        txtProblemas = findViewById(R.id.txtProblemas);
        txtDetalhamento = findViewById(R.id.txtDetalhamento);

        loading = findViewById(R.id.loading);
        layoutConteudo = findViewById(R.id.layoutConteudo);

        fabVoltar = findViewById(R.id.fabVoltar);
        fabVoltar.setOnClickListener(v -> finish());

    }

    private void buscarDetalhesSupabase(int denunciaId) {
        SupabaseApi api = SupabaseClient.getApi();
        api.getDenunciaPorId(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + denunciaId)
                .enqueue(new Callback<List<Denuncia>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Denuncia>> call, @NonNull Response<List<Denuncia>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Denuncia denuncia = response.body().get(0);
                            exibirDados(denuncia);
                        } else {
                            Toast.makeText(Tela_Especifica.this, "Erro ao carregar detalhes.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Denuncia>> call, @NonNull Throwable t) {
                        Toast.makeText(Tela_Especifica.this, "Falha na conexão.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void exibirDados(Denuncia denuncia) {
        String caminhoImagem = denuncia.getCaminhoImagem();
        if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
            if (caminhoImagem.startsWith("http")) {
                //carrega imagem da URL usando Picasso
                Picasso.get().load(caminhoImagem).into(imgDenuncia);
            } else {
                //caso ainda existam caminhos locais (legado)
                File imgFile = new File(caminhoImagem);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgDenuncia.setImageBitmap(myBitmap);
                }
            }
        } else {
            imgDenuncia.setVisibility(View.GONE);
        }

        txtLocal.setText(denuncia.getEndereco());
        String data = denuncia.getData();
        if (data != null && data.length() >= 10) {
            //formatar de YYYY-MM-DD -> DD/MM/YYYY
            txtData.setText(String.format("%s/%s/%s", data.substring(8, 10), data.substring(5, 7), data.substring(0, 4)));
        }
        txtProblemas.setText(denuncia.getProblemas().replace(";", "\n"));
        txtDetalhamento.setText(denuncia.getDescricao());

        //esconde o loading e mostra o conteúdo com uma animação suave de fade-in
        loading.setVisibility(View.GONE);
        layoutConteudo.setVisibility(View.VISIBLE);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        layoutConteudo.startAnimation(fadeIn);
    }
}