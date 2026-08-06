package com.example.infralerta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsável por todas as operações de banco de dados
 * relacionadas à tabela de denúncias.
 */
public class BancoControllerDenuncias {

    private final CriaBanco bancoHelper;

    //constantes para nomes de tabela e colunas
    private static final String TABELA_DENUNCIAS = "denuncias";
    public static final String COLUNA_ID_DENUNCIA = "denuncia_id";
    public static final String COLUNA_ID_USUARIO = "user_id";
    public static final String COLUNA_DATA = "data";
    public static final String COLUNA_ENDERECO = "endereco";
    public static final String COLUNA_COORDENADAS = "coordenadas";
    public static final String COLUNA_PROBLEMAS = "problemas";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_CAMINHO_IMAGEM = "caminho_imagem";

    public BancoControllerDenuncias(Context contexto) {
        bancoHelper = new CriaBanco(contexto);
    }

    /**
     * Sincroniza uma lista de denúncias vindas do Supabase com o cache local.
     * Realiza um 'upsert': insere se não existir, atualiza se já existir.
     * Também remove do cache local as denúncias que não existem mais no Supabase.
     */
    public void sincronizarCache(int userId, List<Denuncia> denuncias) {
        try (SQLiteDatabase db = bancoHelper.getWritableDatabase()) {
            db.beginTransaction();
            try {
                StringBuilder idsPermanecer = new StringBuilder();
                for (int i = 0; i < denuncias.size(); i++) {
                    idsPermanecer.append(denuncias.get(i).getDenunciaId());
                    if (i < denuncias.size() - 1) idsPermanecer.append(",");
                }

                String clausulaDelete = COLUNA_ID_USUARIO + " = ?";
                if (idsPermanecer.length() > 0) {
                    clausulaDelete += " AND " + COLUNA_ID_DENUNCIA + " NOT IN (" + idsPermanecer + ")";
                }
                db.delete(TABELA_DENUNCIAS, clausulaDelete, new String[]{String.valueOf(userId)});

                for (Denuncia d : denuncias) {
                    ContentValues valores = getValores(d);
                    valores.put(COLUNA_ID_DENUNCIA, d.getDenunciaId());

                    int rows = db.update(TABELA_DENUNCIAS, valores, COLUNA_ID_DENUNCIA + " = ?", new String[]{String.valueOf(d.getDenunciaId())});
                    if (rows == 0) {
                        db.insert(TABELA_DENUNCIAS, null, valores);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao sincronizar cache de denúncias", e);
        }
    }

    /**
     * Cria uma nova denúncia no banco de dados.
     *
     * @param denuncia O objeto Denuncia contendo todos os dados a serem inseridos.
     */
    public void criarDenuncia(Denuncia denuncia) {
        try (SQLiteDatabase db = bancoHelper.getWritableDatabase()) {
            ContentValues valores = getValores(denuncia);
            if (denuncia.getDenunciaId() != 0) {
                valores.put(COLUNA_ID_DENUNCIA, denuncia.getDenunciaId());
            }

            db.insertWithOnConflict(TABELA_DENUNCIAS, null, valores, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao criar denúncia no cache", e);
        }
    }

    /**
     * Busca todas as denúncias de um usuário no cache local.
     */
    public ArrayList<Denuncia> buscarTodasDenunciasCache(int userId) {
        ArrayList<Denuncia> lista = new ArrayList<>();
        String clausulaWhere = COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(userId)};

        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, null, clausulaWhere, argumentosWhere, null, null, COLUNA_ID_DENUNCIA + " DESC")) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUNA_ID_DENUNCIA));
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_DATA));
                    String endereco = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_ENDERECO));
                    String coordenadas = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_COORDENADAS));
                    String problemas = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_PROBLEMAS));
                    String descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_DESCRICAO));
                    String img = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_CAMINHO_IMAGEM));

                    Denuncia d = new Denuncia(id, userId, data, endereco, coordenadas, problemas, descricao);
                    d.setCaminhoImagem(img);
                    lista.add(d);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncias do cache", e);
        }
        return lista;
    }

    /**
     * Converte um objeto {@link Denuncia} em um objeto {@link ContentValues} para inserção ou atualização no banco de dados.
     * Este método auxiliar mapeia os atributos do objeto Denuncia para as colunas correspondentes da tabela.
     *
     * @param denuncia O objeto {@link Denuncia} a ser convertido.
     * @return Um objeto {@link ContentValues} preenchido com os dados da denúncia.
     */
    @NonNull
    private static ContentValues getValores(Denuncia denuncia) {
        ContentValues valores = new ContentValues();

        valores.put(COLUNA_ID_USUARIO, denuncia.getUserId());
        valores.put(COLUNA_DATA, denuncia.getData());
        valores.put(COLUNA_ENDERECO, denuncia.getEndereco());
        valores.put(COLUNA_COORDENADAS, denuncia.getCoordenadas());
        valores.put(COLUNA_PROBLEMAS, denuncia.getProblemas());
        valores.put(COLUNA_DESCRICAO, denuncia.getDescricao());
        valores.put(COLUNA_CAMINHO_IMAGEM, denuncia.getCaminhoImagem());
        return valores;
    }

    /**
     * Busca uma denúncia específica no banco de dados com base no ID da denúncia e no ID do usuário.
     *
     * @param userId     O ID do usuário que está tentando acessar a denúncia.
     * @param denunciaId O ID da denúncia a ser buscada.
     * @return Um objeto {@link Denuncia} contendo os dados da denúncia encontrada, ou {@code null}.
     */
    public Denuncia buscarDenunciaPorId(int userId, int denunciaId) {
        String[] colunasProjetadas = {
                COLUNA_ID_USUARIO, COLUNA_DATA, COLUNA_ENDERECO, COLUNA_COORDENADAS,
                COLUNA_PROBLEMAS, COLUNA_DESCRICAO, COLUNA_CAMINHO_IMAGEM
        };
        String clausulaWhere = COLUNA_ID_DENUNCIA + " = ? AND " + COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(denunciaId), String.valueOf(userId)};

        Denuncia denuncia = null;
        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                //pega os índices das colunas uma vez, para melhor performance.
                int indiceData = cursor.getColumnIndexOrThrow(COLUNA_DATA);
                int indiceEndereco = cursor.getColumnIndexOrThrow(COLUNA_ENDERECO);
                int indiceCoordenadas = cursor.getColumnIndexOrThrow(COLUNA_COORDENADAS);
                int indiceProblemas = cursor.getColumnIndexOrThrow(COLUNA_PROBLEMAS);
                int indiceDescricao = cursor.getColumnIndexOrThrow(COLUNA_DESCRICAO);
                int indiceCaminhoImagem = cursor.getColumnIndexOrThrow(COLUNA_CAMINHO_IMAGEM);

                String data = cursor.getString(indiceData);
                String endereco = cursor.getString(indiceEndereco);
                String coordenadas = cursor.getString(indiceCoordenadas);
                String problemas = cursor.getString(indiceProblemas);
                String descricao = cursor.getString(indiceDescricao);
                String caminhoImagem = cursor.getString(indiceCaminhoImagem);

                //cria um objeto 'Denuncia'
                denuncia = new Denuncia(denunciaId, userId, data, endereco, coordenadas, problemas, descricao);
                denuncia.setCaminhoImagem(caminhoImagem);
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncia do usuário.", e);
        }

        return denuncia;
    }

    /**
     * Busca os IDs de todas as denúncias associadas a um ID de usuário específico.
     * @param userId O ID do usuário para o qual buscar as denúncias.
     * @return Uma lista de IDs de denúncias (Integer).
     */
    public ArrayList<Integer> buscarDenunciasPorUserId(int userId) {
        ArrayList<Integer> listaDenuncias = new ArrayList<>();
        String[] colunasProjetadas = {COLUNA_ID_DENUNCIA};
        String clausulaWhere = COLUNA_ID_USUARIO + " = ?";
        String[] argumentosWhere = {String.valueOf(userId)};

        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_DENUNCIAS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int indiceDenunciaId = cursor.getColumnIndexOrThrow(COLUNA_ID_DENUNCIA);

                do {
                    int denunciaId = cursor.getInt(indiceDenunciaId);
                    listaDenuncias.add(denunciaId);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar denúncias do usuário.", e);
        }

        return listaDenuncias;
    }
}
