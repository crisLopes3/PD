package Client;

import others.DadosDowload;
import others.Ficheiro;
import others.Autenticacao;
import others.Constantes;
import others.InformaçaoUtlizador;
import others.Registo;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import others.Historico;

public class Cliente implements Constantes {

    public static boolean REGISTO = false;
    public static boolean IS_AUTENTICADO = false;
    public boolean THREAD_ON = true;
    public boolean exitCliente;
    public static int TIMEOUT = 10; //segundos 
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public String DESTINOFICHEIRO = "";
    public static int PORTTCPDOWLOAD = 6010;
    public static int PORTTCPMENSAGENS = 6008;
    public static int PORTUDP = 6009;
    public static String IPSERVIDOR = "";
    public static int PORT_SERVIDOR_TCP_ESCUTA = 6001;
    public TreadRecebeMensagensTCP recebeMensagensTCP;
    public TreadPedidoFicheiroCliente pedidosFicheiro;
    public TreadRecebeMensagensUDP recebeMensagensUDP;
    public TreadVerificaFicheiros verificaFicheiros;

    ///////Informaçao///////////////7
    public List<InformaçaoUtlizador> informacaoServidor;

    public Cliente(String ipServidor, String destinoFicheiro) {
        DESTINOFICHEIRO = destinoFicheiro;
        IPSERVIDOR = ipServidor;
        exitCliente = false;
        informacaoServidor = new ArrayList<>();
        recebeMensagensTCP = new TreadRecebeMensagensTCP(this);
        recebeMensagensTCP.setDaemon(true);
        recebeMensagensTCP.start();
        pedidosFicheiro = new TreadPedidoFicheiroCliente(this);
        pedidosFicheiro.setDaemon(true);
        pedidosFicheiro.start();
        recebeMensagensUDP = new TreadRecebeMensagensUDP(this);
        recebeMensagensUDP.setDaemon(true);
        recebeMensagensUDP.start();
        verificaFicheiros = new TreadVerificaFicheiros(this);
        verificaFicheiros.setDaemon(true);
        verificaFicheiros.start();

    }

    public boolean PedidoDeRegisto(String Username, String Password) throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        USERNAME = Username;
        PASSWORD = Password;
        Registo novo = new Registo(0, USERNAME, PASSWORD, PORTUDP, PORTTCPMENSAGENS, InetAddress.getLocalHost().getHostAddress(), PORTTCPDOWLOAD); // os portos é o cliente que escolhe

        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
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

    public boolean PedidoDeAutenticacao(String Username, String password) throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        USERNAME = Username;
        PASSWORD = password;

        Autenticacao aux = new Autenticacao(USERNAME, PASSWORD);
        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(PEDIDO_AUTENTICACAO);
            out.flush();

            out.writeObject(aux);
            out.flush();

            response = (String) in.readObject();

            if (response.compareTo(AUTENTICADO) == 0) {
                System.out.println("entrei aqui");
                IS_AUTENTICADO = true;
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

    public boolean PedidoDesconect() throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        Autenticacao aux = new Autenticacao(USERNAME, PASSWORD);
        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(DISCONECTAR);
            out.flush();

            out.writeObject(aux);
            out.flush();

            response = (String) in.readObject();

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

    //verficar melhor
    public boolean DisponiblizarFicheiro(String Nome, String Directoria) throws IOException {

        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        Ficheiro novo = new Ficheiro(Nome, Directoria);

        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(DISPONIBLIZAR_FICHEIROS);
            out.flush();

            out.writeObject(new Autenticacao(USERNAME, PASSWORD));
            out.flush();

            out.writeObject(novo);
            out.flush();

            response = (String) in.readObject();

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

    public boolean EleminarFicheiroDisponablizado(String nomeFicheiro) throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(ELEMINAR_FICHEIRO);
            out.flush();

            out.writeObject(new Autenticacao(USERNAME, PASSWORD));
            out.flush();

            out.writeObject(nomeFicheiro);
            out.flush();

            response = (String) in.readObject();

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

    public boolean DowloadFicheiro(String nomeFicheiro) throws IOException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String nomeFicneiro, response;
        DadosDowload dadosFicheiro;

        try {

            socket = new Socket(IPSERVIDOR, PORT_SERVIDOR_TCP_ESCUTA);
            socket.setSoTimeout(TIMEOUT * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(DOWLOAD_FICHEIRO);
            out.flush();

            out.writeObject(new Autenticacao(USERNAME, PASSWORD));
            System.out.println("Autenticaçao: " + USERNAME + " " + PASSWORD);
            out.flush();

            out.writeObject(nomeFicheiro);
            out.flush();

            dadosFicheiro = (DadosDowload) in.readObject();
            if (dadosFicheiro != null) {
                System.out.println("vou correr a tread para inciar Dowload: " + dadosFicheiro.getIpDonoFicheiro() + ": " + dadosFicheiro.getTCPDonoFicheiro());
                Thread tread = new TreadClienteReceberFicheiro(dadosFicheiro, this);
                tread.setDaemon(true);
                tread.start();
            }

            response = (String) in.readObject();

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

    public void DisconectarCliente() {
        // THREAD_ON=false;
        exitCliente = true;
    }

    public void setInformacaoServidor(List<InformaçaoUtlizador> informacaoServidor) {
        this.informacaoServidor = informacaoServidor;
    }

    public List<InformaçaoUtlizador> getInformacaoServidor() {
        return informacaoServidor;
    }

    public void listarInformacaoActualzada() {
        for (int i = 0; i < informacaoServidor.size(); i++) {
            System.out.println(informacaoServidor.get(i).toString());
        }
    }

    public void ListarHistorico() {
        for (int i = 0; i < informacaoServidor.size(); i++) {
            if (informacaoServidor.get(i).Username.compareTo(USERNAME) == 0) {
                for (Historico historico : informacaoServidor.get(i).getHistorico()) {
                    System.out.println(historico.toString());
                }
            }
        }
    }
}
