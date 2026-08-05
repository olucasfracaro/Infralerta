package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tela_Denuncias extends AppCompatActivity {
    LinearLayout llDenuncias;
    FloatingActionButton btMapaDenunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_denuncias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btMapaDenunc = findViewById(R.id.btMapaDenunc);
        btMapaDenunc.setOnClickListener(v -> finish());
        llDenuncias = findViewById(R.id.llDenuncias);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            carregarDenunciasSupabase(userId);
        }
    }

    private void carregarDenunciasSupabase(int userId) {
        SupabaseApi api = SupabaseClient.getApi();
        api.getDenunciasPorUsuario(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + userId)
                .enqueue(new Callback<List<Denuncia>>() {
                    @Override
                    public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ArrayList<Denuncia> denuncias = new ArrayList<>(response.body());
                            exibirDenuncias(denuncias);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                        // Tratar erro
                    }
                });
    }

    private void exibirDenuncias(ArrayList<Denuncia> denuncias) {
        denuncias.sort(Comparator.comparing(Denuncia::getEndereco, String.CASE_INSENSITIVE_ORDER));

        llDenuncias.removeAllViews();

        for (Denuncia denuncia : denuncias) {
            int denunciaId = denuncia.getDenunciaId();
            String data = denuncia.getData();
            String local = denuncia.getEndereco();

            View denunciaView = getLayoutInflater().inflate(R.layout.fragment_denuncia, null);
            TextView txtLocal = denunciaView.findViewById(R.id.txtLocal);
            TextView txtData = denunciaView.findViewById(R.id.txtData);
            ConstraintLayout clDenuncia = denunciaView.findViewById(R.id.clDenuncia);

            txtLocal.setText(local);
            txtData.setText(data);

            llDenuncias.addView(denunciaView);

            clDenuncia.setOnClickListener(v -> {
                Intent it = new Intent(Tela_Denuncias.this, Tela_Especifica.class);
                it.putExtra("denuncia_id", denunciaId);
                startActivity(it);
            });
        }
    }
}