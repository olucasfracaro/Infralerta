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
    public static final String COLUNA_ID_DENUNCIA = "denuncia_id"; // Constante adicionada
    public static final String COLUNA_ID_USUARIO = "user_id";
    public static final String COLUNA_DATA = "data";
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
            valores.put(COLUNA_DATA, denuncia.getData());
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
     * Busca uma denúncia específica no banco de dados com base no ID da denúncia e no ID do usuário.
     * Este método garante que apenas o usuário que criou a denúncia possa acessá-la.
     *
     * @param userId     O ID do usuário que está tentando acessar a denúncia.
     * @param denunciaId O ID da denúncia a ser buscada.
     * @return Um objeto {@link Denuncia} contendo os dados da denúncia encontrada, ou {@code null} se a denúncia não for encontrada,
     *         se o ID do usuário não corresponder, ou se ocorrer um erro durante a consulta.
     */
    public Denuncia buscarDenunciaPorId(int userId, int denunciaId) {
        String[] colunasProjetadas = {COLUNA_ID_USUARIO, COLUNA_DATA, COLUNA_ENDERECO, COLUNA_COORDENADAS, COLUNA_PROBLEMAS, COLUNA_DESCRICAO};
        String clausulaWhere = COLUNA_ID_DENUNCIA + " = ? AND " + COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(denunciaId), String.valueOf(userId)};

        Denuncia denuncia = null;
        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                //pega os índices das colunas uma vez, antes do loop, para melhor performance.
                int indiceData = cursor.getColumnIndexOrThrow(COLUNA_DATA);
                int indiceEndereco = cursor.getColumnIndexOrThrow(COLUNA_ENDERECO);
                int indiceCoordenadas = cursor.getColumnIndexOrThrow(COLUNA_COORDENADAS);
                int indiceProblemas = cursor.getColumnIndexOrThrow(COLUNA_PROBLEMAS);
                int indiceDescricao = cursor.getColumnIndexOrThrow(COLUNA_DESCRICAO);

                String data = cursor.getString(indiceData);
                String endereco = cursor.getString(indiceEndereco);
                String coordenadas = cursor.getString(indiceCoordenadas);
                String problemas = cursor.getString(indiceProblemas);
                String descricao = cursor.getString(indiceDescricao);

                //cria um objeto 'Denuncia' e o retorna
                denuncia = new Denuncia(denunciaId, userId, data, endereco, coordenadas, problemas, descricao);
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncia do usuário.", e);
        }

        return denuncia;
    }

    /**
     * Busca os IDs de todas as denúncias associadas a um ID de usuário específico.
     * @param userId O ID do usuário para o qual buscar as denúncias.
     * @return Uma lista de IDs de denúncias (Integer). A lista estará vazia se nenhuma denúncia for encontrada.
     */
    public ArrayList<Integer> buscarDenunciasPorUserId(int userId) {
        ArrayList<Integer> listaDenuncias = new ArrayList<>();
        String[] colunasProjetadas = {COLUNA_ID_DENUNCIA, COLUNA_DATA, COLUNA_ENDERECO, COLUNA_COORDENADAS, COLUNA_PROBLEMAS, COLUNA_DESCRICAO};
        String clausulaWhere = COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(userId)};

        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int indiceDenunciaId = cursor.getColumnIndexOrThrow(COLUNA_ID_DENUNCIA);

                do {
                    int denunciaId = cursor.getInt(indiceDenunciaId);

                    //adiciona à lista.
                    listaDenuncias.add(denunciaId);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncias do usuário.", e);
        }

        return listaDenuncias;
    }
}
