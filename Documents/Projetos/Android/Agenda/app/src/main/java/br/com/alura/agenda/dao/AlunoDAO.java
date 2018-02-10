package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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

            case 1: this.upgradeParaVersao2(db);
        }
    }

    private void crieTabelaAluno(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder(String.format("CREATE TABLE %s \n", NOME_TABELA));
        sql.append("(");
        sql.append(String.format("%s INTEGER PRIMARY KEY", CAMPO_ID));
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

    public void insira(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = getDadosAluno(aluno);

        db.insert(NOME_TABELA, null, dados);
    }

    @NonNull
    private ContentValues getDadosAluno(Aluno aluno) {

        ContentValues dados = new ContentValues();
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
        Cursor c = db.rawQuery(String.format("SELECT * FROM %s", NOME_TABELA), null);
        List<Aluno> alunos = new ArrayList<Aluno>();

        while (c.moveToNext()) {

            Aluno aluno = new Aluno();

            aluno.setId(c.getLong(c.getColumnIndex(CAMPO_ID)));
            aluno.setNome(c.getString(c.getColumnIndex(CAMPO_NOME)));
            aluno.setEndereco(c.getString(c.getColumnIndex(CAMPO_ENDERECO)));
            aluno.setTelefone(c.getString(c.getColumnIndex(CAMPO_TELEFONE)));
            aluno.setSite(c.getString(c.getColumnIndex(CAMPO_SITE)));
            aluno.setNota(c.getFloat(c.getColumnIndex(CAMPO_NOTA)));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex(CAMPO_CAMINHO_FOTO)));

            alunos.add(aluno);
        }
        c.close();

        return alunos;
    }

    public void delete(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();
        String[] params = {aluno.getId().toString()};
        db.delete(NOME_TABELA, "id = ?", params);
    }

    public void atualize(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();
        String[] params = {aluno.getId().toString()};

        ContentValues dados = getDadosAluno(aluno);

        db.update(NOME_TABELA, dados, "id = ?", params);
    }

    /**
     * Migra para a versão 2 do banco de dados.
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
}
