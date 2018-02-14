package br.com.alura.agenda.util;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by rodrigo on 10/02/2018.
 */

public class ClienteHttp {

    public String post(String json, String endereco){

        try {
            
            URL url = new URL(endereco);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintStream saida = new PrintStream(connection.getOutputStream());
            saida.println(json);

            connection.connect();

            String resposta = new Scanner(connection.getInputStream()).next();

            return resposta;


        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
