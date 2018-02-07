package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String CAMPO_NOTA = "notar";

    public AlunoDAO(Context context) {
        super(context, NOME_BANCO_DE_DADOS, null, VERSAO_BANCO_DE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.crieTabelaAluno(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        this.destruaTabelaAluno(db);
        this.crieTabelaAluno(db);
    }

    private void crieTabelaAluno(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder("CREATE TABLE Aluno \n");
        sql.append("(");
        sql.append(String.format("%s INTEGER PRIMARY KEY,", CAMPO_ID));
        sql.append(String.format("%s TEXT NOT NULL,", CAMPO_NOME));
        sql.append(String.format("%s TEXT,", CAMPO_ENDERECO));
        sql.append(String.format("%s TEXT,", CAMPO_TELEFONE));
        sql.append(String.format("%s TEXT,", CAMPO_SITE));
        sql.append(String.format("%s REAL", CAMPO_NOTA));
        sql.append(")");

        db.execSQL(sql.toString());
    }

    private void destruaTabelaAluno(SQLiteDatabase db) {

        String sql = "DROP TABLE IF EXISTS Aluno;";
        db.execSQL(sql.toString());
    }

    public void insere(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put(CAMPO_NOME, aluno.getNome());
        dados.put(CAMPO_ENDERECO, aluno.getEndereco());
        dados.put(CAMPO_TELEFONE, aluno.getTelefone());
        dados.put(CAMPO_SITE, aluno.getSite());
        dados.put(CAMPO_NOTA, aluno.getNota());

        db.insert("Aluno", null, dados);
    }

    public List<Aluno> listeAluno() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Aluno", null);
        List<Aluno> alunos = new ArrayList<Aluno>();

        while (c.moveToNext()) {

            Aluno aluno = new Aluno();

            aluno.setId(c.getLong(c.getColumnIndex(CAMPO_ID)));
            aluno.setNome(c.getString(c.getColumnIndex(CAMPO_NOME)));
            aluno.setEndereco(c.getString(c.getColumnIndex(CAMPO_ENDERECO)));
            aluno.setTelefone(c.getString(c.getColumnIndex(CAMPO_TELEFONE)));
            aluno.setSite(c.getString(c.getColumnIndex(CAMPO_SITE)));
            aluno.setNota(c.getFloat(c.getColumnIndex(CAMPO_NOTA)));

            alunos.add(aluno);
        }
        c.close();

        return alunos;
    }
}
