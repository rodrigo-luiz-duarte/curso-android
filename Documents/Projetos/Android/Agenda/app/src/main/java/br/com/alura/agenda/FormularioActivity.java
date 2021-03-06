package br.com.alura.agenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;
import br.com.alura.agenda.helper.FormularioHelper;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.alura.agenda.retrofit.RetrofitInicializador.URL_API_ALUNO;

public class FormularioActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_ACAO_CAMERA = 1;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        helper = new FormularioHelper(this);

        if (aluno != null) {
            helper.preencheFormulario(aluno);
        }

        registreListenerBtnCamera();
    }

    private void registreListenerBtnCamera() {

        Button botaoCamera = (Button) findViewById(R.id.formulario_botao_camera);

        botaoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(caminhoFoto)));
                startActivityForResult(irParaCamera, REQUEST_CODE_ACAO_CAMERA);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:

                AlunoDAO dao = new AlunoDAO(this);

                Aluno aluno = helper.getAluno();
                aluno.dessincronize();

                if (aluno != null) {

                    if (aluno.getId() == null) {
                        dao.insira(aluno);
                    }  else {
                        dao.atualize(aluno);
                    }
                }

                dao.close();

                salveNoServidor(aluno);

                Toast.makeText(FormularioActivity.this,
                        String.format("Aluno %s salvo com sucesso!", aluno.getNome()),
                        Toast.LENGTH_SHORT).show();

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void salveNoServidor(Aluno aluno) {
        Call<Void> call = new RetrofitInicializador(URL_API_ALUNO).getAlunoService().salve(aluno);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("onResponse", "Aluno inserido com sucesso.");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onFailure", "Falha ao inserir aluno.", t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACAO_CAMERA) {
            if (resultCode == RESULT_OK) {

                helper.carregaFoto(caminhoFoto);

            }
        }
    }
}
