package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;

public class Tela_Denuncias extends AppCompatActivity {
    LinearLayout llDenuncias;
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

        btMapaDenunc = findViewById(R.id.btMapaDenunc);
        btMapaDenunc.setOnClickListener(v -> finish());
        llDenuncias = findViewById(R.id.llDenuncias);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1); //pega o user_id, caso não exista, será -1
        BancoControllerDenuncias bd = new BancoControllerDenuncias(getBaseContext());

        ArrayList<Integer> denunciasIds = bd.buscarDenunciasPorUserId(userId);

        for (int denunciaId : denunciasIds) {
            Denuncia denuncia = bd.buscarDenunciaPorId(userId, denunciaId);
            String data = denuncia.getData();
            String local = denuncia.getEndereco();

            //denunciaView é o fragment inteiro
            View denunciaView = getLayoutInflater().inflate(R.layout.fragment_denuncia, null);
            TextView txtLocal = denunciaView.findViewById(R.id.txtLocal);
            TextView txtData = denunciaView.findViewById(R.id.txtData);
            //o clDenuncia é o conteúdo do fragment
            ConstraintLayout clDenuncia = denunciaView.findViewById(R.id.clDenuncia);

            txtLocal.setText(local);
            txtData.setText(data);

            //a denunciaView é adicionada ao LinearLayout
            llDenuncias.addView(denunciaView);

            clDenuncia.setOnClickListener(v -> {
                Intent it = new Intent(Tela_Denuncias.this, Tela_Especifica.class);
                it.putExtra("denuncia_id", denunciaId);
                startActivity(it);
            });
        }
    }
}