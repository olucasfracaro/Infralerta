package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        String email = txtEmailLogin.getText().toString().trim();
        String senhaInserida = txtSenhaLogin.getText().toString();

        if (email.isEmpty() || senhaInserida.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String senhaInseridaHash = Tela_Cadastro.sha256(senhaInserida);

        SupabaseApi api = SupabaseClient.getApi();
        api.getUsuarioLogin(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + email, "eq." + senhaInseridaHash)
                .enqueue(new Callback<List<Usuario>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Usuario>> call, @NonNull Response<List<Usuario>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            //sucesso no login
                            Usuario usuario = response.body().get(0);
                            Integer user_id = usuario.getUserId();

                            BancoControllerUsuarios bd = new BancoControllerUsuarios(Tela_Login.this);
                            bd.salvarUsuarioLocal(usuario);

                            //user_id nas prefs
                            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            if (user_id != null) {
                                editor.putInt("user_id", user_id);
                            }
                            editor.apply();

                            txtEmailLogin.setText("");
                            txtSenhaLogin.setText("");
                            Intent it = new Intent(Tela_Login.this, Tela_Mapas.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                            finish();
                        } else {
                            //login falhou/usuário não encontrado
                            Toast.makeText(Tela_Login.this, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Usuario>> call, @NonNull Throwable t) {
                        Toast.makeText(Tela_Login.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Tela_Login", "Erro ao verificar dados de login", t);
                    }
                });
    }

}