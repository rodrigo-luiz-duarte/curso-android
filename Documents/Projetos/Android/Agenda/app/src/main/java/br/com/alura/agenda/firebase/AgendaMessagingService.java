package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.ListaAlunoDTO;
import br.com.alura.agenda.event.AtualizacaoAlunoEvent;

/**
 * Created by rodrigo on 16/02/2018.
 */

public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("Mensagem do FCM: ", String.valueOf(mensagem));

        this.sincronize(mensagem);
    }

    private void sincronize(Map<String, String> mensagem) {

        String chave = "alunoSync";

        if (mensagem.containsKey(chave)) {

            String json = mensagem.get(chave);

            Log.i("json", "conteudo da msg: " + json);

            final Gson gson = new GsonBuilder().create();
            ListaAlunoDTO listaAlunoDTO = gson.fromJson(json, ListaAlunoDTO.class);

            AlunoDAO alunoDAO = new AlunoDAO(this);
            alunoDAO.sincronize(listaAlunoDTO.getAlunos());
            alunoDAO.close();

            Log.i("Atualizacao FCM", "Aluno recebido atualizado com sucesso.");

            EventBus.getDefault().post(new AtualizacaoAlunoEvent());
        }
    }
}
