package com.example.infralerta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;

public class BancoControllerDenuncias {
    private SQLiteDatabase db;
    private final CriaBanco banco;
    private final String tabela = "denuncias";
    public static final String COLUNA_ID_USUARIO = "user_id";
    public static final String COLUNA_ENDERECO = "endereco";
    public static final String COLUNA_COORDENADAS = "coordenadas";
    public static final String COLUNA_PROBLEMAS = "problemas";
    public static final String COLUNA_DESCRICAO = "descricao";


    public BancoControllerDenuncias(Context context) {
        banco = new CriaBanco(context);
    }


    public boolean criarDenuncia(Denuncia _denuncia) {
        ContentValues valores;
        long resultado;

        //try-with-resources para gerenciar a conexão automaticamente
        try (SQLiteDatabase db = banco.getWritableDatabase()) {
            valores = new ContentValues();

            valores.put(COLUNA_ID_USUARIO, _denuncia.getUserId());

            valores.put(COLUNA_ENDERECO, _denuncia.getEndereco());
            valores.put(COLUNA_COORDENADAS, _denuncia.getCoordenadas());
            valores.put(COLUNA_PROBLEMAS, _denuncia.getProblemas());
            valores.put(COLUNA_DESCRICAO, _denuncia.getDescricao());

            resultado = db.insert(tabela, null, valores);
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao criar denúncia", e);
            resultado = -1;
        }

        return resultado != -1;
    }


    public ArrayList<Denuncia> buscarDenunciasPorUserId(int userId) {
        ArrayList<Denuncia> denuncias = new ArrayList<>();
        String[] projection = {COLUNA_ENDERECO, COLUNA_COORDENADAS, COLUNA_PROBLEMAS, COLUNA_DESCRICAO};
        String selection = COLUNA_ID_USUARIO + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        try (SQLiteDatabase db = banco.getReadableDatabase();
             Cursor cursor = db.query(tabela, projection, selection, selectionArgs, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                //pega os índices das colunas uma vez antes do loop para melhor performance
                int indexEndereco = cursor.getColumnIndexOrThrow(COLUNA_ENDERECO);
                int indexCoordenadas = cursor.getColumnIndexOrThrow(COLUNA_COORDENADAS);
                int indexProblemas = cursor.getColumnIndexOrThrow(COLUNA_PROBLEMAS);
                int indexDescricao = cursor.getColumnIndexOrThrow(COLUNA_DESCRICAO);

                do {
                    //lê os dados do cursor usando os índices obtidos
                    String endereco = cursor.getString(indexEndereco);
                    String coordenadas = cursor.getString(indexCoordenadas);
                    String problemas = cursor.getString(indexProblemas);
                    String descricao = cursor.getString(indexDescricao);

                    Denuncia denuncia = new Denuncia(endereco, coordenadas, problemas, descricao);
                    denuncias.add(denuncia);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncias por usuário.", e);
        }

        return denuncias;
    }
}



