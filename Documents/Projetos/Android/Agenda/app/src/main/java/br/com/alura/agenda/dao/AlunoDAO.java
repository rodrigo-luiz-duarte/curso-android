package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.alura.agenda.dominio.Aluno;

import static br.com.alura.agenda.dao.DbInfo.NOME_BANCO_DE_DADOS;
import static br.com.alura.agenda.dao.DbInfo.VERSAO_BANCO_DE_DADOS;

/**
 * Created by rodrigo on 07/02/2018.
 */

public class AlunoDAO extends SQLiteOpenHelper {

    private static final String CAMPO_ID = "id";
    private static final String CAMPO_NOME = "nome";
    private static final String CAMPO_ENDERECO = "endereco";
    private static final String CAMPO_TELEFONE = "telefone";
    private static final String CAMPO_SITE = "site";
    private static final String CAMPO_NOTA = "nota";
    private static final String CAMPO_CAMINHO_FOTO = "caminhoFoto";
    private static final String NOME_TABELA = "Aluno";

    public AlunoDAO(Context context) {
        super(context, NOME_BANCO_DE_DADOS, null, VERSAO_BANCO_DE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.crieTabelaAluno(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {

            case 1:
                this.upgradeParaVersao2(db);
            case 2:
                this.upgradeParaVersao3(db);
        }
    }

    private void crieTabelaAluno(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder(String.format("CREATE TABLE %s \n", NOME_TABELA));
        sql.append("(");
        sql.append(String.format("%s CHAR(36) PRIMARY KEY", CAMPO_ID));
        sql.append(String.format(", %s TEXT NOT NULL", CAMPO_NOME));
        sql.append(String.format(", %s TEXT", CAMPO_ENDERECO));
        sql.append(String.format(", %s TEXT", CAMPO_TELEFONE));
        sql.append(String.format(", %s TEXT", CAMPO_SITE));
        sql.append(String.format(", %s REAL", CAMPO_NOTA));
        sql.append(String.format(", %s TEXT", CAMPO_CAMINHO_FOTO));
        sql.append(")");

        db.execSQL(sql.toString());
    }

    private void destruaTabelaAluno(SQLiteDatabase db) {

        String sql = String.format("DROP TABLE IF EXISTS %s;", NOME_TABELA);
        db.execSQL(sql.toString());
    }

    public void insira(Aluno aluno, SQLiteDatabase... db) {

        if (db == null || db.length == 0) {
            db = new SQLiteDatabase[] {getWritableDatabase()};
        }

        if (aluno.getId() == null) {
            aluno.setId(getUUID());
        }

        ContentValues dados = getDadosAluno(aluno);

        db[0].insert(NOME_TABELA, null, dados);
    }

    private ContentValues getDadosAluno(Aluno aluno) {

        ContentValues dados = new ContentValues();
        dados.put(CAMPO_ID, aluno.getId());
        dados.put(CAMPO_NOME, aluno.getNome());
        dados.put(CAMPO_ENDERECO, aluno.getEndereco());
        dados.put(CAMPO_TELEFONE, aluno.getTelefone());
        dados.put(CAMPO_SITE, aluno.getSite());
        dados.put(CAMPO_NOTA, aluno.getNota());
        dados.put(CAMPO_CAMINHO_FOTO, aluno.getCaminhoFoto());

        return dados;
    }

    public List<Aluno> listeAluno() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = getCursorListaAluno(db);
        return fetchListaAluno(c);
    }

    @NonNull
    private List<Aluno> listeAluno(SQLiteDatabase db) {

        Cursor c = getCursorListaAluno(db);
        return fetchListaAluno(c);
    }

    @NonNull
    private List<Aluno> fetchListaAluno(Cursor c) {

        List<Aluno> alunos = new ArrayList<Aluno>();

        while (c.moveToNext()) {

            Aluno aluno = fetchAluno(c);
            alunos.add(aluno);
        }

        c.close();

        return alunos;
    }

    private Cursor getCursorListaAluno(SQLiteDatabase db) {
        return db.rawQuery(String.format("SELECT * FROM %s", NOME_TABELA), null);
    }

    @NonNull
    private Aluno fetchAluno(Cursor c) {
        Aluno aluno = new Aluno();

        aluno.setId(c.getString(c.getColumnIndex(CAMPO_ID)));
        aluno.setNome(c.getString(c.getColumnIndex(CAMPO_NOME)));
        aluno.setEndereco(c.getString(c.getColumnIndex(CAMPO_ENDERECO)));
        aluno.setTelefone(c.getString(c.getColumnIndex(CAMPO_TELEFONE)));
        aluno.setSite(c.getString(c.getColumnIndex(CAMPO_SITE)));
        aluno.setNota(c.getFloat(c.getColumnIndex(CAMPO_NOTA)));
        aluno.setCaminhoFoto(c.getString(c.getColumnIndex(CAMPO_CAMINHO_FOTO)));

        return aluno;
    }

    public void delete(Aluno aluno, SQLiteDatabase... db) {

        if (db == null || db.length == 0) {
            db = new SQLiteDatabase[] {getWritableDatabase()};
        }

        String[] params = {aluno.getId().toString()};
        db[0].delete(NOME_TABELA, "id = ?", params);
    }

    public void atualize(Aluno aluno, SQLiteDatabase... db) {

        if (db == null || db.length == 0) {
            db = new SQLiteDatabase[] {getWritableDatabase()};
        }

        String[] params = {aluno.getId().toString()};

        ContentValues dados = getDadosAluno(aluno);

        db[0].update(NOME_TABELA, dados, "id = ?", params);
    }

    /**
     * Migra para a versão 2 do banco de dados.
     *
     * @param db
     */
    private void upgradeParaVersao2(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder(String.format("ALTER TABLE %s \n", NOME_TABELA));
        sql.append(String.format(" ADD COLUMN %s TEXT;", CAMPO_CAMINHO_FOTO));

        db.execSQL(sql.toString());
    }

    public boolean isAluno(String telefone) {

        String sql = String.format("SELECT * FROM %s WHERE telefone = ? ", NOME_TABELA);
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{telefone});

        int count = cursor.getCount();

        cursor.close();

        return count > 0;

    }

