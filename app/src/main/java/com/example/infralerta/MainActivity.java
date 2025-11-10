package com.example.infralerta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btproxima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        verificaLogin(this);

        btproxima = findViewById(R.id.btproximo);

        btproxima.setOnClickListener(view -> {
            Intent login = new Intent(MainActivity.this, Tela_Login.class);
            startActivity(login);
        });
    }

    public static void verificaLogin(Context context) {
        try { //se o usuário já estiver logado, pule essa tela
            SharedPreferences prefs = context.getSharedPreferences("usuario", MODE_PRIVATE);
            int user_id = prefs.getInt("user_id", -1);

            if (user_id > -1) {
                Intent it = new Intent(context, Tela_Mapas.class);
                context.startActivity(it);
            }
        } catch (Exception e) {
            System.out.println("Sem SharedPreferences, ignorando...");
        }
    }
}