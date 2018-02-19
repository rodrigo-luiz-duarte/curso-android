package br.com.alura.agenda.sincronizacao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.dto.ListaAlunoDTO;
import br.com.alura.agenda.event.AtualizacaoAlunoEvent;
import br.com.alura.agenda.preferences.AlunoPreferences;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {

    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context) {

        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    public void sincronizeComServidor() {

        if (preferences.temVersao()) {
            this.listeApenasNovosAlunos();
        } else {
            this.listeTodosAlunos();
        }
    }

    @NonNull
    private Callback<ListaAlunoDTO> getListaAlunoCallback() {

        return new Callback<ListaAlunoDTO>() {
            @Override
            public void onResponse(Call<ListaAlunoDTO> call, Response<ListaAlunoDTO> response) {

                ListaAlunoDTO listaAlunoDTO = response.body();

                String versao = listaAlunoDTO.getMomentoDaUltimaModificacao();

                preferences.salveVersao(versao);

                Log.i("versao dos dados", "versao: " + preferences.getVersao());

                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.sincronize(listaAlunoDTO.getAlunos());
                alunoDAO.close();
                bus.post(new AtualizacaoAlunoEvent());
            }

            @Override
            public void onFailure(Call<ListaAlunoDTO> call, Throwable t) {
                Log.e("onFailure", "Sincronizacao com servidor falhou: ", t);
                bus.post(new AtualizacaoAlunoEvent());
            }
        };
    }

    private void listeTodosAlunos() {

        Call<ListaAlunoDTO> call = new RetrofitInicializador(RetrofitInicializador.URL_API_ALUNO).getAlunoService().listeTodos();
        call.enqueue(getListaAlunoCallback());
    }

    private void listeApenasNovosAlunos() {

        String versao = preferences.getVersao();

        Call<ListaAlunoDTO> call = new RetrofitInicializador(RetrofitInicializador
                .URL_API_ALUNO).getAlunoService().listeApenasNovos(versao);

        call.enqueue(getListaAlunoCallback());
    }

    /**
     *
     * Envia para o servidor apenas os alunos que estão armazenados localmente
     * e ainda não foram enviados para o servidor.
     *
     */
    public void sincronizeAlunosNaoSincronizados() {

        AlunoDAO alunoDAO = new AlunoDAO(context);
        List<Aluno> alunos = alunoDAO.listeAlunoNaoSincronizado();
        alunoDAO.close();

        Call<ListaAlunoDTO> call = new RetrofitInicializador(RetrofitInicializador.URL_API_ALUNO).getAlunoService().atualize(alunos);

        call.enqueue(getListaAlunoCallback());
    }
}