/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import others.Constantes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Pedro
 */
public class mainServidor implements Constantes {

    public static void main(String[] args) {
        Server timeServer = new Server(args, true);

        try {
            System.out.println("Connected");
            timeServer.conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.err.print(e);
        }
        if (timeServer.conn != null) {
            timeServer.lancaThreads();
            timeServer.ActualizaListasDados();

            try {
                System.out.println("Comando:");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

                while (true) {

                    String cast = in.readLine();

                    if (cast.equals("exit")) {
                        timeServer.disconectarAutenicacoesInServidor();
                        return;
                    }
                    if (cast.equals("lista")) {
                        timeServer.listaRegistos();
                    }
                    if (cast.equals("envia")) {
                        timeServer.enviarMensagensUtilizadores("ola");
                        //timeServer.enviarMensagemUtlizador(1, "ola1");
                    }
                    if (cast.equals("Informacao")) {
                        timeServer.listaInformaçaoUtlizadors();
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

}
