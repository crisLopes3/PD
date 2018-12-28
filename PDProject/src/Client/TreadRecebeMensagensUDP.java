/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.Cliente.IPSERVIDOR;
import static Client.Cliente.PORT_SERVIDOR_TCP_ESCUTA;
import static Client.Cliente.TIMEOUT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import others.Constantes;
import static others.Constantes.DOWLOAD_FICHEIRO;

/**
 *
 * @author Pedro
 */
public class TreadRecebeMensagensUDP extends Thread implements Constantes {

    private Cliente cliente;
    private int MAX_SIZE = 4000;
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private boolean debug;

    public TreadRecebeMensagensUDP(Cliente cliente) {
        this.cliente = cliente;
        try {
            socket = new DatagramSocket(this.cliente.PORTUDP);
        } catch (SocketException ex) {
            System.out.println("Erro na criaçao do socket");
        }
        debug = true;
    }

    @Override
    public void run() {

        String MensagemOutroServidor, MensagemMeuServidor;
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        if (socket == null) {
            return;
        }

        if (debug) {
            System.out.println("UDP Time Server iniciado...");
        }

        while (cliente.THREAD_ON) {

            MensagemOutroServidor = waitDatagram();
            System.out.println("Mensagem Servidor: " + MensagemOutroServidor);
            switch (MensagemOutroServidor) {
                case BD_ATUALIZADA:

                    System.out.println("BD_atualizada refrescar informaçao: ");
                     {
                        try {
                            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
                            socket.setSoTimeout(TIMEOUT * 1000);

                            out = new ObjectOutputStream(socket.getOutputStream());
                            in = new ObjectInputStream(socket.getInputStream());

                            out.writeObject(DOWLOAD_FICHEIRO);
                            out.flush();

                            MensagemMeuServidor = (String) in.readObject();

                            System.out.println(MensagemMeuServidor);
                        } catch (IOException ex) {
                            System.out.println("Erro ao enviar a mensagem ao meu servidor de que precisa de refrecar a base dados");
                        } catch (ClassNotFoundException ex) {
                            System.out.println("objeto recebido n é o pretendido");
                        }
                    }

                    break;
                default:
                    System.out.println("Mensagem Servidor: " + MensagemOutroServidor);
            }

        }

    }

    public String waitDatagram() {
        String request = "";

        if (socket == null) {
            return null;
        }
        try {
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            System.out.println("A espera de receber Datagram: ");
            socket.receive(packet);
            System.out.println("Recebido datagram: ");
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            request = (String) in.readObject();

            if (debug) {
                System.out.println("Recebido \"" + request + "\" de "
                        + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            }
        } catch (IOException ex) {
            System.out.println("erro: " + ex);
        } catch (ClassNotFoundException e) {
            System.out.println("erro ao receber pedido");
        }
        return request;

    }

}
