/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Pedro
 */
public class AuthenticationRequest extends Thread {

    private final Server server;

    public AuthenticationRequest(Server server) {
        this.server = server;
    }

    private final boolean VerificaRegisto(Registo registo) {

        for (Registo Registo : server.Registos) {
            if (Registo.equals(registo)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public void run() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Autenticacao request;
        String resposta = "";

        if (server.socketAutenticacao == null) {
            return;
        }

        System.out.println("Thread Autenticaçao " + server.socketAutenticacao.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = server.socketAutenticacao.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return;
            }

            try {

                out = new ObjectOutputStream(toClientSocket.getOutputStream());
                in = new ObjectInputStream(toClientSocket.getInputStream());

                request = (Autenticacao) (in.readObject());
                if (request == null || !VerificaRegisto(request.getRegisto())) { //EOF

                    System.out.println("Erro utlizador necessita de estar registado primeiro:\n\t");
                    resposta += "Nao registado";
                    //Envia a resposta ao cliente
                    out.writeObject(resposta);
                    out.flush();
                    toClientSocket.close();
                    continue; //to next client request
                }

                server.inClientes.add(request);
                //Constroi a resposta terminando-a com uma mudanca de lina
                resposta += "autenticado";

                //Envia a resposta ao cliente
                out.writeObject(resposta);
                out.flush();

            } catch (IOException e) {
                System.out.println("Erro na comunicação como o cliente "
                        + toClientSocket.getInetAddress().getHostAddress() + ":"
                        + toClientSocket.getPort() + "\n\t" + e);
            } catch (ClassNotFoundException e) {
                System.out.println("Pedido recebido de tipo inesperado:\n\t" + e);
            } finally {
                try {
                    if (toClientSocket != null) {
                        toClientSocket.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

}