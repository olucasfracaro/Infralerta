package com.example.infralerta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BancoControllerDenuncias {
    private SQLiteDatabase db;
    private final CriaBanco banco;
    private final String tabela = "denuncias";

    public BancoControllerDenuncias(Context context) {
        banco = new CriaBanco(context);
    }


    public boolean criarDenuncia(String _endereco, String _coordenadas, String _denuncia, String _descricao) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("endereco", _endereco);
        valores.put("coordenadas", _coordenadas);
        valores.put("denuncia", _denuncia);
        valores.put("descricao", _descricao);

        resultado = db.insert(tabela, null, valores);
        db.close();

        if (resultado == -1)
            return false;
        else
            return true;
    }

    public int buscarUserId(String _email) {
        SQLiteDatabase db = banco.getReadableDatabase();
        int userId = -1; // Valor padrão para indicar 'não encontrado'
        Cursor cursor = null;

        try {
            String[] projection = {"user_id"};
            String selection = "email = ?";
            String[] selectionArgs = {_email};

            cursor = db.query(tabela, projection, selection, selectionArgs, null, null, null);

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



