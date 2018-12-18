package Client;

import Server.Autenticacao;
import Server.Constantes;
import Server.Registo;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente extends Thread implements Constantes {

    public static boolean REGISTO = false;
    public static boolean AUTENTICADO = false;
    public static final int TIMEOUT = 10; //segundos 
    public static String USERNAME = "";
    public static String PASSWORD = "";

    public Cliente() {
    }

    public static final boolean PedidoDeRegisto() throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        System.out.println("Dados De Registo\n\n");
        System.out.println("Utruduza Username: ");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        USERNAME = input.readLine();
        System.out.println("Utruduza Password: ");
        PASSWORD = input.readLine();
        Registo novo = new Registo(0, USERNAME, PASSWORD, 6001, 6002, InetAddress.getLocalHost().getHostAddress()); // os portos é o cliente que escolhe

        try {

            socket = new Socket("10.65.145.71", 6001);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(PEDIDO_REGISTO);
            out.flush();

            out.writeObject(novo);
            out.flush();

            response = (String) in.readObject();

            if (response.equals(REGISTADO)) {
                REGISTO = true;
            }
            System.out.println(response);

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido nao e' do tipo esperado:\n\t" + e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return true;
    }

    public static final boolean PedidoDeAutenticacao() throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        System.out.println("Dados De Autenticaçao:");
        System.out.println("Utruduza Username: ");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String Username = input.readLine();
        System.out.println("Utruduza Password: ");
        String Password = input.readLine();

        Autenticacao aux = new Autenticacao(Username, Password);
        try {

            socket = new Socket("10.65.145.71", 6001);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(PEDIDO_AUTENTICACAO);
            out.flush();

            out.writeObject(aux);
            out.flush();

            response = (String) in.readObject();

            if (response.equals(AUTENTICADO)) {
                AUTENTICADO = true;

            }
            System.out.println(response);

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido nao e' do tipo esperado:\n\t" + e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return true;
    }

    public static final boolean DisponiblizarFicheiro() throws IOException {

        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;
        List<Ficheiro> listaFicheiro = new ArrayList<>();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Disponiblizar Ficheiros");
        do {
            System.out.println("Utruduza Nome Ficheiro: ");

            String Nome = input.readLine();
            System.out.println("Introduza Directoria Ficheiro ");
            String Directoria = input.readLine();
            Ficheiro novo = new Ficheiro(Nome, Directoria);
            listaFicheiro.add(novo);

            System.out.println("Se pretender para press <1>: ");

        } while (input.readLine() != "1");

        try {

            socket = new Socket("10.65.145.71", 6001);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(DISPONIBLIZAR_FICHEIROS);
            out.flush();

            out.writeObject(new Autenticacao(USERNAME, PASSWORD));
            out.flush();
            
            out.writeObject(listaFicheiro);
            out.flush();

            response = (String) in.readObject();

            if (response.equals(AUTENTICADO)) {
                AUTENTICADO = true;

            }
            System.out.println(response);

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido nao e' do tipo esperado:\n\t" + e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return true;

    }

    @Override
    public void run() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        String request;
        String resposta = "";
        ServerSocket aux = null;
        try {
            aux = new ServerSocket(6002);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Lista Mensagens no porto:  " + aux.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = aux.accept();
            } catch (IOException e) {
                System.out.println("Erro enquanto aguarda por um pedido de ligação:\n\t" + e);
                return;
            }

            try {

                in = new ObjectInputStream(toClientSocket.getInputStream());

                request = (String) (in.readObject());

                if (request == null) { //EOF
                    toClientSocket.close();
                    continue; //to next client request
                }

                System.out.println("Mensagens: " + request);
                //Constroi a resposta terminando-a com uma mudanca de lina
//                resposta += "Registado";
//
//                //Envia a resposta ao cliente
//                out.writeObject(resposta);
//                out.flush();

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

    public static void main(String[] args) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Thread t1 = new Cliente();
        t1.setDaemon(true);
        t1.start();
        while (true) {
            System.out.println("Comando:");
            switch (in.readLine()) {
                case "1":
                    if (!REGISTO) {
                        System.out.println("Pedido de Registo:");
                        PedidoDeRegisto();
                    }
                    break;
                case "2":
                    if (!AUTENTICADO) {
                        System.out.println("Pedido de autenticaçao:");
                        PedidoDeAutenticacao();
                    }
                    break;
                case "3":
                    if (!AUTENTICADO) {
                        System.out.println("Disponiblizar Ficheiro:");
                        DisponiblizarFicheiro();
                    }
                    break;

            }
        }
    }
}
