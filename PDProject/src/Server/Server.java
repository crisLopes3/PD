package Server;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Constantes {

    public static final int TIMEOUT = 10;
    public ServerSocket socketAutenticacao;
    public ServerSocket socketRegista;
    public List<Registo> Registos = new ArrayList<>();
    public List<Autenticacao> inClientes = new ArrayList<>();
    public Connection conn;

    public Server(String args[], boolean debug) {

        socketRegista = null;
        try {
            socketRegista = new ServerSocket(6004);
            socketAutenticacao = new ServerSocket(6005);
            // ActualizaListasDados();
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na criação do socket de escuta:\n\t" + e);
            socketRegista = null;
        }
    }

    public boolean lancaThreads() {
        Thread t1 = new RegistrationRequest(this);
        t1.setDaemon(true);
        t1.start();

        Thread t2 = new AuthenticationRequest(this);
        t2.setDaemon(true);
        t2.start();

        return t1 != null && t2 != null;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Server timeServer = new Server(args, true);

        try {
            System.out.println("Connected");
            timeServer.conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.err.print(e);
        }

        timeServer.lancaThreads();
        timeServer.ActualizaListasDados();
        try {
            System.out.println("Comando:");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {

                String cast = in.readLine();

                if (cast.equals("exit")) {
                    return;
                }
                if (cast.equals("lista")) {
                    timeServer.ListaRegistos();
                }
                if (cast.equals("envia")) {
                    timeServer.enviarMensagensUtilizadores("ola");
                }
            }
        } catch (Exception e) {

        }

    }

    public void setRegistos(List<Registo> Registos) {
        this.Registos = Registos;
    }

    public void ActualizaListasDados() {
        PreparedStatement st = null;
        ResultSet res = null;
        List<Registo> aux = new ArrayList<>();
        try {
            st = this.conn.prepareStatement("SELECT * FROM utilizador");
            res = st.executeQuery();

            while (res.next()) {
                Registo aux1;
                aux1 = new Registo(res.getInt(1), res.getString(2), res.getString(3), res.getInt(4), res.getInt(5), res.getString(6), res.getBoolean(7));
                System.out.println(aux.toString());
                aux.add(aux1);
            }
            this.setRegistos(aux);
            enviarMensagensUtilizadores("BD actualiazada");
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            //return false;
        }
        //return true;
    }

    public boolean enviarMensagensUtilizadores(String msg) {

        for (Registo Registo : Registos) {
            if (Registo.getEstado() == true) {
                try {
                    enviarMensagemUtlizador(Registo.getIdUtlizador(), msg);
                } catch (IOException ex) {
                    System.out.println("Erro ao enviar mensagem para utilizador com o id: " + Registo.getUserName());
                }
            }
        }

        return true;

    }

    public boolean enviarMensagemUtlizador(int id, String msg) throws IOException {

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket = null;
        Registo aux = getRegistoid(id);

        if (aux != null) {
            try {
                socket = new Socket("localhost", aux.getPortoTCP());
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(msg);
                out.flush();
            } catch (UnknownHostException e) {
                System.out.println("Destino desconhecido:\n\t" + e);
            } catch (NumberFormatException e) {
                System.out.println("O porto do servidor deve ser um inteiro positivo.");
            } catch (SocketTimeoutException e) {
                System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
        return false;

    }

    public Registo getRegistoid(int id) {
        for (Registo Registo : Registos) {
            if (Registo.getIdUtlizador() == id) {
                return Registo;
            }
        }
        return null;
    }

    public int getIdRegisto(String username, String password) {
        for (int i = 0; i < this.Registos.size(); i++) {
            if (Registos.get(i).getUserName().compareTo(username) == 0 && Registos.get(i).getPassWord().compareTo(password) == 0) {
                return Registos.get(i).getIdUtlizador();
            }
        }
        return -1;
    }

    public void ListaRegistos() {
        for (Registo Registo : Registos) {
            System.out.println(Registo.toString());
        }
    }

}
