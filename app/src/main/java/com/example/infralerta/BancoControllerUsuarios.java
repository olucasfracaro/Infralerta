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
    public boolean criarUsuario(String _nome, String _email, String _senha, String _cpf) {
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

    public boolean alterarUsuario(int _id, String _nome, String _email, String _senha, String _cpf) {
        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues();
        if (_nome != null) valores.put("nome" , _nome);
        if (_email != null) valores.put("email", _email);
        if (_senha != null) valores.put("senha", _senha);
        if (_cpf != null) valores.put("cpf", _cpf);

        String condicao = "user_id = " + _id;

        int linha;
        linha = db.update("contatos", valores, condicao, null);

        if (linha < 1){
            return false;
        }

        db.close();
        return true;
    }

    public boolean excluirUsuario(String _tabela, int _id) {
        db = banco.getReadableDatabase();

        String condicao = "user_id = " + _id ;

        int linhas ;
        linhas = db.delete(_tabela, condicao, null) ;

        if (linhas < 1) {
            return false;
        }

        db.close();
        return true;
    }

    public Cursor carregarDadosLogin(String _email, String _senha) {
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



