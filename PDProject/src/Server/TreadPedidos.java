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
public class TreadPedidos extends Thread {

    private final Server server;

    public TreadPedidos(Server server) {
        this.server = server;
    }

    @Override
    public void run() {

        Socket socketToCleinte = null;
        ObjectInputStream in = null;
        ObjectOutputStream on = null;
        int pedido;
        Autenticacao novaAutenticacao;
        Registo novoRegisto;
        String resposta = "";

        if (server.socketPedidos == null) {
            return;
        }

        try {
            while (true) {

                try {
                    socketToCleinte = server.socketPedidos.accept();
                } catch (IOException ex) {
                    System.out.println("Ocorreu uma excepcao no socket enquanto aguardava por um pedido de ligacao: \n\t" + ex);
                    System.out.println("O servidor vai terminar...");
                    return;
                }

                try {
                    in = new ObjectInputStream(socketToCleinte.getInputStream());
                    on = new ObjectOutputStream(socketToCleinte.getOutputStream());

                    pedido = (Integer) in.readObject();

                    switch (pedido) {
                        case 1:
                            System.out.println("Pedido 1");
                            ///in = new ObjectInputStream(socketToCleinte.getInputStream());
                            novoRegisto = (Registo) in.readObject();
                            resposta += "Registado";
                            break;
                        case 2:
                            System.out.println("Pedido 2");
                            //in = new ObjectInputStream(socketToCleinte.getInputStream());
                            novaAutenticacao = (Autenticacao) in.readObject();
                            resposta += "Autenticado";
                            break;

                        default:
                            System.out.println("Pedido estranho");
                            resposta += "PedidoEstranho";
                            break;
                    }
                    on.writeObject(resposta);
                    on.flush();

                } catch (IOException ex) {
                    System.out.println("Ocorreu a excepcao de E/S: \n\t" + ex);
                } catch (ClassNotFoundException ex) {
                    System.out.println("Pedido recebido de tipo inesperado:\n\t" + ex);
                }

            }

        } finally {

            try {
                if (socketToCleinte != null) {
                    socketToCleinte.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
