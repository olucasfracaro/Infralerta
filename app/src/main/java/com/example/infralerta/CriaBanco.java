package com.example.infralerta;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco_infra.db";
    private static final int VERSAO = 1;
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
                + "denuncia text,"
                + "descricao text,"
                + "FOREIGN KEY(user_id) REFERENCES usuarios(user_id))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}

//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class CriaBanco extends SQLiteOpenHelper {
//
//    private static final String NOME_BANCO = "banco_exemplo.db";
//    private static final int VERSAO = 2;
//    public CriaBanco(Context context) {
//        super(context, NOME_BANCO, null, VERSAO);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String sql = "CREATE TABLE contatos ("
//                + "codigo integer primary key autoincrement,"
//                + "nome text,"
//                + "email text)";
//        db.execSQL(sql);
//
//        sql = "CREATE TABLE usuarios ("
//                + "codigo integer primary key autoincrement,"
//                + "nome text,"
//                + "email text,"
//                + "senha text,"
//                + "cpf  text)";
//        db.execSQL(sql);
//
//        sql = "INSERT INTO usuarios (nome, email, senha, cpf) VALUES " +
//                "('Admin','admin@teste.com','123456','123.456.789-00')";
//        db.execSQL(sql);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS contatos");
//        db.execSQL("DROP TABLE IF EXISTS usuarios");
//        onCreate(db);
//    }
//}
