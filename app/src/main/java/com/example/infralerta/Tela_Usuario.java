package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());
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

        fabSalvar.setOnClickListener(v -> {
            salvarAlteracoes(bd, userId);
        });

        // Carrega os dados do usuário e atualiza a UI
        if (userId != -1) {
            carregarDados(bd, userId);
        } else {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_LONG).show();
            logout(); // Se não há ID, desloga por segurança
        }

        // Garante que a tela inicie no modo de leitura
        trocarModoExibicao();
    }

    private void carregarDados(BancoControllerUsuarios bd, int userId) {
        try (Cursor dados = bd.carregarDadosUsuario(userId)) {
            if (dados != null && dados.moveToFirst()) {
                this.nome = dados.getString(dados.getColumnIndexOrThrow("nome"));
                this.email = dados.getString(dados.getColumnIndexOrThrow("email"));
                this.cpf = dados.getString(dados.getColumnIndexOrThrow("cpf"));

                // Atualiza os TextViews do modo leitura
                txtUSUNome.setText(this.nome);
                txtUSUEmail.setText(this.email);
                txtUSUCPF.setText(this.cpf);

            } else {
                Toast.makeText(this, "Erro ao carregar os dados do usuário.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ocorreu um erro ao buscar os dados.", Toast.LENGTH_SHORT).show();
            Log.e("Tela_Usuario", "Erro ao carregar dados do banco.", e);
        }
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

            // Controla os botões
            fabSalvar.setVisibility(View.VISIBLE);
            fabEditar.setVisibility(View.GONE);
        }
    }

    private void salvarAlteracoes(BancoControllerUsuarios bd, int userId) {
        String nomeNovo = inUSUNome.getText().toString().trim();
        String emailNovo = inUSUEmail.getText().toString().trim();
        String senhaNova = inUSUSenha.getText().toString(); //senha em texto puro
        String cpfNovo = inUSUCPF.getText().toString();

        if (nomeNovo.isEmpty() || emailNovo.isEmpty() || cpfNovo.isEmpty()) {
            Toast.makeText(this, "Nome, e-mail e CPF são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailNovo).matches()) {
            Toast.makeText(this, "Formato de e-mail inválido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Tela_Cadastro.verificarCPF(inUSUCPF.getText().toString())) {
            Toast.makeText(this, "CPF inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean nomeAlterado = !nomeNovo.equals(this.nome);
        boolean emailAlterado = !emailNovo.equals(this.email);
        boolean senhaAlterada = !senhaNova.isEmpty(); //senha é alterada se não estiver vazia
        boolean cpfAlterado = !cpfNovo.equals(this.cpf);

        //se nada foi alterado, apenas volta para o modo de leitura
        if (!nomeAlterado && !emailAlterado && !senhaAlterada && !cpfAlterado) {
            modoLeitura = true;
            trocarModoExibicao();
            //Toast.makeText(this, "Nenhuma alteração foi feita.", Toast.LENGTH_SHORT).show();
            return;
        }

        //prepara os dados para o update (usa null se o campo não foi alterado)
        String nomeParaUpdate = nomeAlterado ? nomeNovo : null;
        String emailParaUpdate = emailAlterado ? emailNovo : null;
        String senhaParaUpdate = senhaAlterada ? Tela_Cadastro.sha256(senhaNova) : null;
        String cpfParaUpdate = cpfAlterado ? cpfNovo : null;

        if (bd.alterarUsuario(userId, nomeParaUpdate, emailParaUpdate, senhaParaUpdate, cpfParaUpdate)) {
            Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();

            //recarrega os dados atualizados do banco
            carregarDados(bd, userId);

            //volta para o modo de leitura
            modoLeitura = true;
            trocarModoExibicao();
        } else {
            Toast.makeText(this, "Erro ao alterar os dados.", Toast.LENGTH_SHORT).show();
        }
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
