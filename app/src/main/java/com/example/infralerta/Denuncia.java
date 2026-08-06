package com.example.infralerta;

import com.google.gson.annotations.SerializedName;

public class Denuncia {
    @SerializedName("denuncia_id")
    private Integer denunciaId;

    @SerializedName("user_id")
    private final int userId;

    @SerializedName("data")
    private final String data;

    @SerializedName("endereco")
    private final String endereco;

    @SerializedName("coordenadas")
    private final String coordenadas;

    @SerializedName("problemas")
    private final String problemas;

    @SerializedName("descricao")
    private final String descricao;

    @SerializedName("caminho_imagem")
    private String caminhoImagem;

    public int getDenunciaId() { return denunciaId; }
    public int getUserId() { return userId; }
    public String getData() { return data; }
    public String getEndereco() { return endereco; }
    public String getCoordenadas() { return coordenadas; }
    public String getProblemas() { return problemas; }
    public String getDescricao() { return descricao; }

    public String getCaminhoImagem() { return caminhoImagem; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }

    //construtor para RECUPERAR uma denúncia do banco
    public Denuncia(int denunciaId, int userId, String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.denunciaId = denunciaId;
        this.userId = userId;
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }


    //construtor pra criar uma denúncia nova (denunciaId é nulo para o banco gerar automaticamente)
    public Denuncia(int userId, String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.denunciaId = null;
        this.userId = userId;
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }
}
