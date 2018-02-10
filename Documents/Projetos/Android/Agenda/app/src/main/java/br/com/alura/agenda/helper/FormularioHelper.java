package br.com.alura.agenda.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView campoFoto;

    public FormularioHelper(FormularioActivity formulario) {

        this.campoNome = (EditText) formulario.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) formulario.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) formulario.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) formulario.findViewById(R.id.formulario_site);
        this.campoNota = (RatingBar) formulario.findViewById(R.id.formulario_nota);
        this.campoFoto = formulario.findViewById(R.id.formulario_foto_aluno);

    }

    public Aluno getAluno() {

        if (aluno == null)  {
            aluno = new Aluno();
        }

        aluno.setNome(campoNome.getText().toString());
        aluno.setEndereco(campoEndereco.getText().toString());
        aluno.setTelefone(campoTelefone.getText().toString());
        aluno.setSite(campoSite.getText().toString());
        aluno.setNota(Float.valueOf(campoNota.getRating() * 2));
        aluno.setCaminhoFoto((String) campoFoto.getTag());

        if (aluno.getCaminhoFoto() != null) {
            carregaFoto(aluno.getCaminhoFoto());
        }

        return aluno;
    }

    public void preencheFormulario(Aluno aluno) {

        this.aluno = aluno;
        campoNome.setText(aluno.getNome());
        campoEndereco.setText(aluno.getEndereco());
        campoTelefone.setText(aluno.getTelefone());
        campoSite.setText(aluno.getSite());
        campoNota.setRating(aluno.getNota() / 2);

        if (aluno.getCaminhoFoto() != null) {
            carregaFoto(aluno.getCaminhoFoto());
        }

    }

    public void carregaFoto(String caminhoFoto) {

        Bitmap bm = BitmapFactory.decodeFile(caminhoFoto);
        bm = Bitmap.createScaledBitmap(bm, 100, 100, true);
        campoFoto.setImageBitmap(bm);
        campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
        campoFoto.setTag(caminhoFoto);

    }
}
