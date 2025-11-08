package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Tela_Detalhes extends AppCompatActivity {
    FloatingActionButton btVoltarDetalhe, btEnviar;
    TextView txtlocalselecionado;
    String localselecionado;
    LinearLayout layoutproblemas;



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
        txtlocalselecionado = findViewById(R.id.txtlocalselecionado);
        txtlocalselecionado.setText(localselecionado);
        layoutproblemas = findViewById(R.id.layoutproblemas);

        Intent detalhes = getIntent();
        localselecionado = getIntent().getExtras().getString("a");
        if (localselecionado != null) {
            txtlocalselecionado.setText(localselecionado);
        }
        ArrayList<String> problemas = detalhes.getStringArrayListExtra("problemasSelecionados");

        if (problemas != null && !problemas.isEmpty()) {
            for (String problema : problemas){
                TextView textview = new TextView(this);
                textview.setText(problema);
                textview.setTextSize(16);
                layoutproblemas.addView(textview);
            }
        }



        btVoltarDetalhe.setOnClickListener(v -> {
            Intent Mapa = new Intent(Tela_Detalhes.this, Tela_Mapas.class);
            startActivity(Mapa);
            finish();
        });


    }
}