package com.example.infralerta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_Cadastro extends AppCompatActivity implements View.OnClickListener {
    Button btCADcadastro, btCADentrar;
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

        btCADentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Entrar = new Intent(Tela_Cadastro.this , Tela_Login.class);
                startActivity(Entrar);
            }

        });

        btCADcadastro.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (v.getId() == R.id.btCadastrarCad);{
            salvar();
        }
    }

    public void salvar(){
        String msg = "";
        String txtNome = txtCADNome.getText().toString();
        String txtEmail = txtCADEmail.getText().toString();
        String txtSenha = txtCADSenha.getText().toString();
        String txtCPF = txtCADCPF.getText().toString();
        if (txtNome.length()==0 || txtEmail.length()<10)
        {
            msg = "Atenção - Os campos Nome e E-mail devem ser preenchidos!!!";
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
        }else {
            BancoControllerUsuarios bd = new BancoControllerUsuarios(getBaseContext());
            String resultado;

            resultado = bd.insereDados(txtNome, txtEmail, txtSenha, txtCPF);

            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            limpar();
        }

    }
    public void limpar(){
        txtCADCPF.setText("") ;
        txtCADNome.setText("") ;
        txtCADEmail.setText("") ;
        txtCADSenha.setText("");
    }

}