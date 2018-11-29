package Server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket socketAutenticacao;
    private ServerSocket socketRegista;
    private List<Registo> Registos = new ArrayList<Registo>();
    private List<Autenticacao> inClientes = new ArrayList<Autenticacao>();

    public Server(String args[], boolean debug) {

        socketRegista = null;
        try {
            socketRegista = new ServerSocket(6001);
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na criação do socket de escuta:\n\t" + e);
            socketRegista = null;
        }
    }

    public final void RegistaCliente() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Registo request;
        String resposta = "";

        if (socketRegista == null) {
            return;
        }

        System.out.println("TCP Serialized Time Server iniciado no porto " + socketRegista.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = socketRegista.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return;
            }

            try {

                out = new ObjectOutputStream(toClientSocket.getOutputStream());
                in = new ObjectInputStream(toClientSocket.getInputStream());

                request = (Registo) (in.readObject());

                if (request == null) { //EOF
                    toClientSocket.close();
                    continue; //to next client request
                }
                Registos.add(request);
                //Constroi a resposta terminando-a com uma mudanca de lina
                resposta += "Registado";

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
        } //while
    }

    public final boolean RecebeAutenticacao() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Autenticacao request;
        String resposta = "";

        if (socketAutenticacao == null) {
            return false;
        }

        System.out.println("TCP Serialized Time Server iniciado no porto " + socketAutenticacao.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = socketAutenticacao.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return false;
            }

            try {

                out = new ObjectOutputStream(toClientSocket.getOutputStream());
                in = new ObjectInputStream(toClientSocket.getInputStream());

                request = (Autenticacao) (in.readObject());

                if (request == null || !VerificaRegisto(request.getRegisto())) { //EOF
                    toClientSocket.close();
                    continue; //to next client request
                }
                inClientes.add(request);
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
    private final boolean VerificaRegisto(Registo registo){
        
        for (Registo Registo : Registos) {
            if(Registo.equals(registo))
                return true;
            
        }
        return false;
    }
    public static void main(String[] args) {
        Server timeServer;

        timeServer = new Server(args, true);
        timeServer.RegistaCliente();
    }
}
