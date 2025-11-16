package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tela_Especifica extends AppCompatActivity {
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

        txtLocal = findViewById(R.id.txtLocal);
        txtData = findViewById(R.id.txtData);
        txtProblemas = findViewById(R.id.txtProblemas);
        txtDetalhamento = findViewById(R.id.txtDetalhamento);

        fabVoltar = findViewById(R.id.fabVoltar);
        fabVoltar.setOnClickListener(v -> finish());

        Intent it = getIntent();
        int denunciaId = it.getIntExtra("denuncia_id", -1);

        Denuncia denuncia = bd.buscarDenunciaPorId(userId, denunciaId);

        txtLocal.setText(denuncia.getEndereco());
        txtData.setText(denuncia.getData());
        txtProblemas.setText(denuncia.getProblemas().replaceAll(";", "\n"));
        txtDetalhamento.setText(denuncia.getDescricao());
    }
}