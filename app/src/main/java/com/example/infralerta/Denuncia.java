package com.example.infralerta;

public class Denuncia {
    private final int denunciaId;
    private final int userId;
    private final String data, endereco, coordenadas, problemas, descricao;
    public int getDenunciaId() { return denunciaId; }
    public int getUserId() { return userId; }
    public String getData() { return data; }
    public String getEndereco() { return endereco; }
    public String getCoordenadas() { return coordenadas; }
    public String getProblemas() { return problemas; }
    public String getDescricao() { return descricao; }

    // Construtor pra ler do banco
    public Denuncia(int denunciaId, int userId, String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.denunciaId = denunciaId;
        this.userId = userId;
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }

    // Construtor pra criar uma denúncia nova (denunciaId é gerado pelo banco, então pode ser 0)
    public Denuncia(int userId, String data, String endereco, String coordenadas, String problemas, String descricao) {
        this.denunciaId = 0; // ID zerado pois será gerado pelo banco na inserção
        this.userId = userId;
        this.data = data;
        this.endereco = endereco;
        this.coordenadas = coordenadas;
        this.problemas = problemas;
        this.descricao = descricao;
    }
}
