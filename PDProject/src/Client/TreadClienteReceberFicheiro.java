/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import others.DadosDowload;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author Pedro
 */
public class TreadClienteReceberFicheiro extends Thread {

    public static final int MAX_SIZE = 4000;
    public static int TIMEOUT = 10; //segundos 
    private DadosDowload dados;
    private Cliente utilzador;

    public TreadClienteReceberFicheiro(DadosDowload dados, Cliente cliente) {
        this.dados = dados;
        this.utilzador = cliente;
    }

    @Override
    public void run() {

        File localDirectory = new File(utilzador.DESTINOFICHEIRO.trim());
        Socket socket = null;
        String localFilePath = null;
        FileOutputStream localFileOutputStream = null;
        InetAddress serverAddr;
        int serverPort;

        try {
            try {

                localFilePath = localDirectory.getCanonicalPath() + File.separator + dados.getNomeFicheiroDono();
                localFileOutputStream = new FileOutputStream(localFilePath);
                System.out.println("Ficheiro " + localFilePath + " criado.");

            } catch (IOException e) {

                if (localFilePath == null) {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao obter o caminho canonico para o ficheiro local!");
                } else {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao tentar criar o ficheiro " + localFilePath + "!");
                }

                return;
            }
            try {

                serverAddr = InetAddress.getByName(dados.getIpDonoFicheiro());
                serverPort = dados.getTCPDonoFicheiro();
                System.out.println("vou ligar me ao dono: " + serverAddr + ", " + serverPort);
                socket = new Socket(serverAddr, serverPort);
                socket.setSoTimeout(TIMEOUT * 1000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Eviar pedido de ficheiro com o nome:  " + dados.getNomeFicheiroDono());
                out.println(dados.getDirectoriaDonoFicheiro());
                out.flush();
                out.println(dados.getNomeFicheiroDono());
                out.flush();

                byte reader[] = new byte[MAX_SIZE];
                int nbytes;
                System.out.println("Dowaload Inciado: ");
                do {

                    nbytes = socket.getInputStream().read(reader);

                    // if (socket.getPort() == serverPort && socket.getLocalAddress().equals(serverAddr)) {
                    //System.out.println("Recebido o bloco n. " + ++contador + " com " + packet.getLength() + " bytes.");
                    if (nbytes > 0) {
                        localFileOutputStream.write(reader, 0, nbytes);
                    }
                    //System.out.println("Acrescentados " + packet.getLength() + " bytes ao ficheiro " + localFilePath+ ".");
                    //  }

                } while (nbytes > 0);

                System.out.println("Transferencia concluida.");

            } catch (UnknownHostException e) {
                System.out.println("Destino desconhecido:\n\t" + e);
            } catch (NumberFormatException e) {
                System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
            } catch (SocketTimeoutException e) {
                System.out.println("Nao foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
            } catch (SocketException e) {
                System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao socket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
            }

        } finally {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Erro ao fechar socket no dowload");
                }
            }

            if (localFileOutputStream != null) {
                try {
                    localFileOutputStream.close();
                } catch (IOException e) {
                }
            }

        }

    }

}
