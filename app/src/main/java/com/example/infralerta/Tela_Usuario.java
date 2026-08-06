package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tela_Usuario extends AppCompatActivity {
    boolean modoLeitura = true;
    TextView tvUSUNome, tvUSUEmail, tvUSUSenha, tvUSUCPF;
    TextView txtUSUNome, txtUSUEmail, txtUSUCPF;
    TextInputLayout tilUSUNome, tilUSUEmail, tilUSUSenha, tilUSUCPF;
    TextInputEditText inUSUNome, inUSUEmail, inUSUSenha, inUSUCPF;

    FloatingActionButton fabLogout, fabEditar, fabSalvar;

    private String nome;
    private String email;
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        tvUSUNome = findViewById(R.id.tvUSUNome);
        tvUSUEmail = findViewById(R.id.tvUSUEmail);
        tvUSUSenha = findViewById(R.id.tvUSUSenha);
        tvUSUCPF = findViewById(R.id.tvUSUCPF);

        txtUSUNome = findViewById(R.id.txtUSUNome);
        txtUSUEmail = findViewById(R.id.txtUSUEmail);
        txtUSUCPF = findViewById(R.id.txtUSUCPF);

        tilUSUNome = findViewById(R.id.tilUSUNome);
        tilUSUEmail = findViewById(R.id.tilUSUEmail);
        tilUSUSenha = findViewById(R.id.tilUSUSenha);
        tilUSUCPF = findViewById(R.id.tilUSUCPF);

        inUSUNome = findViewById(R.id.inUSUNome);
        inUSUEmail = findViewById(R.id.inUSUEmail);
        inUSUSenha = findViewById(R.id.inUSUSenha);
        inUSUCPF = findViewById(R.id.inUSUCPF);

        inUSUCPF.addTextChangedListener(Tela_Cadastro.cpfWatcher());

        fabLogout = findViewById(R.id.fabLogout);
        fabEditar = findViewById(R.id.fabEditar);
        fabSalvar = findViewById(R.id.fabSalvar);

        fabLogout.setOnClickListener(v -> logout());

        fabEditar.setOnClickListener(v -> {
            modoLeitura = false;
            trocarModoExibicao();
        });

        fabSalvar.setOnClickListener(v -> salvarAlteracoes(userId));

        carregarDadosCache(userId);

        if (userId != -1) {
            carregarDadosSupabase(userId);
        } else {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_LONG).show();
            logout();
        }

        trocarModoExibicao();
    }

    private void carregarDadosCache(int userId) {
        BancoControllerUsuarios bd = new BancoControllerUsuarios(this);
        try (android.database.Cursor dados = bd.carregarDadosUsuario(userId)) {
            if (dados != null && dados.moveToFirst()) {
                nome = dados.getString(dados.getColumnIndexOrThrow("nome"));
                email = dados.getString(dados.getColumnIndexOrThrow("email"));
                cpf = dados.getString(dados.getColumnIndexOrThrow("cpf"));

                txtUSUNome.setText(nome);
                txtUSUEmail.setText(email);
                txtUSUCPF.setText(cpf);
            }
        } catch (Exception e) {
            Log.e("Tela_Usuario", "Erro ao carregar dados do cache local", e);
        }
    }

    private void carregarDadosSupabase(int userId) {
        SupabaseApi api = SupabaseClient.getApi();
        api.getUsuarioPorId(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + userId)
                .enqueue(new Callback<List<Usuario>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Usuario>> call, @NonNull Response<List<Usuario>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Usuario u = response.body().get(0);

                            BancoControllerUsuarios bd = new BancoControllerUsuarios(Tela_Usuario.this);
                            bd.salvarUsuarioLocal(u);
                            
                            nome = u.getNome();
                            email = u.getEmail();
                            cpf = u.getCpf();

                            txtUSUNome.setText(nome);
                            txtUSUEmail.setText(email);
                            txtUSUCPF.setText(cpf);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Usuario>> call, @NonNull Throwable t) {
                        Log.e("Tela_Usuario", "Erro ao carregar do Supabase", t);
                    }
                });
    }

    private void trocarModoExibicao() {
        if (modoLeitura) {
            tvUSUNome.setVisibility(View.VISIBLE);
            tvUSUEmail.setVisibility(View.VISIBLE);
            tvUSUSenha.setVisibility(View.VISIBLE);
            tvUSUCPF.setVisibility(View.VISIBLE);

            txtUSUNome.setVisibility(View.VISIBLE);
            txtUSUEmail.setVisibility(View.VISIBLE);
            txtUSUCPF.setVisibility(View.VISIBLE);

            tilUSUNome.setVisibility(View.GONE);
            tilUSUEmail.setVisibility(View.GONE);
            tilUSUSenha.setVisibility(View.GONE);
            tilUSUCPF.setVisibility(View.GONE);

            fabSalvar.setVisibility(View.GONE);
            fabEditar.setVisibility(View.VISIBLE);
        } else {
            inUSUNome.setText(this.nome);
            inUSUEmail.setText(this.email);
            inUSUCPF.setText(this.cpf);
            inUSUSenha.setText("");

            tvUSUNome.setVisibility(View.GONE);
            tvUSUEmail.setVisibility(View.GONE);
            tvUSUSenha.setVisibility(View.GONE);
            tvUSUCPF.setVisibility(View.GONE);

            txtUSUNome.setVisibility(View.GONE);
            txtUSUEmail.setVisibility(View.GONE);
            txtUSUCPF.setVisibility(View.GONE);

            tilUSUNome.setVisibility(View.VISIBLE);
            tilUSUEmail.setVisibility(View.VISIBLE);
            tilUSUSenha.setVisibility(View.VISIBLE);
            tilUSUCPF.setVisibility(View.VISIBLE);

            fabSalvar.setVisibility(View.VISIBLE);
            fabEditar.setVisibility(View.GONE);
        }
    }

    private void salvarAlteracoes(int userId) {
        String nomeNovo = Objects.requireNonNull(inUSUNome.getText()).toString().trim();
        String emailNovo = Objects.requireNonNull(inUSUEmail.getText()).toString().trim();
        String senhaNova = Objects.requireNonNull(inUSUSenha.getText()).toString();
        String cpfNovo = Objects.requireNonNull(inUSUCPF.getText()).toString();

        if (nomeNovo.isEmpty() || emailNovo.isEmpty() || cpfNovo.isEmpty()) {
            Toast.makeText(this, "Nome, e-mail e CPF são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailNovo).matches()) {
            Toast.makeText(this, "Formato de e-mail inválido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Tela_Cadastro.verificarCPF(cpfNovo)) {
            Toast.makeText(this, "CPF inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean nomeAlterado = !nomeNovo.equals(this.nome);
        boolean emailAlterado = !emailNovo.equals(this.email);
        boolean senhaAlterada = !senhaNova.isEmpty();
        boolean cpfAlterado = !cpfNovo.equals(this.cpf);

        if (!nomeAlterado && !emailAlterado && !senhaAlterada && !cpfAlterado) {
            modoLeitura = true;
            trocarModoExibicao();
            return;
        }

        String senhaParaUpdate = senhaAlterada ? Tela_Cadastro.sha256(senhaNova) : null;
        
        Usuario usuarioUpdate = new Usuario(nomeNovo, emailNovo, senhaParaUpdate, cpfNovo);

        SupabaseApi api = SupabaseClient.getApi();
        api.updateUsuario(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + userId, usuarioUpdate)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Tela_Usuario.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarDadosSupabase(userId);
                            modoLeitura = true;
                            trocarModoExibicao();
                        } else {
                            Toast.makeText(Tela_Usuario.this, "Erro ao alterar os dados no Supabase.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(Tela_Usuario.this, "Falha na conexão.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.apply();

        Intent it = new Intent(Tela_Usuario.this, Tela_Login.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
        finish();
    }
}
