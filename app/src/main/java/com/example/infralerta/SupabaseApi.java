package com.example.infralerta;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseApi {
    // Buscar todos os registros
    @GET("denuncias?select=*")
    Call<List<Denuncia>> getDenuncias(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token
    );

    // Inserir um novo registro
    @POST("denuncias")
    Call<Void> insertDenuncia(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Denuncia denuncia
    );
}