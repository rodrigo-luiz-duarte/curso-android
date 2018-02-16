package br.com.alura.agenda.retrofit;

import br.com.alura.agenda.service.AlunoService;
import br.com.alura.agenda.service.DispositivoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rodrigo on 14/02/2018.
 */

public class RetrofitInicializador {

    public static final String URL_API_ALUNO = "http://197.50.24.102:8080/api/";
    private final Retrofit retrofit;

    public RetrofitInicializador(String urlBase) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpCliente = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpCliente)
                .build();
    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }

    public DispositivoService getDispositivoSerice() {
        return retrofit.create(DispositivoService.class);
    }
}
