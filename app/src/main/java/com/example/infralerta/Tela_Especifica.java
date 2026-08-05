package com.example.infralerta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import java.io.File;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tela_Especifica extends AppCompatActivity {
    ImageView imgDenuncia;
    TextView txtLocal, txtData, txtProblemas, txtDetalhamento;
    FloatingActionButton fabVoltar;

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

        imgDenuncia = findViewById(R.id.imgDenuncia);

        txtLocal = findViewById(R.id.txtLocal);
        txtData = findViewById(R.id.txtData);
        txtProblemas = findViewById(R.id.txtProblemas);
        txtDetalhamento = findViewById(R.id.txtDetalhamento);

        fabVoltar = findViewById(R.id.fabVoltar);
        fabVoltar.setOnClickListener(v -> finish());

        Intent it = getIntent();
        int denunciaId = it.getIntExtra("denuncia_id", -1);

        if (denunciaId != -1) {
            buscarDetalhesSupabase(denunciaId);
        }
    }

    private void buscarDetalhesSupabase(int denunciaId) {
        SupabaseApi api = SupabaseClient.getApi();
        api.getDenunciaPorId(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + denunciaId)
                .enqueue(new Callback<List<Denuncia>>() {
                    @Override
                    public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Denuncia denuncia = response.body().get(0);
                            exibirDados(denuncia);
                        } else {
                            Toast.makeText(Tela_Especifica.this, "Erro ao carregar detalhes.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Denuncia>> call, Throwable t) {
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
        txtData.setText(denuncia.getData());
        txtProblemas.setText(denuncia.getProblemas().replaceAll(";", "\n"));
        txtDetalhamento.setText(denuncia.getDescricao());
    }
}