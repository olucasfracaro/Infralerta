package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Cadastro extends AppCompatActivity {
    Button btCADcadastro;
    LinearLayout btCADentrar;
    EditText txtCADEmail, txtCADSenha, txtCADCPF, txtCADNome;


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
        txtCADNome = findViewById(R.id.txtNomeCad);

        //tirei o Intent para otimizar o código
        btCADentrar.setOnClickListener(v -> finish());

        btCADcadastro.setOnClickListener(v -> cadastrar());
    }

    public void cadastrar() {
        String txtNome = txtCADNome.getText().toString();
        String txtEmail = txtCADEmail.getText().toString();
        String txtSenha = txtCADSenha.getText().toString();
        String txtCPF = txtCADCPF.getText().toString();

        if (txtNome.isEmpty() || txtEmail.isEmpty() || txtSenha.isEmpty() || txtCPF.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show();
        } else if (!txtEmail.contains("@")) {
            Toast.makeText(this, "Insira um email válido!", Toast.LENGTH_LONG).show();
        } else if (txtCPF.length() < 14) {
            Toast.makeText(this, "Insira um CPF válido!", Toast.LENGTH_LONG).show();
        } else {
            BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());

            if (bd.insereDados(txtNome, txtEmail, txtSenha, txtCPF)) {
                Toast.makeText(this, "Usuário cadastrado.", Toast.LENGTH_LONG).show();

                //salva o usuário para o próximo login
                SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                int userId = bd.buscarUserId(txtEmail);
                editor.putInt("user_id", userId);

                editor.apply();
                Intent it = new Intent(Tela_Cadastro.this, Tela_Mapas.class);
                startActivity(it);
            } else {
                Toast.makeText(this, "Erro ao cadastrar usuário.", Toast.LENGTH_LONG).show();
            }
            limparCampos();
        }
    }
    public void limparCampos(){
        txtCADCPF.setText("");
        txtCADNome.setText("");
        txtCADEmail.setText("");
        txtCADSenha.setText("");
    }
}