package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tela_Usuario extends AppCompatActivity {
    boolean modoLeitura = true;
    TextView txtUSUNome, txtUSUEmail, txtUSUSenha, txtUSUCPF;
    EditText inUSUNome, inUSUEmail, inUSUSenha, inUSUCPF;
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
        //pega os dados básicos
        BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        txtUSUNome = findViewById(R.id.txtUSUNome);
        txtUSUEmail = findViewById(R.id.txtUSUEmail);
        txtUSUSenha = findViewById(R.id.txtUSUSenha);
        txtUSUCPF = findViewById(R.id.txtUSUCPF);

        inUSUNome = findViewById(R.id.inUSUNome);
        inUSUEmail = findViewById(R.id.inUSUEmail);
        inUSUSenha = findViewById(R.id.inUSUSenha);
        inUSUCPF = findViewById(R.id.inUSUCPF);

        inUSUCPF.addTextChangedListener(Tela_Cadastro.cpfWatcher());

        fabLogout = findViewById(R.id.fabLogout);
        fabLogout.setOnClickListener(v -> logout());

        fabEditar = findViewById(R.id.fabEditar);
        fabEditar.setOnClickListener(v -> {
            modoLeitura = false;
            trocarModoLeitura();
        });

        fabSalvar = findViewById(R.id.fabSalvar);
        fabSalvar.setOnClickListener(v -> salvarAlteracoes(bd, userId));

        //pega os dados do usuário com o user_id
        carregarDados(bd, userId);
    }

    private void carregarDados(BancoControllerUsuarios bd, int userId) {
        try (Cursor dados = bd.carregarDadosUsuario(userId)) {
            if (dados != null && dados.moveToFirst()) {
                this.nome = dados.getString(dados.getColumnIndexOrThrow("nome"));
                this.email = dados.getString(dados.getColumnIndexOrThrow("email"));
                this.cpf = dados.getString(dados.getColumnIndexOrThrow("cpf"));

                txtUSUNome.setText(this.nome);
                txtUSUEmail.setText(this.email);
                txtUSUCPF.setText(this.cpf);
            } else {
                Toast.makeText(this, "Erro ao carregar os dados do usuário", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //qualquer outra exceção que possa ocorrer
            Toast.makeText(this, "Ocorreu um erro durante a busca.", Toast.LENGTH_SHORT).show();
            Log.e("Tela_Login", "Erro ao verificar dados da busca", e);
        }
    }

    private void trocarModoLeitura() {
        if (modoLeitura) { //leitura
            txtUSUNome.setText(this.nome);
            txtUSUEmail.setText(this.email);
            txtUSUCPF.setText(this.cpf);

            txtUSUNome.setVisibility(View.VISIBLE);
            txtUSUEmail.setVisibility(View.VISIBLE);
            txtUSUSenha.setVisibility(View.VISIBLE);
            txtUSUCPF.setVisibility(View.VISIBLE);

            inUSUNome.setVisibility(View.GONE);
            inUSUEmail.setVisibility(View.GONE);
            inUSUSenha.setVisibility(View.GONE);
            inUSUCPF.setVisibility(View.GONE);

            fabSalvar.setVisibility(View.GONE);
            fabEditar.setVisibility(View.VISIBLE);
        } else { //edição
            inUSUNome.setText(this.nome);
            inUSUEmail.setText(this.email);
            inUSUCPF.setText(this.cpf);

            txtUSUNome.setVisibility(View.GONE);
            txtUSUEmail.setVisibility(View.GONE);
            txtUSUSenha.setVisibility(View.GONE);
            txtUSUCPF.setVisibility(View.GONE);

            inUSUNome.setVisibility(View.VISIBLE);
            inUSUEmail.setVisibility(View.VISIBLE);
            inUSUSenha.setVisibility(View.VISIBLE);
            inUSUCPF.setVisibility(View.VISIBLE);

            fabSalvar.setVisibility(View.VISIBLE);
            fabEditar.setVisibility(View.GONE);
        }
    }

    private void salvarAlteracoes(BancoControllerUsuarios bd, int userId) {
        if (inUSUNome.getText().toString().trim().isEmpty()
            || inUSUEmail.getText().toString().trim().isEmpty()
            || inUSUCPF.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inUSUEmail.getText().toString()).matches()) {
            Toast.makeText(this, "E-mail inválido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (inUSUCPF.getText().toString().length() < 14) {
            Toast.makeText(this, "CPF inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nomeNovo = inUSUNome.getText().toString().trim();
        String emailNovo = inUSUEmail.getText().toString().trim();
        String senhaNova = inUSUSenha.getText().toString(); //senha em texto puro
        String cpfNovo = inUSUCPF.getText().toString();

        //verifica se houve alguma alteração real
        boolean nomeAlterado = !nomeNovo.equals(this.nome);
        boolean emailAlterado = !emailNovo.equals(this.email);
        //a senha é considerada "alterada" apenas se o campo não estiver vazio.
        boolean senhaAlterada = !senhaNova.isEmpty();
        boolean cpfAlterado = !cpfNovo.equals(this.cpf);

        //verifica se nada foi alterado
        if (!nomeAlterado && !emailAlterado && !senhaAlterada && !cpfAlterado) {
            modoLeitura = true;
            trocarModoLeitura();
            return;
        }

        //prepara os dados para o update. null se inalterado
        String nomeParaUpdate = nomeAlterado ? nomeNovo : null;
        String emailParaUpdate = emailAlterado ? emailNovo : null;
        String senhaParaUpdate = senhaAlterada ? Tela_Cadastro.sha256(senhaNova) : null; //envia a senha em texto puro
        String cpfParaUpdate = cpfAlterado ? cpfNovo : null;


        if (bd.alterarUsuario(userId, nomeParaUpdate, emailParaUpdate, senhaParaUpdate, cpfParaUpdate)) {
            Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
            //recarrega os dados e volta para o modo de leitura
            carregarDados(bd, userId);
            modoLeitura = true;
            trocarModoLeitura();
            finish();
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
    }
}