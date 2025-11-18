package com.example.infralerta;

import android.content.ContentValues;import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Controller responsável por todas as operações de banco de dados
 * relacionadas à tabela de usuários.
 */
public class BancoControllerUsuarios {

    //constantes para nomes de tabela e colunas, evitando erros de digitação.
    private static final String TABELA_USUARIOS = "usuarios";
    public static final String COLUNA_USER_ID = "user_id";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_EMAIL = "email";
    public static final String COLUNA_SENHA = "senha";
    public static final String COLUNA_CPF = "cpf";

    private final CriaBanco bancoHelper;

    public BancoControllerUsuarios(Context contexto) {
        bancoHelper = new CriaBanco(contexto);
    }

    /**
     * Cria um novo usuário no banco de dados.
     * @param nome O nome do usuário.
     * @param email O e-mail do usuário.
     * @param senhaHash A senha do usuário já em formato hash (SHA-256).
     * @param cpf O CPF do usuário.
     * @return {@code true} se a inserção for bem-sucedida, {@code false} caso contrário.
     */
    public boolean criarUsuario(String nome, String email, String senhaHash, String cpf) {
        long resultado;

        // 'try-with-resources' garante que a conexão com o banco (db) seja fechada automaticamente.
        try (SQLiteDatabase db = bancoHelper.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put(COLUNA_NOME, nome);
            valores.put(COLUNA_EMAIL, email);
            valores.put(COLUNA_SENHA, senhaHash);
            valores.put(COLUNA_CPF, cpf);

            resultado = db.insert(TABELA_USUARIOS, null, valores);
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao criar usuário", e);
            resultado = -1; //erro
        }

        return resultado != -1;
    }

    /**
     * Altera os dados de um usuário existente.
     * Apenas os campos não nulos serão atualizados.
     * @param userId O ID do usuário a ser alterado.
     * @param nome O novo nome (ou null para não alterar).
     * @param email O novo e-mail (ou null para não alterar).
     * @param senhaHash A nova senha em hash (ou null para não alterar).
     * @param cpf O novo CPF (ou null para não alterar).
     * @return {@code true} se a alteração for bem-sucedida, {@code false} caso contrário.
     */
    public boolean alterarUsuario(int userId, String nome, String email, String senhaHash, String cpf) {
        int linhasAfetadas;

        try (SQLiteDatabase db = bancoHelper.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            //adiciona ao update apenas os valores que não são nulos.
            if (nome != null) valores.put(COLUNA_NOME, nome);
            if (email != null) valores.put(COLUNA_EMAIL, email);
            if (senhaHash != null) valores.put(COLUNA_SENHA, senhaHash);
            if (cpf != null) valores.put(COLUNA_CPF, cpf);

            // Prepara a cláusula WHERE de forma segura para evitar SQL Injection.
            String clausulaWhere = COLUNA_USER_ID + " = ?";
            String[] argumentosWhere = {String.valueOf(userId)};

            linhasAfetadas = db.update(TABELA_USUARIOS, valores, clausulaWhere, argumentosWhere);

        } catch (Exception e) {
            Log.e("BancoController", "Erro ao alterar usuário", e);
            linhasAfetadas = 0; // Sinaliza que nenhuma linha foi alterada devido ao erro.
        }

        return linhasAfetadas > 0;
    }

    /**
     * Busca um usuário pelo e-mail e senha (hash) para fins de login.
     * @param email O e-mail do usuário.
     * @param senhaHash A senha do usuário já em formato hash.
     * @return Um objeto {@code Cursor} com os dados do usuário, ou vazio se não for encontrado.
     */
    public Cursor carregarDadosLogin(String email, String senhaHash) {
        SQLiteDatabase db = bancoHelper.getReadableDatabase();
        String[] colunas = {COLUNA_USER_ID, COLUNA_NOME, COLUNA_EMAIL, COLUNA_SENHA, COLUNA_CPF};
        String clausulaWhere = COLUNA_EMAIL + " = ? AND " + COLUNA_SENHA + " = ?";
        String[] argumentosWhere = {email, senhaHash};

        return db.query(TABELA_USUARIOS, colunas, clausulaWhere, argumentosWhere, null, null, null);
    }

    /**
     * Carrega todos os dados de um usuário específico pelo seu ID.
     * @param id O ID do usuário.
     * @return Um objeto {@code Cursor} com os dados do usuário.
     */
    public Cursor carregarDadosUsuario(int id) {
        SQLiteDatabase db = bancoHelper.getReadableDatabase();
        String[] colunas = {COLUNA_USER_ID, COLUNA_NOME, COLUNA_EMAIL, COLUNA_SENHA, COLUNA_CPF};
        String clausulaWhere = COLUNA_USER_ID + " = ?";
        String[] argumentosWhere = {String.valueOf(id)};

        return db.query(TABELA_USUARIOS, colunas, clausulaWhere, argumentosWhere, null, null, null);
    }

    /**
     * Busca o ID de um usuário a partir do seu e-mail e CPF.
     * Útil para o processo de cadastro.
     * @param email O e-mail a ser buscado.
     * @param cpf O CPF a ser buscado.
     * @return O ID do usuário se encontrado, ou -1 caso contrário.
     */
    public int buscarUserId(String email, String cpf) {
        int userId = -1;

        String[] colunasProjetadas = {COLUNA_USER_ID};
        String clausulaWhere = COLUNA_EMAIL + " = ? OR " + COLUNA_CPF + " = ?";
        String[] argumentosWhere = {email, cpf};

        try (SQLiteDatabase db = bancoHelper.getReadableDatabase();
             Cursor cursor = db.query(TABELA_USUARIOS, colunasProjetadas, clausulaWhere, argumentosWhere, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int indiceColuna = cursor.getColumnIndex(COLUNA_USER_ID);
                if (indiceColuna != -1) {
                    userId = cursor.getInt(indiceColuna);
                }
            }
        } catch (Exception e) {
            Log.e("BancoController", "Erro ao buscar user_id por e-mail ou CPF", e);
        }

        return userId;
    }

    public boolean verificarUsuarioExistente(String email, String cpf) {
        return buscarUserId(email, cpf) != -1;
    }
}
