package com.example.infralerta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "banco_infra.db";
    private static final int VERSAO = 3;
    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE usuarios ("
                + "user_id integer primary key autoincrement,"
                + "nome text,"
                + "email text,"
                + "senha text,"
                + "cpf text)";
        db.execSQL(sql);

        sql = "CREATE TABLE denuncias ("
                +"denuncia_id integer primary key autoincrement,"
                +"user_id integer,"
                +"endereco text,"
                + "coordenadas text,"
                + "problemas text,"
                + "descricao text,"
                + "FOREIGN KEY(user_id) REFERENCES usuarios(user_id))";
        db.execSQL(sql);

        db.execSQL("INSERT INTO usuarios (nome, email, senha, cpf) " +
                    "VALUES ('Administrador','admin@infralerta.com','admin','000.000.000-00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS denuncias");
        onCreate(db);
    }
}