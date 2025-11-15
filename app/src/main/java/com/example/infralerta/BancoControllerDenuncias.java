package com.example.infralerta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;

/**
 * Controller responsável por todas as operações de banco de dados
 * relacionadas à tabela de denúncias.
 */
public class BancoControllerDenuncias {

    private final CriaBanco bancoHelper;

    //constantes para nomes de tabela e colunas
    private static final String TABELA_DENUNCIAS = "denuncias";
    public static final String COLUNA_ID_USUARIO = "user_id";
    public static final String COLUNA_ENDERECO = "endereco";
    public static final String COLUNA_COORDENADAS = "coordenadas";
    public static final String COLUNA_PROBLEMAS = "problemas";
    public static final String COLUNA_DESCRICAO = "descricao";

    public BancoControllerDenuncias(Context contexto) {
        bancoHelper = new CriaBanco(contexto);
    }

    /**
     * Cria uma nova denúncia no banco de dados.
     * @param denuncia O objeto Denuncia contendo todos os dados a serem inseridos.
     * @return {@code true} se a inserção for bem-sucedida, {@code false} caso contrário.
     */
    public boolean criarDenuncia(Denuncia denuncia) {
        long resultado;

        // 'try-with-resources' garante que a conexão com o banco (db) seja fechada automaticamente.
        try (SQLiteDatabase db = bancoHelper.getWritableDatabase()) {
            ContentValues valores = new ContentValues();

            valores.put(COLUNA_ID_USUARIO, denuncia.getUserId());
            valores.put(COLUNA_ENDERECO, denuncia.getEndereco());
            valores.put(COLUNA_COORDENADAS, denuncia.getCoordenadas());
            valores.put(COLUNA_PROBLEMAS, denuncia.getProblemas());
            valores.put(COLUNA_DESCRICAO, denuncia.getDescricao());

            resultado = db.insert(TABELA_DENUNCIAS, null, valores);
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao criar denúncia", e);
            resultado = -1; //erro
        }

        return resultado != -1;
    }

    /**
     * Busca todas as denúncias associadas a um ID de usuário específico.
     * @param userId O ID do usuário para buscar as denúncias.
     * @return Uma lista de objetos Denuncia. A lista estará vazia se nenhum resultado for encontrado.
     */
    public ArrayList<Denuncia> buscarDenunciasPorUserId(int userId) {
        ArrayList<Denuncia> listaDenuncias = new ArrayList<>();
        String[] colunasProjetadas = {COLUNA_ENDERECO, COLUNA_COORDENADAS, COLUNA_PROBLEMAS, COLUNA_DESCRICAO};
        String clausulaWhere = COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(userId)};

        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                // Pega os índices das colunas uma vez, antes do loop, para melhor performance.
                int indiceEndereco = cursor.getColumnIndexOrThrow(COLUNA_ENDERECO);
                int indiceCoordenadas = cursor.getColumnIndexOrThrow(COLUNA_COORDENADAS);
                int indiceProblemas = cursor.getColumnIndexOrThrow(COLUNA_PROBLEMAS);
                int indiceDescricao = cursor.getColumnIndexOrThrow(COLUNA_DESCRICAO);

                do {
                    //lê os dados do cursor usando os índices obtidos.
                    String endereco = cursor.getString(indiceEndereco);
                    String coordenadas = cursor.getString(indiceCoordenadas);
                    String problemas = cursor.getString(indiceProblemas);
                    String descricao = cursor.getString(indiceDescricao);

                    //cria um objeto 'Denuncia' e o adiciona à lista.
                    Denuncia denuncia = new Denuncia(endereco, coordenadas, problemas, descricao);
                    listaDenuncias.add(denuncia);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncias por usuário.", e);
        }

        return listaDenuncias;
    }
}
