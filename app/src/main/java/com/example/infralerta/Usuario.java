package com.example.infralerta;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("nome")
    private String nome;

    @SerializedName("email")
    private String email;

    @SerializedName("senha")
    private String senha;

    @SerializedName("cpf")
    private String cpf;

    //construtor vazio para o GSON
    public Usuario() {}

    public Usuario(String nome, String email, String senha, String cpf) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.userId = null; //id não enviado no insert
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getCpf() { return cpf; }
}
