package com.example.infralerta;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {
    //buscar todos os registros
    @GET("/rest/v1/denuncias?select=*")
    Call<List<Denuncia>> getDenuncias(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token
    );

    //buscar denúncias de um usuário específico
    @GET("/rest/v1/denuncias?select=*")
    Call<List<Denuncia>> getDenunciasPorUsuario(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("user_id") String userIdFilter
    );

    //buscar uma denúncia específica por ID
    @GET("/rest/v1/denuncias?select=*")
    Call<List<Denuncia>> getDenunciaPorId(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("denuncia_id") String denunciaIdFilter
    );

    //inserir um novo registro
    @POST("/rest/v1/denuncias")
    Call<Void> insertDenuncia(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Denuncia denuncia
    );

    //inserir um novo usuário e retornar os dados inseridos (incluindo o ID gerado)
    @POST("/rest/v1/usuarios")
    @Headers("Prefer: return=representation")
    Call<List<Usuario>> insertUsuario(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body Usuario usuario
    );

    //buscar usuário por email e senha (para login)
    @GET("/rest/v1/usuarios?select=*")
    Call<List<Usuario>> getUsuarioLogin(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("email") String email,
            @Query("senha") String senha
    );

    //buscar usuário por ID
    @GET("/rest/v1/usuarios?select=*")
    Call<List<Usuario>> getUsuarioPorId(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("user_id") String userIdFilter
    );

    //atualizar dados do usuário
    @PATCH("/rest/v1/usuarios")
    Call<Void> updateUsuario(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("user_id") String userIdFilter,
            @Body Usuario usuario
    );

    //upload de imagem ao Supabase Storage
    @POST("storage/v1/object/imagens_denuncias/{path}")
    Call<ResponseBody> uploadImagem(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Content-Type") String contentType,
            @Path("path") String fileName,
            @Body RequestBody file
    );
}