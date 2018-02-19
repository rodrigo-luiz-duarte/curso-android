package br.com.alura.agenda.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rodrigo on 18/02/2018.
 */

public class AlunoPreferences {

    private static final String VERSAO = "versao";
    private final Context context;

    public AlunoPreferences(Context context) {
        this.context = context;
    }

    public void salveVersao(String versao) {

        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSAO, versao);
        editor.commit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(this.getClass().getName(), context.MODE_PRIVATE);
    }

    public String getVersao() {

        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(VERSAO, null);
    }

    public boolean temVersao() {
        return getVersao() != null;
    }
}
