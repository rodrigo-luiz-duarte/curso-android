package br.com.alura.agenda.sincronizacao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.alura.agenda.ListaAlunosActivity;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.dto.ListaAlunoDTO;
import br.com.alura.agenda.event.AtualizacaoAlunoEvent;
import br.com.alura.agenda.preferences.AlunoPreferences;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.alura.agenda.retrofit.RetrofitInicializador.URL_API_ALUNO;

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
    private Callback<ListaAlunoDTO> getListaAlunoCallback(final Boolean... edicaoLocal) {

        return new Callback<ListaAlunoDTO>() {
            @Override
            public void onResponse(Call<ListaAlunoDTO> call, Response<ListaAlunoDTO> response) {

                ListaAlunoDTO listaAlunoDTO = response.body();

                sincronize(listaAlunoDTO);

                bus.post(new AtualizacaoAlunoEvent());

//              Testa se o callback está sendo utilizado pelo método de sincronização
//              dos alunos não sincronizados.
//              Só será executado se o callback estiver sendo utilizado pelo método
//              de sincronização com o servidor, para reaproveitar o callback
//              sem causar loop infinito.
                if (edicaoLocal == null || edicaoLocal.length == 0 || !edicaoLocal[0]) {

//                Chamada do método para sincronizar os alunos editados no app
//                quando em modo offline, priozando os dados existentes no servidor
//                quando da edição paralela de mesmos recursos.
//                Ou seja, caso um aluno seja editado no app e também
//                no servidor, irá prevalecer os dados do servidor;
                    sincronizeAlunosNaoSincronizados();
                }
            }

            @Override
            public void onFailure(Call<ListaAlunoDTO> call, Throwable t) {
                Log.e("onFailure", "Sincronizacao com servidor falhou: ", t);
                bus.post(new AtualizacaoAlunoEvent());
            }
        };
    }

    public void sincronize(ListaAlunoDTO listaAlunoDTO) {

        String versao = listaAlunoDTO.getMomentoDaUltimaModificacao();

        Log.i("versao externa", versao);

        if (ehVersaoNova(versao)) {

            Log.i("versao dos interna", preferences.getVersao());

            preferences.salveVersao(versao);

            AlunoDAO alunoDAO = new AlunoDAO(context);
            alunoDAO.sincronize(listaAlunoDTO.getAlunos());
            alunoDAO.close();

            Log.i("versao atualizada", "versao: " + preferences.getVersao());
        }

    }

    private boolean ehVersaoNova(String versao) {

        if (!preferences.temVersao()) {
            return true;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {

            Date dataExterna = format.parse(versao);
            String versaoInterna = preferences.getVersao();
            Date dataInterna = format.parse(versaoInterna);
            return dataExterna.after(dataInterna);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
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
    private void sincronizeAlunosNaoSincronizados() {

        AlunoDAO alunoDAO = new AlunoDAO(context);
        List<Aluno> alunos = alunoDAO.listeAlunoNaoSincronizado();
        alunoDAO.close();

        Call<ListaAlunoDTO> call = new RetrofitInicializador(RetrofitInicializador.URL_API_ALUNO).getAlunoService().atualize(alunos);

        call.enqueue(getListaAlunoCallback(true));
    }

    public void delete(final Aluno aluno) {

        Call<Void> call = new RetrofitInicializador(URL_API_ALUNO).getAlunoService().delete(aluno.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.delete(aluno);
                alunoDAO.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onFailure", "Exclusao no servidor falhou: ", t);
            }
        });
    }
}