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
    CheckBox btasfalto, btvazamento, btfaltaposte, btfaltageral, btcalcada, btarvoreanormal, btoutro, btproblemasemaforo, btindicacao;
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
        btoutro = findViewById(R.id.btoutro);
        btproblemasemaforo = findViewById(R.id.btproblemasemaforo);
        btindicacao = findViewById(R.id.btindicacao);


        btproximoinfra.setOnClickListener(view -> {
            ArrayList<String> problemasSelecionados = new ArrayList<>();
            if (btasfalto.isChecked()) problemasSelecionados.add("• Problemas com o asfalto");
            if (btvazamento.isChecked()) problemasSelecionados.add("• Vazamento de esgoto");
            if (btfaltaposte.isChecked()) problemasSelecionados.add("• Falta de energia (poste)");
            if (btfaltageral.isChecked()) problemasSelecionados.add("• Falta de energia (geral)");
            if (btcalcada.isChecked()) problemasSelecionados.add("• Calçada danificada");
            if (btarvoreanormal.isChecked()) problemasSelecionados.add("• Árvore de tamanho anormal");
            if (btproblemasemaforo.isChecked()) problemasSelecionados.add("• Problema de semáforo");
            if (btindicacao.isChecked()) problemasSelecionados.add("• Falta de indicação na rua");
            if (btoutro.isChecked()) problemasSelecionados.add("• Outro (detalhar)");


            Intent detalhes = new Intent(Tela_Problemas.this, Tela_Detalhes.class);
            detalhes.putStringArrayListExtra("problemasSelecionados", problemasSelecionados);
            detalhes.putExtra("local", txtLocal.getText().toString());
            startActivity(detalhes);
        });

        txtLocal = findViewById(R.id.txtLocal);

        Intent intent = getIntent();
        String localRecebido = intent.getStringExtra("local");

        if (localRecebido != null) {
            txtLocal.setText(localRecebido);
        }

    }
}