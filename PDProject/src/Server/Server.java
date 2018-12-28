package Server;

import others.InformaçaoUtlizador;
import others.Autenticacao;
import others.Registo;
import others.Constantes;
import others.DadosDowload;
import others.Ficheiro;
import java.net.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import others.Historico;

public class Server implements Constantes {

    public static final int TIMEOUT = 10;
    public ServerSocket socketPedidos;
    public List<Registo> registos = new ArrayList<>();
    public Connection conn;
    public List<Autenticacao> inServidor = new ArrayList<>();
    public boolean enviaMensagensUdpServidorActualizada = true;
    public int TCPServidor = 6002;
    public ServidorRmi servidorRmi;

    public Server(String args[], boolean debug) throws RemoteException {

        try {
            socketPedidos = new ServerSocket(TCPServidor);
            // ActualizaListasDados();
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na criação do socket de escuta:\n\t" + e);
            socketPedidos = null;
        }
        servidorRmi = new ServidorRmi(this);
        
    }

    public boolean lancaThreads() {
        servidorRmi.InicioServidorRmi();
        System.out.println("Lancou");
        Thread t1 = new TreadPedidos(this);
        t1.setDaemon(true);
        t1.start();

        return true;
    }

    public void setRegistos(List<Registo> Registos) {
        this.registos = Registos;
    }

    public void ActualizaListasDados() {
        PreparedStatement st = null, st1 = null, st2 = null;
        ResultSet res = null, res1 = null, res2 = null;
        List<Registo> aux = new ArrayList<>();
        try {
            st = this.conn.prepareStatement("SELECT * FROM utilizador");
            res = st.executeQuery();

            while (res.next()) {
                Registo novo;
                novo = new Registo(res.getInt(1), res.getString(2), res.getString(3), res.getInt(4), res.getInt(5), res.getString(6), res.getBoolean(7), res.getInt(8));
                st1 = this.conn.prepareStatement("SELECT * FROM ficheiro where idUtilizador=" + novo.getIdUtlizador());
                res1 = st1.executeQuery();
                while (res1.next()) {
                    novo.addFicheiro(new Ficheiro(res1.getInt(1), res1.getString(2), res1.getString(3), res1.getInt(4)));
                    System.out.println("Adiconei um ficheiro:");
                }
                st2 = this.conn.prepareStatement("SELECT * FROM historico where idUtilizadorDestino=" + novo.getIdUtlizador());
                res2 = st2.executeQuery();
                while (res2.next()) {
                    novo.addHistorico(new Historico(res2.getInt(1), res2.getString(2), res2.getString(3), res2.getString(4), res2.getInt(5)));
                    System.out.println("Adiconei um historico:");
                }
                System.out.println(novo.toString());
                if (novo.getEstado()) {
                    aux.add(novo);
                }

            }
            this.setRegistos(aux);
            enviarMensagensUtilizadores(BD_ATUALIZADA);

            servidorRmi.EnviaInformacaoServidor(this.getListaInformaçaoUtlizadores());

        } catch (SQLException ex) {
            System.out.println("Erro ao carregar os registos");
            //return false;
        } catch (RemoteException ex) {
            System.out.println("Ocooreu aqui um erro ");
        }
        //return true;
    }

