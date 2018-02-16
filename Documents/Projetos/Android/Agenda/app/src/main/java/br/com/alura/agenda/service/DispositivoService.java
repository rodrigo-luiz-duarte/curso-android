package br.com.alura.agenda.service;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rodrigo on 16/02/2018.
 */

public interface DispositivoService {

    @POST("firebase/dispositivo")
    Call<Void> envieToken(@Header("token") String token);
}
