package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Problemas extends AppCompatActivity {
    Button btproximoinfra;
    TextView txtLocal;
    RadioGroup radiogProblemas;
    RadioButton btasfalto, btvazamento, btfaltaposte, btfaltageral, btcalcada, btarvoreanormal, btausenciaasfalto, btproblemasemaforo, btindicacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_problemas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btproximoinfra = findViewById(R.id.btproximoinfra);
        txtLocal = findViewById(R.id.txtLocal);
        btasfalto = findViewById(R.id.btasfalto);
        btvazamento = findViewById(R.id.btvazamento);
        btfaltaposte = findViewById(R.id.btfaltaposte);
        btfaltageral = findViewById(R.id.btfaltageral);
        btcalcada = findViewById(R.id.btcalcada);
        btarvoreanormal = findViewById(R.id.btarvoreanormal);
        btausenciaasfalto = findViewById(R.id.btausenciaasfalto);
        btproblemasemaforo = findViewById(R.id.btproblemasemaforo);
        btindicacao = findViewById(R.id.btindicacao);


        btproximoinfra.setOnClickListener(view -> {
            Intent detalhes = new Intent(Tela_Problemas.this, Tela_Detalhes.class);
            startActivity(detalhes);
        });

    }
}