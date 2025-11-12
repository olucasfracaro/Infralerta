package com.example.infralerta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BancoControllerUsuarios {
    private SQLiteDatabase db;
    private final CriaBanco banco;

    public BancoControllerUsuarios(Context context) {
        banco = new CriaBanco(context);

    }
    public boolean insereDados(String _nome, String _email, String _senha, String _cpf) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("nome", _nome);
        valores.put("email", _email);
        valores.put("senha", _senha);
        valores.put("cpf", _cpf);

        resultado = db.insert("usuarios", null, valores);
        db.close();

        if (resultado == -1)
            return false;
        else
            return true;
    }

    public String alteraDados(int id, String nome, String email){

        String msg = "Dados alterados com sucesso!!!" ;

        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues() ;
        valores.put("nome" , nome ) ;
        valores.put("email", email) ;

        String condicao = "user_id = " + id;

        int linha ;
        linha = db.update("contatos", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados" ;
        }

        db.close();
        return msg;
    }

    public String excluirDados(String _tabela, int _id){
        String msg = "Registro Excluído com sucesso!" ;

        db = banco.getReadableDatabase();

        String condicao = "user_id = " + _id ;

        int linhas ;
        linhas = db.delete(_tabela, condicao, null) ;

        if ( linhas < 1) {
            msg = "Erro ao Excluir" ;
        }

        db.close();
        return msg;
    }

    public Cursor carregaDadosLogin(String _email, String _senha) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String[] campos = {"user_id", "nome", "email", "senha", "cpf"};
        String where = "email = ? AND senha = ?";
        String[] args = {_email, _senha};

        Cursor cursor = db.query("usuarios", campos, where, args, null, null, null, null);

        return cursor;
    }

    public int buscarUserId(String _email) {
        SQLiteDatabase db = banco.getReadableDatabase();
        int userId = -1; // Valor padrão para indicar 'não encontrado'
        Cursor cursor = null;

        try {
            String[] projection = {"user_id"};
            String selection = "email = ?";
            String[] selectionArgs = {_email};

            cursor = db.query("usuarios", projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("user_id");
                if (columnIndex != -1) { //verifica se a coluna "user_id" existe no cursor
                    userId = cursor.getInt(columnIndex);
                }
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar user_id", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return userId;
    }
}



