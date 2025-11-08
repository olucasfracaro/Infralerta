package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Tela_Problemas extends AppCompatActivity {
    FloatingActionButton btproximoinfra;
    TextView txtLocal;
    CheckBox btasfalto, btvazamento, btfaltaposte, btfaltageral, btcalcada, btarvoreanormal, btausenciaasfalto, btproblemasemaforo, btindicacao;
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
            ArrayList<String> problemasSelecionados = new ArrayList<>();
            if (btasfalto.isChecked()) problemasSelecionados.add("* Asfalto Danificado");
            if (btvazamento.isChecked()) problemasSelecionados.add("* Vazamento de Esgoto");
            if (btfaltaposte.isChecked()) problemasSelecionados.add("* Falta de Energia (Poste)");
            if (btfaltageral.isChecked()) problemasSelecionados.add("* Falta de Energia (Geral)");
            if (btcalcada.isChecked()) problemasSelecionados.add("* Calçada Danificada");
            if (btarvoreanormal.isChecked()) problemasSelecionados.add("* Árvore de Tamanho Anormal");
            if (btausenciaasfalto.isChecked()) problemasSelecionados.add("* Ausência de Asfalto");
            if (btproblemasemaforo.isChecked()) problemasSelecionados.add("* Problema de Semáforo");
            if (btindicacao.isChecked()) problemasSelecionados.add("* Falta de Indicação na Rua");


            Intent detalhes = new Intent(Tela_Problemas.this, Tela_Detalhes.class);
            detalhes.putStringArrayListExtra("problemasSelecionados", problemasSelecionados);
            detalhes.putExtra("a", txtLocal.getText().toString());
            startActivity(detalhes);
        });

        txtLocal = findViewById(R.id.txtLocal);

        Intent intent = getIntent();
        String localRecebido = intent.getStringExtra("a");

        if (localRecebido != null) {
            txtLocal.setText(localRecebido);
        }

    }
}