package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tela_Detalhes extends AppCompatActivity {
    FloatingActionButton btVoltarDetalhe, btEnviar;


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
        btVoltarDetalhe = findViewById(R.id.btvoltarsituacao);
        btEnviar = findViewById(R.id.btEnvio);

        btVoltarDetalhe.setOnClickListener(v -> {
            Intent Mapa = new Intent(Tela_Detalhes.this, Tela_Mapas.class);
        });

    }
}