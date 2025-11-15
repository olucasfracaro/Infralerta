package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
            cadastro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(cadastro);
        });
    }

    private void verificarDados() {
        String email = txtEmailLogin.getText().toString().trim(); // .trim() remove espaços extras
        String senhaInserida = txtSenhaLogin.getText().toString();

        if (email.isEmpty() || senhaInserida.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());
        //gera o hash da senha que o usuário inseriu
        String senhaInseridaHash = Tela_Cadastro.sha256(senhaInserida);

        //faz a consulta no banco de dados
        try (Cursor dados = bd.carregarDadosLogin(email, senhaInseridaHash)) {

            //verifica se a consulta retornou dados e se os dados conferem
            if (dados != null && dados.moveToFirst()) {
                //LOGIN SUCEDIDO!

                //pega o user_id da consulta
                int columnIndex = dados.getColumnIndex("user_id");
                if (columnIndex != -1) {
                    int user_id = dados.getInt(columnIndex);

                    //salva o user_id nas prefs
                    SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", user_id);
                    editor.apply();

                    txtEmailLogin.setText("");
                    txtSenhaLogin.setText("");
                    Intent it = new Intent(Tela_Login.this, Tela_Mapas.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);

                } else {
                    //coluna 'user_id' não foi encontrada na consulta
                    Toast.makeText(this, "Erro crítico: coluna de usuário não encontrada.", Toast.LENGTH_SHORT).show();
                }

            } else {
                //login falho
                Toast.makeText(this, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //qualquer outra exceção que possa ocorrer
            Toast.makeText(this, "Ocorreu um erro durante o login.", Toast.LENGTH_SHORT).show();
            Log.e("Tela_Login", "Erro ao verificar dados de login", e);
        }
    }

}