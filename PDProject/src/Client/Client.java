package Client;

import Server.Autenticacao;
import Server.Registo;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static boolean REGISTO = false;
    public static boolean AUTENTICADO = false;

    public static final int TIMEOUT = 10; //segundos

    public static final Boolean PedidoDeRegisto() throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        try {

            socket = new Socket("localhost", 6001);
            socket.setSoTimeout(TIMEOUT * 1000);

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(new Registo("cris", "pass234"));
            out.flush();

            response = (String) in.readObject();

            if (response.equals("registao")) {
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

    public static final Boolean PedidoDeAutenticacao() throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        try {

            socket = new Socket("localhost", 6002);
            socket.setSoTimeout(TIMEOUT * 1000);

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(new Autenticacao(new Registo("cris", "pass234"), InetAddress.getByName("localhost")));
            out.flush();

            response = (String) in.readObject();

            if (response.equals("autenticado")) {
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

    public static void main(String[] args) throws IOException {

        System.out.println("Comando:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

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

            }
        }
    }
}
