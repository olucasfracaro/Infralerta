package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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

        txtCADCPF.addTextChangedListener(new TextWatcher() {
            boolean atualizando = false;
            final String mascara = "###.###.###-##";

            @Override
            public void beforeTextChanged(CharSequence s, int inicio, int antes, int depois) {}

            @Override
            public void onTextChanged(CharSequence texto, int inicio, int antes, int quantidade) {
                if (atualizando) return;

                // Remove tudo que não for número
                String somenteNumeros = texto.toString().replaceAll("[^0-9]", "");
                StringBuilder textoMascarado = new StringBuilder();

                int i = 0;
                for (char m : mascara.toCharArray()) {
                    if (m != '#' && somenteNumeros.length() > i) {
                        textoMascarado.append(m);
                    } else {
                        try {
                            textoMascarado.append(somenteNumeros.charAt(i));
                            i++;
                        } catch (Exception e) {
                            break;
                        }
                    }
                }

                atualizando = true;
                txtCADCPF.setText(textoMascarado.toString());
                txtCADCPF.setSelection(textoMascarado.length());
                atualizando = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

            String txtSenhaHash = sha256(txtSenha);

            if (bd.criarUsuario(txtNome, txtEmail, txtSenhaHash, txtCPF)) {
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

    public static String sha256(String input) {
        try {
            //cria o MessageDigest e depois calcula o hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            //converte o hash em uma string hexadecimal
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void limparCampos(){
        txtCADCPF.setText("");
        txtCADNome.setText("");
        txtCADEmail.setText("");
        txtCADSenha.setText("");
    }
}