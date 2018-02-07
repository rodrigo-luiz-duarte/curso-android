package br.com.alura.agenda.dominio;

/**
 * Created by rodrigo on 07/02/2018.
 */

public class Aluno {

    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private Float nota;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return String.format("%1$d - %2$s", getId(), getNome());
    }
}
