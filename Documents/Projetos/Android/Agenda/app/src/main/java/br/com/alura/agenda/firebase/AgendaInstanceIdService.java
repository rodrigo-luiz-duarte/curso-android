package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.alura.agenda.retrofit.RetrofitInicializador.URL_API_ALUNO;

/**
 * Created by rodrigo on 16/02/2018.
 */

public class AgendaInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token firebase", token);

        this.envieTokenParaServidor(token);
    }

    private void envieTokenParaServidor(final String token) {

        Call<Void> call = new RetrofitInicializador(URL_API_ALUNO).getDispositivoSerice().envieToken(token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("token enviado", token);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("token falhou", t.getMessage(), t);
            }
        });
    }
}
