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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

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

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        BancoControllerDenuncias bd = new BancoControllerDenuncias(getBaseContext());

        imgDenuncia = findViewById(R.id.imgDenuncia);

        txtLocal = findViewById(R.id.txtLocal);
        txtData = findViewById(R.id.txtData);
        txtProblemas = findViewById(R.id.txtProblemas);
        txtDetalhamento = findViewById(R.id.txtDetalhamento);

        fabVoltar = findViewById(R.id.fabVoltar);
        fabVoltar.setOnClickListener(v -> finish());

        Intent it = getIntent();
        int denunciaId = it.getIntExtra("denuncia_id", -1);

        Denuncia denuncia = bd.buscarDenunciaPorId(userId, denunciaId);

        //verifica se a denúncia possui um caminho de imagem válido
        String caminhoImagem = denuncia.getCaminhoImagem();
        if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
            File imgFile = new File(caminhoImagem);

            if (imgFile.exists()) {
                //converte o arquivo de imagem em um Bitmap
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                imgDenuncia.setImageBitmap(myBitmap);
            }
        } else {
            //cculta o ImageView se não houver imagem (?)
            imgDenuncia.setVisibility(View.GONE);
        }

        txtLocal.setText(denuncia.getEndereco());
        txtData.setText(denuncia.getData());
        txtProblemas.setText(denuncia.getProblemas().replaceAll(";", "\n"));
        txtDetalhamento.setText(denuncia.getDescricao());
    }
}