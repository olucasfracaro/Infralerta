package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Login extends AppCompatActivity {
    Button btLogin, btCadastro;
    EditText txtEmailLogin, txtSenhaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btLogin = findViewById(R.id.btEntrarLogin);
        btCadastro = findViewById(R.id.btCadastrarLogin);

        btCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastro = new Intent(Tela_Login.this, Tela_Cadastro.class);
                startActivity(cadastro);
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent entrar = new Intent(Tela_Login.this, Tela_Mapas.class);
                startActivity(entrar);
                finish();
            }
        });

        txtEmailLogin = findViewById(R.id.txtEmail);
        txtSenhaLogin = findViewById(R.id.txtSenha);

    }
}