    //falta Testar a parte de udp para refrecar BD
    public boolean enviarMensagensUtilizadores(String msg) {
        System.out.println("Mandar Mensagens aos utlizadores:");
        for (Registo Registo : registos) {
            if (Registo.getEstado() == true) {
                if (verificaRegistoInServidor(Registo)) {
                    try {
                        System.out.println("enviar para o ultizador com o id: " + Registo.getIdUtlizador());
                        enviarMensagemUtlizadorInServidor(Registo.getIdUtlizador(), msg);
                    } catch (IOException ex) {
                        System.out.println("Erro ao enviar mensagem para utilizador com o id: " + Registo.getUserName());
                    }
                } else {
                    if (enviaMensagensUdpServidorActualizada) {
                        System.out.println("entrou aqui para enviar mensagens udp");
                        try {
                            DatagramSocket socket = new DatagramSocket();
                            socket.setSoTimeout(TIMEOUT * 1000);

                            ByteArrayOutputStream ArraySend = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(ArraySend);

                            out.writeObject(msg);
                            out.flush();
                            System.out.println("vou enviar Mensagem para o porto: " + Registo.getPortoUDP());
                            DatagramPacket packet = new DatagramPacket(ArraySend.toByteArray(), ArraySend.size(), InetAddress.getByName(Registo.getIpUtilzador()), Registo.getPortoUDP());

                            socket.send(packet);
                            System.out.println("Mensagem Enviada");
                        } catch (IOException ex) {
                            System.out.println("Erro ao enviar mensagem para utilizador via udp com o id: " + Registo.getUserName());
                        }
                    }
                    enviaMensagensUdpServidorActualizada = true;
                }
            }
        }

        return true;

    }

