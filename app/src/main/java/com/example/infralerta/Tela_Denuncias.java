package com.example.infralerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
            carregarDenunciasCache(userId);

            carregarDenunciasSupabase(userId);
        }
    }

    private void carregarDenunciasCache(int userId) {
        BancoControllerDenuncias bd = new BancoControllerDenuncias(this);
        ArrayList<Denuncia> denunciasCache = bd.buscarTodasDenunciasCache(userId);
        if (!denunciasCache.isEmpty()) {
            exibirDenuncias(denunciasCache);
        }
    }

    private void carregarDenunciasSupabase(int userId) {
        SupabaseApi api = SupabaseClient.getApi();
        api.getDenunciasPorUsuario(SupabaseClient.ANON_KEY, "Bearer " + SupabaseClient.ANON_KEY, "eq." + userId)
                .enqueue(new Callback<List<Denuncia>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Denuncia>> call, @NonNull Response<List<Denuncia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ArrayList<Denuncia> denuncias = new ArrayList<>(response.body());
                            
                            //atualiza o cache local com os novos dados (incluindo remoção de deletados)
                            BancoControllerDenuncias bd = new BancoControllerDenuncias(Tela_Denuncias.this);
                            bd.sincronizarCache(userId, denuncias);
                            
                            //atualiza a tela com os dados mais recentes
                            exibirDenuncias(denuncias);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Denuncia>> call, @NonNull Throwable t) {
                        Log.e("Tela_Denuncias", "Erro ao carregar do Supabase", t);
                    }
                });
    }

    private void exibirDenuncias(ArrayList<Denuncia> denuncias) {
        //ordenar pelo ID (mais recentes primeiro)
        denuncias.sort((d1, d2) -> Integer.compare(d2.getDenunciaId(), d1.getDenunciaId()));

        llDenuncias.removeAllViews();

        for (Denuncia denuncia : denuncias) {
            int denunciaId = denuncia.getDenunciaId();
            String data = denuncia.getData();
            //formatar de YYYY-MM-DD -> DD/MM/YYYY
            data = String.format("%s/%s/%s", data.substring(8, 10), data.substring(5, 7), data.substring(0, 4));
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