/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Pedro
 */
public class TreadClienteEnviarFicheiro extends Thread {

    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5; //segundos
    public Socket socket;
    //public DadosDowload dados;

    public TreadClienteEnviarFicheiro(Socket sockets) {
        this.socket = sockets;
        // this.dados = dados;
    }

    @Override
    public void run() {

        BufferedReader in;
        OutputStream out;
        byte[] fileChunck = new byte[MAX_SIZE];
        int nbytes;
        String NomeFicheiro, caminho, DiretoriaFicheiro = null;

        //System.out.println("Passei 1: ");
        FileInputStream requestedFileInputStream = null;
 
        try {

            socket.setSoTimeout(1000 * TIMEOUT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();
            System.out.println("Passei aqui 1: ");
            caminho = in.readLine();
            NomeFicheiro = in.readLine();
            System.out.println("Passei aqui 2: ");
            System.out.println("Recebido pedido para: " + NomeFicheiro);
            File localDirectory = new File(caminho);

            DiretoriaFicheiro = new File(localDirectory + File.separator + NomeFicheiro).getCanonicalPath();
            System.out.println("Passei aqui 3: ");
            if (!DiretoriaFicheiro.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                System.out.println("Nao e' permitido aceder ao ficheiro " + DiretoriaFicheiro + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");

            }
            System.out.println("Passei aqui 4: ");
            requestedFileInputStream = new FileInputStream(DiretoriaFicheiro);
            System.out.println("Ficheiro " + DiretoriaFicheiro + " aberto para leitura.");
            System.out.println("Passei aqui 5: ");
            while ((nbytes = requestedFileInputStream.read(fileChunck)) > 0) {
                System.out.println("Escrever Bytes: ");
                out.write(fileChunck, 0, nbytes);
                out.flush();

            }

            System.out.println("Transferencia concluida");

        } catch (FileNotFoundException e) {   //Subclasse de IOException                 
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + DiretoriaFicheiro + "!");
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        }

        if (requestedFileInputStream != null) {
            try {
                requestedFileInputStream.close();
            } catch (IOException ex) {
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
        }

    } //while(true)

}
