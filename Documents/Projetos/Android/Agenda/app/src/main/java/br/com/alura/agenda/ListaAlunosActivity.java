package br.com.alura.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.alura.agenda.adapter.AlunoAdapter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACAO_TELEFONAR = 1;
    private static final int REQUEST_CODE_ACAO_RECEBER_SMS = 2;

    private Aluno alunoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        this.registreBtnNovoAlunoListener();
        this.registerForContextMenu(getListaAlunos());
        this.registreListenerOnContexteMewnuItemClick();
        this.solicitePermissaoReceberSMS();

    }

    private void carregueAlunos() {

        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.listeAluno();
        dao.close();

        ListView listaAlunos = getListaAlunos();
        AlunoAdapter adapter = new AlunoAdapter(this, alunos);

        listaAlunos.setAdapter(adapter);
    }

    private ListView getListaAlunos() {
        return (ListView) findViewById(R.id.lista_alunos);
    }

    private void registreBtnNovoAlunoListener() {

        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.carregueAlunos();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) getListaAlunos().getItemAtPosition(info.position);

        crieMenuContextoDeletar(menu, aluno);
        crieMenuContextoVisitarSite(menu, aluno);
        crieMenuContextoEnviarSMS(menu, aluno);
        crieMenuContextoTelefonar(menu, aluno);
        crieMenuContextoVisualizarEnderecoMapa(menu, aluno);
    }

    private void crieMenuContextoDeletar(ContextMenu menu, final Aluno aluno) {

        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.delete(aluno);
                dao.close();

                carregueAlunos();

                return false;
            }
        });
    }

    private void crieMenuContextoVisitarSite(ContextMenu menu, final Aluno aluno) {

        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        String site = aluno.getSite();

        if (!site.startsWith("http://")) {
            site = "http://" + site;
        }

        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);
    }

    private void crieMenuContextoEnviarSMS(ContextMenu menu, final Aluno aluno) {

        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);

        String telefone = aluno.getTelefone();

        intentSMS.setData(Uri.parse("sms:" + telefone));
        itemSMS.setIntent(intentSMS);
    }

    private void crieMenuContextoTelefonar(ContextMenu menu, final Aluno aluno) {

        MenuItem itemTelefonar = menu.add("Telefonar");

        itemTelefonar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                telefoneParaAluno(aluno);
                return false;
            }
        });
    }

    private void telefoneParaAluno(Aluno aluno) {

        Intent intentLigar = new Intent(Intent.ACTION_CALL);
        intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            alunoSelecionado = aluno;

            ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ACAO_TELEFONAR);
        } else {

            startActivity(intentLigar);

        }

    }

    private void crieMenuContextoVisualizarEnderecoMapa(ContextMenu menu, final Aluno aluno) {

        MenuItem itemMapa = menu.add("Ver endereço mo mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);

        String endereco = aluno.getEndereco();

        intentMapa.setData(Uri.parse("geo:0,0?q=" + endereco));
        itemMapa.setIntent(intentMapa);
    }

    private void registreListenerOnContexteMewnuItemClick() {

        getListaAlunos().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {

                Aluno aluno = (Aluno) lista.getItemAtPosition(position);

                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intent.putExtra("aluno", aluno);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_ACAO_TELEFONAR: telefoneParaAluno(alunoSelecionado);
            break;
        }

    }

    private void solicitePermissaoReceberSMS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_ACAO_RECEBER_SMS);
        }
    }
}