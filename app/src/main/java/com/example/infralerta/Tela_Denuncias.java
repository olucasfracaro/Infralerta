package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Tela_Denuncias extends AppCompatActivity {
    FloatingActionButton btMapaDenunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_denuncias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1); //pega o user_id, caso não exista, será -1

        BancoControllerDenuncias bd = new BancoControllerDenuncias(getBaseContext());

        ArrayList<Denuncia> denuncias = bd.buscarDenunciasPorUserId(userId);

        //puxa cada denuncia do usuario (usando user_id)
        for (Denuncia denuncia : denuncias) {
            String local = denuncia.getEndereco();
            String coordenadas = denuncia.getCoordenadas();
            String problemasStr = denuncia.getProblemas();
            String descricao = denuncia.getDescricao();

            //TODO: SUBSTITUIR ISSO POR UMA LISTA EXIBINDO AS DENÚNCIAS. RECOMENDÁVEL POR UM SCROLLVIEW!
            System.out.printf("Local: %s\n Coordenadas: %s\n Problemas: %s\n Descrição: %s\n%n",
                                                local, coordenadas, problemasStr, descricao);
        }

        btMapaDenunc = findViewById(R.id.btMapaDenunc);

        btMapaDenunc.setOnClickListener(v -> finish());
    }
}