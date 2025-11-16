package com.example.infralerta;

public class Denuncia {
    private final int userId;
    private final String data, endereco, coordenadas, problemas, descricao;

    public String getData() { return data; }
    public int getUserId() { return userId; }
    public String getEndereco() { return endereco; }
    public String getCoordenadas() { return coordenadas; }
    public String getProblemas() { return problemas; }
    public String getDescricao() { return descricao; }

    //construtor pra ler do banco
    public Denuncia(String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.userId = 0; //id zerado pq n é lido do banco neste construtor
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }

    //construtor pra criar uma denúncia nova
    public Denuncia(int userId, String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.userId = userId;
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }
}
