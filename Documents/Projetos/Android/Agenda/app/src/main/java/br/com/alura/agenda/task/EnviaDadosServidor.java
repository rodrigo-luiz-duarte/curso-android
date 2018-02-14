package br.com.alura.agenda.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.util.ClienteHttp;

/**
 * Created by rodrigo on 10/02/2018.
 */

public class EnviaDadosServidor extends AsyncTask<Void, Void, String> {

    private static final String URL_SERVICO = "https://www.caelum.com.br/mobile";

    private Context context;
    private ProgressDialog progressDialog;

    public EnviaDadosServidor( Context context){

        this.context = context;
    }


    @Override
    protected String doInBackground(Void ... params) {

        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.listeAluno();
        dao.close();

        AlunoConverter converter = new AlunoConverter();
        String json = converter.toJson(alunos);

        ClienteHttp cliente = new ClienteHttp();
        String resposta = cliente.post(json, URL_SERVICO);

        return resposta;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        progressDialog = ProgressDialog.show(context,"Aguarde" ,
                "Enviando para o servidor ...",
                true, true);

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String resposta) {

        progressDialog.dismiss();;
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }
}