    private void upgradeParaVersao3(SQLiteDatabase db) {

        String nomeNovaTabela = NOME_TABELA + "_nova";

        this.crieNovaTabelaAluno(db, nomeNovaTabela);
        this.migraDadosTabelaNova(db, nomeNovaTabela);
    }

    private void crieNovaTabelaAluno(SQLiteDatabase db, String nomeNovaTabela) {


        StringBuilder sql = new StringBuilder(String.format("CREATE TABLE %s \n", nomeNovaTabela));
        sql.append("(");
        sql.append(String.format("%s CHAR(36) PRIMARY KEY", CAMPO_ID));
        sql.append(String.format(", %s TEXT NOT NULL", CAMPO_NOME));
        sql.append(String.format(", %s TEXT", CAMPO_ENDERECO));
        sql.append(String.format(", %s TEXT", CAMPO_TELEFONE));
        sql.append(String.format(", %s TEXT", CAMPO_SITE));
        sql.append(String.format(", %s REAL", CAMPO_NOTA));
        sql.append(String.format(", %s TEXT", CAMPO_CAMINHO_FOTO));
        sql.append(")");

        db.execSQL(sql.toString());
    }

    private void migraDadosTabelaNova(SQLiteDatabase db, String nomeTabela) {

        List<Aluno> alunos = this.listeAluno(db);

        this.destruaTabelaAluno(db);
        this.renomeieTabelaAlunoNova(db, nomeTabela, NOME_TABELA);

        for (Aluno aluno : alunos) {

            aluno.setId(this.getUUID());
            this.atualize(aluno, db);
        }

    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private void renomeieTabelaAlunoNova(SQLiteDatabase db, String nomeTabela, String novoNomeTabela) {

        StringBuilder sql = new StringBuilder(String.format("ALTER TABLE %s \n", nomeTabela));
        sql.append(String.format("RENAME TO %s", novoNomeTabela));
        db.execSQL(sql.toString());
    }

    public void sincronize(List<Aluno> alunos) {

        for (Aluno aluno: alunos) {

            if (!existe(aluno)) {
                this.insira(aluno);
            } else {
                this.atualize(aluno);
            }
        }
    }

    private boolean existe(Aluno aluno) {

        String sql = String.format("SELECT * FROM %s WHERE id = ? LIMIT 1", NOME_TABELA);
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{aluno.getId()});

        int count = cursor.getCount();

        cursor.close();

        return count > 0;
    }
}
