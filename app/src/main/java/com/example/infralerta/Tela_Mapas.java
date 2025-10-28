package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Mapas extends AppCompatActivity {
    Button btMais;
    ImageButton btMapaMapa, btMapaDenuncia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_mapas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btMais = findViewById(R.id.btMais);
        btMapaMapa = findViewById(R.id.btmapamapa);
        btMapaDenuncia = findViewById(R.id.btdenunciasmapa);
        btMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Problema = new Intent(Tela_Mapas.this, Tela_Problemas.class);
                startActivity(Problema);
            }
        });
        btMapaMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btMapaDenuncia.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Denuncia = new Intent(Tela_Mapas.this, Tela_Denuncias.class);
                startActivity(Denuncia);
            }
        }));
    }
}