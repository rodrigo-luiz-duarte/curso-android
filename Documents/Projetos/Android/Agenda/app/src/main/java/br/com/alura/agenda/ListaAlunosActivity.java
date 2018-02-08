package br.com.alura.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dominio.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        this.registreBtnNovoAlunoListener();
        this.registerForContextMenu(getListaAlunos());
        this.registreListenerOnContexteMewnuItemClick();

    }

    private void carregueAlunos(){

        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.listeAluno();
        dao.close();

        ListView listaAlunos = getListaAlunos();
        ArrayAdapter<Aluno> adapter =
                new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);

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

        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Aluno aluno = (Aluno) getListaAlunos().getItemAtPosition(info.position);
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.delete(aluno);
                dao.close();

                carregueAlunos();

                return false;
            }
        });
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
}