    public boolean verificaRegistoInServidor(Registo aux) {
        for (int i = 0; i < this.inServidor.size(); i++) {
            if (aux.getUserName().compareTo(inServidor.get(i).getUsername()) == 0 && aux.getPassWord().compareTo(inServidor.get(i).getPassword()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean enviarMensagemUtlizadorInServidor(int id, String msg) throws IOException {

        ObjectOutputStream out;
        Socket socket = null;
        Registo registo = getRegistoid(id);

        if (registo != null) {
            try {
                System.out.println("envia: " + registo.getIpUtilzador() + " " + registo.getPortoTCP());
                socket = new Socket(registo.getIpUtilzador(), registo.getPortoTCP());
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(MANDAR_MENSAGEM);
                out.flush();
                switch (msg) {
                    case BD_ATUALIZADA:
                        out.writeObject(msg);
                        out.flush();
                        out.writeObject(getListaInformaçaoUtlizadores());
                        out.flush();
                        break;
                    default:
                        out.writeObject(msg);
                        out.flush();
                }

            } catch (UnknownHostException e) {
                System.out.println("Destino desconhecido:\n\t" + e);
            } catch (NumberFormatException e) {
                System.out.println("O porto do servidor deve ser um inteiro positivo.");
            } catch (SocketTimeoutException e) {
                System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao socke ao enviar ao cliente:\n\t" + e);
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
        return false;

    }

    public List<InformaçaoUtlizador> getListaInformaçaoUtlizadores() {
        List<InformaçaoUtlizador> lista = new ArrayList<>();
        for (int i = 0; i < registos.size(); i++) {
            Registo registoIn = this.getRegistoid(this.getIdRegisto(registos.get(i).getUserName(), registos.get(i).getPassWord()));
            System.out.println("Registo retornada com o username: " + registoIn.getUserName());
            InformaçaoUtlizador utlizador = new InformaçaoUtlizador(registoIn.getUserName());
            for (Ficheiro ficheiro : registoIn.getListaFicheiros()) {
                utlizador.getListaFicheiros().add(ficheiro);
            }
            for (Historico historico : registoIn.getHistorico()) {
                utlizador.getHistorico().add(historico);
            }
            lista.add(utlizador);
        }
        return lista;
    }

    public Registo getRegistoid(int id) {
        for (Registo Registo : registos) {
            if (Registo.getIdUtlizador() == id) {
                return Registo;
            }
        }
        return null;
    }

    public int getIdRegisto(String username, String password) {
        for (int i = 0; i < this.registos.size(); i++) {
            if (registos.get(i).getUserName().compareTo(username) == 0 && registos.get(i).getPassWord().compareTo(password) == 0) {
                System.out.println("id retornado: " + registos.get(i).getIdUtlizador());
                return registos.get(i).getIdUtlizador();
            }
        }
        return -1;
    }

    public void listaRegistos() {
        for (Registo Registo : registos) {
            System.out.println(Registo.toString());
        }
    }

    public int getIdFicheiro(int idUtilizador, String nomeFicheiro) {
        Registo registo = this.getRegistoid(idUtilizador);
        System.err.println("entrei no procura ficheiro para procuar ficheiro com nome: " + nomeFicheiro);

        Ficheiro ficheiro = registo.getDadosFicheiro(nomeFicheiro);

        if (ficheiro != null) {
            return ficheiro.getIdficheiro();
        }
        System.err.println("id n econtrado ");
        return -1;
    }

    public void listaInformaçaoUtlizadors() {
        for (InformaçaoUtlizador informacao : this.getListaInformaçaoUtlizadores()) {
            System.out.println(informacao.toString());
        }
    }

    public Registo getRegistoDonoFicheiro(String nomeFicheiro) {
        System.out.println("entrei aqui para procurar dono");
        for (int i = 0; i < registos.size(); i++) {
            if (registos.get(i).ExisteFicheiro(nomeFicheiro)) {
                System.out.println("Econtrei");
                return registos.get(i);
            }
        }
        System.out.println("N econtrei");
        return null;
    }

    public DadosDowload getDadosParaDowload(String nomeFicheiro) {
        try {
            Registo dono = getRegistoDonoFicheiro(nomeFicheiro);
            System.out.println("Dono: " + dono.getIpUtilzador());
            Ficheiro ficheiro = dono.getDadosFicheiro(nomeFicheiro);
            System.out.println("Dono: " + dono.getIpUtilzador() + "ficheiro: " + ficheiro.getDirectoria());
            if (dono != null && ficheiro != null) {
                return (new DadosDowload(dono.getIpUtilzador(), dono.getPortoTCPdowload(), ficheiro.getDirectoria(), ficheiro.getNome()));
            } else {

                return null;
            }
        } catch (NullPointerException ex) {
            System.out.println("Erro ao econtrar os dados do dowload: ficheiro n existe");
            return null;
        }
    }

    public void disconectarAutenicacoesInServidor() {
        System.out.println("entrei aqui no disconect");
        for (int i = 0; i < inServidor.size(); i++) {
            try {

                this.enviarMensagemUtlizadorInServidor(this.getIdRegisto(inServidor.get(i).getUsername(), inServidor.get(i).getPassword()), DISCONNECT);
                actualizaBaseDados(inServidor.get(i), false);
            } catch (IOException ex) {
                System.out.println("Erro ao Disconectar o cliente: " + inServidor.get(i).getUsername());
            }
        }
        try {
            servidorRmi.CloseServidorRmi();
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean actualizaBaseDados(Autenticacao aux, boolean desejo) {

        PreparedStatement st = null, st1 = null;
        ResultSet res = null;
        int id = -1;
        try {
            st = this.conn.prepareStatement("select * from utilizador where Username='" + aux.getUsername() + "' and Password='" + aux.getPassword() + "'");
            res = st.executeQuery();
            if (res.next()) {

                id = res.getInt(1);
                System.out.println("econtrei um com id: " + id);
            }
            try {
                st = conn.prepareStatement("UPDATE utilizador SET utilizador.Estado=" + desejo + " WHERE utilizador.idUtilizador=" + id + ""); // t
                st.executeUpdate();
                if (!desejo) {
                    inServidor.remove(aux);
                } else if (desejo) {
                    System.out.println("adionei");
                    inServidor.add(aux);
                }
                this.ActualizaListasDados();

                System.out.println("entrou no actualiza");

            } catch (SQLException ex) {
                System.out.println(" n entrou");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("erro no select");
            return false;
        }
        return true;
    }

    public boolean verificaExistenciaNomeFicheiro(String nomeFicheiro) {
        for (Registo registo : registos) {
            if (registo.ExisteFicheiro(nomeFicheiro)) {
                return true;
            }
        }
        return false;
    }

}
