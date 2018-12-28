/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import others.DadosDowload;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class TreadPedidoFicheiroCliente extends Thread {

    private Cliente cliente;
    private DadosDowload dados;

    public TreadPedidoFicheiroCliente(Cliente cliente) {
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
            // aux = new ServerSocket(cliente.PORTTCPDOWLOAD);
            aux = new ServerSocket(cliente.PORTTCPDOWLOAD);

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Pedir dowloads no porto:  " + aux.getLocalPort() + " ...");
        while (cliente.THREAD_ON) {

            try {
                toClientSocket = aux.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return;
            }

            Thread enviaFicheiro;
//            System.err.println("Teste de dados enviados: "+dados.getNomeFicheiroDono()+" "+dados.getDirectoriaDonoFicheiro());
            enviaFicheiro = new TreadClienteEnviarFicheiro(toClientSocket);
            enviaFicheiro.setDaemon(true);
            System.out.println("vou inicar tread que envia ficheiro");
            enviaFicheiro.start();


        }
//            if (toClientSocket != null) {
//                try {
//                    toClientSocket.close();
//                } catch (IOException ex) {
//                    System.out.println("Erro ao fechar socket na tread pedidosFicheiroCliente");
//                }
//            }
    } //while
}
