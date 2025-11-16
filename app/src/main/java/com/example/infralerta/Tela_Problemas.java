package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
            ArrayList<String> problemasSelecionados = pegarProblemas();

            if (problemasSelecionados.isEmpty()) {
                Toast.makeText(Tela_Problemas.this, "Selecione ao menos um problema!\nUse o campo 'Outro' se necessário.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent detalhes = new Intent(Tela_Problemas.this, Tela_Detalhes.class);
            String coordenadas = getIntent().getStringExtra("coordenadas");

            detalhes.putStringArrayListExtra("problemasSelecionados", problemasSelecionados);
            detalhes.putExtra("local", txtLocal.getText().toString());
            detalhes.putExtra("coordenadas", coordenadas);
            startActivity(detalhes);
        });

        txtLocal = findViewById(R.id.txtLocal);

        Intent intent = getIntent();
        String localRecebido = intent.getStringExtra("local");

        if (localRecebido != null) {
            txtLocal.setText(localRecebido);
        }

    }

    @NonNull
    private ArrayList<String> pegarProblemas() {
        ArrayList<String> problemasSelecionados = new ArrayList<>();
        if (btasfalto.isChecked()) problemasSelecionados.add("• " + getString(R.string.probAsfalto));
        if (btvazamento.isChecked()) problemasSelecionados.add("• " + getString(R.string.probVazaEsgoto));
        if (btfaltaposte.isChecked()) problemasSelecionados.add("• " + getString(R.string.probEnergiaPostes));
        if (btfaltageral.isChecked()) problemasSelecionados.add("• " + getString(R.string.probEnergiaGeral));
        if (btcalcada.isChecked()) problemasSelecionados.add("• " + getString(R.string.probCalcada));
        if (btarvoreanormal.isChecked()) problemasSelecionados.add("• " + getString(R.string.probTamanhoArvore));
        if (btproblemasemaforo.isChecked()) problemasSelecionados.add("• " + getString(R.string.probSemaforo));
        if (btindicacao.isChecked()) problemasSelecionados.add("• " + getString(R.string.probSinalizacaoRua));
        if (btoutro.isChecked()) problemasSelecionados.add("• " + getString(R.string.probOutro));
        return problemasSelecionados;
    }
}