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

public class Tela_Cadastro extends AppCompatActivity {
    Button btCADcadastro, btCADentrar;
    EditText txtCADEmail, txtCADSenha, txtCADCPF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btCADcadastro = findViewById(R.id.btCadastrarCad);
        btCADentrar = findViewById(R.id.btEntrarCad);

        txtCADEmail = findViewById(R.id.txtEmailCad);
        txtCADSenha = findViewById(R.id.txtSenhaCad);
        txtCADCPF = findViewById(R.id.txtcpf);

        btCADentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Entrar = new Intent(Tela_Cadastro.this , Tela_Login.class);
                startActivity(Entrar);
            }

        });
    }
}