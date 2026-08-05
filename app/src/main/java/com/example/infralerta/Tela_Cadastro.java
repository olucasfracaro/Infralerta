package com.example.infralerta;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        txtCADCPF.addTextChangedListener(cpfWatcher());

        //tirei o Intent para otimizar o código
        btCADentrar.setOnClickListener(v -> finish());

        btCADcadastro.setOnClickListener(v -> cadastrar());
    }

    @NonNull
    public static TextWatcher cpfWatcher() {
        return new TextWatcher() {
            private boolean estaAtualizando = false;
            private final String mascara = "###.###.###-##";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (estaAtualizando) {
                    return;
                }

                estaAtualizando = true;

                //remove tudo que não for dígito
                String somenteDigitos = s.toString().replaceAll("\\D", "");
                StringBuilder textoMascarado = new StringBuilder();

                int i = 0;
                for (char m : mascara.toCharArray()) {
                    if (m != '#' && i < somenteDigitos.length()) {
                        textoMascarado.append(m);
                        continue;
                    }
                    try {
                        textoMascarado.append(somenteDigitos.charAt(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                //remove o listener temporariamente para evitar loops
                s.replace(0, s.length(), textoMascarado.toString());

                estaAtualizando = false;
            }
        };
    }

    public static boolean verificarCPF(String _cpf) {
        //remove a pontuação e verifica condições básicas
        String digitos = _cpf.replaceAll("\\D", "");

        if (digitos.length() != 11 || digitos.matches("(\\d)\\1{10}")) {
            return false; //se não tiver 11 dígitos ou se todos forem iguais
        }

        try {
            //cálculo do primeiro dígito verificador
            int soma = 0;
            //multiplica os 9 primeiros dígitos pela sequência decrescente de 10 a 2
            for (int i = 0; i < 9; i++) {
                soma += (digitos.charAt(i) - '0') * (10 - i);
            }

            int resto = soma % 11;
            int digitoVerificador1 = (resto < 2) ? 0 : (11 - resto);

            //compara o dígito calculado com o dígito real (o 10º dígito do CPF)
            if (digitoVerificador1 != (digitos.charAt(9) - '0')) {
                return false;
            }

            //cálculo do segundo dígito verificador
            soma = 0;
            //multiplica os 10 primeiros dígitos (incluindo o primeiro dígito verificador) pela sequência decrescente de 11 a 2
            for (int i = 0; i < 10; i++) {
                soma += (digitos.charAt(i) - '0') * (11 - i);
            }

            resto = soma % 11;
            int digitoVerificador2 = (resto < 2) ? 0 : (11 - resto);

            //compara o segundo dígito calculado com o dígito real (o 11º dígito do CPF)
            if (digitoVerificador2 != (digitos.charAt(10) - '0')) {
                return false;
            }

        } catch (NumberFormatException e) {
            return false; //se algo der errado na conversão, o cpf é inválido
        }

        //se passou por todas as verificações, o CPF é válido.
        return true;
    }

    public void cadastrar() {
        String txtNome = txtCADNome.getText().toString().trim();
        String txtEmail = txtCADEmail.getText().toString().trim();
        String txtSenha = txtCADSenha.getText().toString();
        String txtCPF = txtCADCPF.getText().toString();

        if (txtNome.isEmpty() || txtEmail.isEmpty() || txtSenha.isEmpty() || txtCPF.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
            Toast.makeText(this, "Insira um email válido!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!verificarCPF(txtCPF)) {
            Toast.makeText(this, "Insira um CPF válido!", Toast.LENGTH_LONG).show();
            return;
        }

        String txtSenhaHash = sha256(txtSenha);

        Usuario novoUsuario = new Usuario(txtNome, txtEmail, txtSenhaHash, txtCPF);

        SupabaseApi api = SupabaseClient.getApi();
        api.insertUsuario(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, novoUsuario)
                .enqueue(new Callback<List<Usuario>>() {
                    @Override
                    public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Usuario usuarioCriado = response.body().get(0);
                            Integer userId = usuarioCriado.getUserId();

                            Toast.makeText(Tela_Cadastro.this, "Usuário cadastrado com sucesso.", Toast.LENGTH_LONG).show();

                            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            if (userId != null) {
                                editor.putInt("user_id", userId);
                            }
                            editor.apply();

                            Intent it = new Intent(Tela_Cadastro.this, Tela_Mapas.class);
                            it.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                            finish();
                        } else {
                            String errorMsg = "Erro ao cadastrar: " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(Tela_Cadastro.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Usuario>> call, Throwable t) {
                        Toast.makeText(Tela_Cadastro.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
