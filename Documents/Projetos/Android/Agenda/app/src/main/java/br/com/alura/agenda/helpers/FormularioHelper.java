package br.com.alura.agenda.helpers;

import android.widget.EditText;
import android.widget.RatingBar;

import br.com.alura.agenda.FormularioActivity;
import br.com.alura.agenda.R;
import br.com.alura.agenda.dominio.Aluno;

/**
 * Created by rodrigo on 07/02/2018.
 */

public class FormularioHelper {

    private EditText campoNome;
    private EditText campoEndereco;
    private EditText campoTelefone;
    private EditText campoSite;
    private RatingBar campoNota;
    private Aluno aluno;

    public FormularioHelper(FormularioActivity formulario) {

        this.campoNome = (EditText) formulario.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) formulario.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) formulario.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) formulario.findViewById(R.id.formulario_site);
        this.campoNota = (RatingBar) formulario.findViewById(R.id.formulario_nota);

    }

    public Aluno getAluno() {

        if (aluno == null)  {
            aluno = new Aluno();
        }

        aluno.setNome(campoNome.getText().toString());
        aluno.setEndereco(campoEndereco.getText().toString());
        aluno.setTelefone(campoTelefone.getText().toString());
        aluno.setSite(campoSite.getText().toString());
        aluno.setNota(Float.valueOf(campoNota.getProgress()));
        return aluno;
    }

    public void preencheFormulario(Aluno aluno) {

        this.aluno = aluno;
        campoNome.setText(aluno.getNome());
        campoEndereco.setText(aluno.getEndereco());
        campoTelefone.setText(aluno.getTelefone());
        campoSite.setText(aluno.getSite());
        campoNota.setRating(aluno.getNota());

    }
}
