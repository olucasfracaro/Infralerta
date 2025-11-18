package com.example.infralerta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "banco_infra.db";
    /*
    * VERSAO 1: Inicial
    * VERSAO 2: Sistema de Login
    * VERSAO 3: Atualização das Denúncias
    * VERSAO 4: Hashing das senhas
    * VERSAO 5: Registro de data
    * VERSAO 6: Caminho de imagem
    */
    private static final int VERSAO = 6;
    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE usuarios ("
                + "user_id integer primary key autoincrement,"
                + "nome text,"
                + "email text,"
                + "senha text," //SHA-256
                + "cpf text)";
        db.execSQL(sql);

        sql = "CREATE TABLE denuncias ("
                + "denuncia_id integer primary key autoincrement,"
                + "user_id integer,"
                + "data DATE,"
                + "endereco text,"
                + "coordenadas text,"
                + "problemas text,"
                + "descricao text,"
                + "caminho_imagem text,"
                + "FOREIGN KEY(user_id) REFERENCES usuarios(user_id))";
        db.execSQL(sql);

        /* OBSOLETO desde a validação de CPF
        db.execSQL("INSERT INTO usuarios (nome, email, senha, cpf) " +
                    "VALUES ('Administrador','admin@infralerta.com','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','000.000.000-00')");
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS denuncias");
        onCreate(db);
    }
}