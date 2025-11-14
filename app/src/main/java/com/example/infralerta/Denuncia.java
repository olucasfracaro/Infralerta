package com.example.infralerta;

public class Denuncia {
    private final int userId;
    private final String endereco, coordenadas, problemas, descricao;

    public int getUserId() { return userId; }
    public String getEndereco() { return endereco; }
    public String getCoordenadas() { return coordenadas; }
    public String getProblemas() { return problemas; }
    public String getDescricao() { return descricao; }

    //construtor pra ler do banco
    public Denuncia(String endereco, String coordenadas, String problemas, String descricao) {
        this.userId = 0; //id zerado pq n é lido do banco neste construtor
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }

    //construtor pra criar uma denúncia nova
    public Denuncia(int userId, String endereco, String coordenadas, String problemas, String descricao) {
        this.userId = userId;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }
}
