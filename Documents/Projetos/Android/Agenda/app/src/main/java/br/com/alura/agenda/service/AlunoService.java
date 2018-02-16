package br.com.alura.agenda.service;

import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.dto.ListaAlunoDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by rodrigo on 14/02/2018.
 */

public interface AlunoService {

    public static final String URL_API_ALUNO = "http://197.50.24.102:8080/api/";

    @POST("aluno")
    Call<Void> salve(@Body Aluno aluno);

    @GET("aluno")
    Call<ListaAlunoDTO> sincronize();

    @DELETE("aluno/{id}")
    Call<Void> delete(@Path("id") String id);
}
