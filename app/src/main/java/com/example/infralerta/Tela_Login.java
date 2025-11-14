package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Login extends AppCompatActivity {
    Button btLogin;
    LinearLayout btCadastro;
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

        MainActivity.verificaLogin(getBaseContext());

        txtEmailLogin = findViewById(R.id.txtEmail);
        txtSenhaLogin = findViewById(R.id.txtSenha);
        btLogin = findViewById(R.id.btEntrarLogin);
        btCadastro = findViewById(R.id.btCadastrarLogin);

        btLogin.setOnClickListener(v -> verificarDados());

        btCadastro.setOnClickListener(view -> {
            Intent cadastro = new Intent(Tela_Login.this, Tela_Cadastro.class);
            startActivity(cadastro);
        });
    }

    private void verificarDados() {
        String email = txtEmailLogin.getText().toString();
        String senhaInserida = txtSenhaLogin.getText().toString();

        if (email.isEmpty() || senhaInserida.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());
        Cursor dados = null;
        try {
            dados = bd.carregarDadosLogin(email, senhaInserida);

            //move para a primeira linha e verifica se ela existe
            if (dados != null && dados.moveToFirst()) {
                //sucesso, agora é só pegar o user_id pro prefs.
                int columnIndex = dados.getColumnIndex("user_id");
                if (columnIndex != -1) {
                    int user_id = dados.getInt(columnIndex);

                    //salvando no prefs
                    SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", user_id);
                    editor.apply();

                    //apagando campos e mudando de tela
                    txtEmailLogin.setText("");
                    txtSenhaLogin.setText("");
                    Intent it = new Intent(Tela_Login.this, Tela_Mapas.class);
                    startActivity(it);
                } else {
                    Toast.makeText(this, "Erro: coluna de usuário não encontrada", Toast.LENGTH_SHORT).show();
                }

            } else {
                //login falho
                Toast.makeText(this, "Usuário ou senha inválidos. Verifique os dados e tente novamente", Toast.LENGTH_LONG).show();
            }
        } finally {
            if (dados != null) {
                dados.close();
            }
        }
    }
}