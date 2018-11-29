package Server;

import java.net.*;
import java.io.*;

public class Server {

    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";

    private ServerSocket socket;

    public Server(String args[], boolean debug) {

        socket = null;
        try {
            socket = new ServerSocket(6001);
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na criação do socket de escuta:\n\t" + e);
            socket = null;
        }
    }

    public final void processRequests() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Registo request;
        String resposta = "";

        if (socket == null) {
            return;
        }

        System.out.println("TCP Serialized Time Server iniciado no porto " + socket.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = socket.accept();
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

                //Constroi a resposta terminando-a com uma mudanca de lina
                resposta += "Autenticado";

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

    public static void main(String[] args) {
        Server timeServer;

        timeServer = new Server(args, true);
        timeServer.processRequests();
    }
}
