package br.com.alura.agenda.task;

import android.os.AsyncTask;

import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.util.ClienteHttp;

/**
 * Created by rodrigo on 14/02/2018.
 */

public class SalvaAlunoTask extends AsyncTask{

    private static final String URL_API_ALUNO = "http://197.50.24.102:8080/api/aluno";
    private final Aluno aluno;

    public SalvaAlunoTask(Aluno aluno) {

        this.aluno = aluno;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        AlunoConverter converter = new AlunoConverter();
        String json = converter.toJson(aluno);

        ClienteHttp clienteHttp = new ClienteHttp();
        String resposta = clienteHttp.post(json, URL_API_ALUNO);

        return resposta;
    }
}
