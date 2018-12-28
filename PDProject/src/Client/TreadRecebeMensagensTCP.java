/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import others.Constantes;
import others.InformaçaoUtlizador;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class TreadRecebeMensagensTCP extends Thread implements Constantes {

    private Cliente cliente;

    public TreadRecebeMensagensTCP(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        String request;
        int pedido;
        String resposta = "";
        ServerSocket aux = null;
        try {
            // aux = new ServerSocket(cliente.PORTTCPMENSAGENS);
            aux = new ServerSocket(cliente.PORTTCPMENSAGENS);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Lista Mensagens no porto:  " + aux.getLocalPort() + " ...");

        while (cliente.THREAD_ON) {

            try {
                toClientSocket = aux.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return;
            }

            try {

                in = new ObjectInputStream(toClientSocket.getInputStream());
                pedido = (int) in.readObject();

                request = (String) in.readObject();
                System.out.println("Mensagem Servidor: " + request);
                switch (request) {
                    case DISCONNECT:
                        System.out.println("Servidor foi abaixo, disconect necessario");
                        cliente.DisconectarCliente();
                        break;
                    case BD_ATUALIZADA:

                        List<InformaçaoUtlizador> listaActualizada = (List<InformaçaoUtlizador>) in.readObject();
                        cliente.setInformacaoServidor(listaActualizada);
                        System.out.println("Informacao atualizada com Sucesso ");
                }

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
        } //while
    }

}
