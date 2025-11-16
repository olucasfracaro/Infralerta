package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText txtDetalhamento;
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
        txtDetalhamento = findViewById(R.id.inDetalhamento);
        layoutproblemas = findViewById(R.id.layoutproblemas);

        Intent detalhes = getIntent();
        String data = detalhes.getExtras().getString("data");
        localselecionado = getIntent().getExtras().getString("local");
        String coordenadas = getIntent().getExtras().getString("coordenadas");

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

        btVoltarDetalhe.setOnClickListener(v -> finish());

        btEnviar.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            BancoControllerDenuncias bd = new BancoControllerDenuncias(getBaseContext());

            int userId = prefs.getInt("user_id", -1);
            String problemasStr = problemasParaString(problemas);
            String descricao = txtDetalhamento.getText().toString();

            Denuncia novaDenuncia = new Denuncia(userId, data, localselecionado, coordenadas, problemasStr, descricao);

            if (bd.criarDenuncia(novaDenuncia)) {
                Toast.makeText(getBaseContext(), "Denúncia enviada com sucesso!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(Tela_Detalhes.this, Tela_Mapas.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            } else {
                Toast.makeText(this, "Falha ao criar denúncia.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String problemasParaString(ArrayList<String> _problemas) {
        StringBuilder sbProblemas = new StringBuilder();
        for (String prob : _problemas) {
            sbProblemas.append(prob).append(";");
        }
        return sbProblemas.toString();
    }
}