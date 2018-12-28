/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import others.Autenticacao;
import others.Registo;
import others.Constantes;
import others.DadosDowload;
import others.Ficheiro;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import others.Historico;

/**
 *
 * @author Pedro
 */
public class TreadPedidos extends Thread implements Constantes {

    private final Server server;

    public TreadPedidos(Server server) {
        this.server = server;
    }

    @Override
    public void run() {

        Socket socketToCleinte = null;
        ObjectInputStream in = null;
        ObjectOutputStream on = null;
        int pedido;

        String resposta = "";

        if (server.socketPedidos == null) {
            return;
        }

        try {
            while (true) {

                try {
                    socketToCleinte = server.socketPedidos.accept();
                } catch (IOException ex) {
                    System.out.println("Ocorreu uma excepcao no socket enquanto aguardava por um pedido de ligacao: \n\t" + ex);
                    System.out.println("O servidor vai terminar...");
                    return;
                }

                try {
                    in = new ObjectInputStream(socketToCleinte.getInputStream());
                    on = new ObjectOutputStream(socketToCleinte.getOutputStream());

                    pedido = (Integer) in.readObject();

                    switch (pedido) {
                        case PEDIDO_REGISTO:
                            System.out.println("Pedido Registo");
                            Registo novoRegisto = (Registo) in.readObject();
                            adicionaRegisto(novoRegisto);
                            resposta = "Registado";
                            break;

                        case PEDIDO_AUTENTICACAO:
                            System.out.println("Pedido Auteticaçao");
                            Autenticacao novaAutenticacao = (Autenticacao) in.readObject();
                            if (!verificaRegistoNaBaseDados(novaAutenticacao)) {
                                resposta = "nao esta autenticado";
                                break;
                            }
                            // this.verificaRegistoNaBaseDados(novaAutenticacao, true);
                            this.server.actualizaBaseDados(novaAutenticacao, true);
                            resposta = AUTENTICADO;

                            break;

                        case DISPONIBLIZAR_FICHEIROS:
                            System.out.println("Pedido para Disponiblizar Ficheiro");
                            Autenticacao cliUtlizador = (Autenticacao) in.readObject();
                            System.out.println("Utlizador: " + cliUtlizador.getUsername() + " disponiblizou ficheiro");
                            Ficheiro ficheiroin = (Ficheiro) in.readObject();
                            if (cliUtlizador != null && !this.server.verificaExistenciaNomeFicheiro(ficheiroin.getNome())) {
                                adicionaFicheiro(ficheiroin, cliUtlizador);
                                resposta = "ficheirosAdiconados";
                            } else {
                                resposta = "Existe um ficheiro com o mesmo nome";
                            }
                            break;

                        case DOWLOAD_FICHEIRO:
                            System.out.println("Pedido para DowloadFicheiro");
                            Autenticacao cliUtlizador1 = (Autenticacao) in.readObject();
                            String ficheiro = (String) in.readObject();
                            DadosDowload dados = this.server.getDadosParaDowload(ficheiro);
                            if (dados != null) {
                                System.out.println("Escrever dados");
                                on.writeObject(dados);
                                AdicionaHistorico(ficheiro, cliUtlizador1);
                                resposta = "DowloadPedido";
                                break;
                            }
                            resposta = "Ficheiro Nao existe";
                            break;

                        case DISCONECTAR:
                            System.out.println("Pedido DISCONECT");
                            Autenticacao cliUtlizador2 = (Autenticacao) in.readObject();
                            if (!VerificaRegisto(cliUtlizador2)) {
                                resposta = "nao esta autenticado";
                                break;
                            }
                            this.server.actualizaBaseDados(cliUtlizador2, false);
                            //this.verificaRegistoNaBaseDados(cliUtlizador2, false);
                            resposta = "DISCONECTADO";
                            break;

                        case REFRECAR_BD:
                            System.out.println("Base dados foi atualizada por outro servidor");
                            this.server.enviaMensagensUdpServidorActualizada = false;
                            this.server.ActualizaListasDados();
                            resposta = "BD actualiazda no seu servidor";
                            break;

                        case ELEMINAR_FICHEIRO:
                            Autenticacao cliUtlizador3 = (Autenticacao) in.readObject();
                            String ficheiroNome = (String) in.readObject();
                            System.out.println("Utlizador:" + cliUtlizador3.getUsername() + " eleminou um ficheiro" + ficheiroNome + " disponiblizado");
                            this.EleminaFicheiro(cliUtlizador3, ficheiroNome);
                            resposta = "ficheiro eleminado";
                            break;

                        default:
                            System.out.println("Pedido estranho");
                            resposta = "PedidoEstranho";
                            break;
                    }

                    on.writeObject(resposta);
                    on.flush();

                } catch (IOException ex) {
                    System.out.println("Ocorreu a excepcao de E/S: \n\t" + ex);
                } catch (ClassNotFoundException ex) {
                    System.out.println("Pedido recebido de tipo inesperado:\n\t" + ex);
                }

            }

        } finally {

            try {
                if (socketToCleinte != null) {
                    socketToCleinte.close();
                    System.out.println("fecha socket");
                }
            } catch (IOException e) {
            }
        }
    }

    private boolean VerificaRegisto(Autenticacao autenticacao) {

        for (int i = 0; i < server.registos.size(); i++) {
            if (autenticacao.getUsername().compareTo(server.registos.get(i).getUserName()) == 0
                    && autenticacao.getPassword().compareTo(server.registos.get(i).getPassWord()) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean verificaRegistoNaBaseDados(Autenticacao autenticacao) {
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            st = this.server.conn.prepareStatement("select * from utilizador where Username='" + autenticacao.getUsername() + "' and Password='" + autenticacao.getPassword() + "'");
            res = st.executeQuery();
            if (res.next()) {
                System.out.println("econtrei um");
                //this.server.actualizaBaseDados(autenticacao, desejo, res.getInt(1));
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            //return false;
        }
        return false;
    }

    public boolean adicionaRegisto(Registo novo) {

        PreparedStatement st = null;
        try {
            st = server.conn.prepareStatement("INSERT INTO Utilizador(Username,Password,PortoUDP,PortoTCP,IpUtilizador,Estado,PortoTCPdowload) VALUES(?,?,?,?,?,?,?)");
            st.setString(1, novo.getUserName());
            st.setString(2, novo.getPassWord());
            st.setInt(3, novo.getPortoTCP());
            st.setInt(4, novo.getPortoUDP());
            st.setString(5, novo.getIpUtilzador());
            st.setBoolean(6, false);
            st.setInt(7, novo.getPortoTCPdowload());
            st.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("erro na query");
        }
        server.registos.add(novo);

        return true;
    }

    public boolean adicionaFicheiro(Ficheiro ficheiro, Autenticacao pedidoCliente) {
        int idCliente = this.server.getIdRegisto(pedidoCliente.getUsername(), pedidoCliente.getPassword());
        PreparedStatement st = null;

        try {

            st = server.conn.prepareStatement("INSERT INTO ficheiro (Nome,Directoria,idUtilizador) VALUES(?,?,?)");
            st.setString(1, ficheiro.getNome());
            st.setString(2, ficheiro.getDirectoria());
            st.setInt(3, idCliente);
            st.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("erro na query");
            return false;
        }
        server.getRegistoid(idCliente).addFicheiro(ficheiro);
        return true;

    }

    public boolean EleminaFicheiro(Autenticacao donoFicheiro, String nomeFicheiro) {
        int idCliente = this.server.getIdRegisto(donoFicheiro.getUsername(), donoFicheiro.getPassword());
        PreparedStatement st = null, st1 = null;
        ResultSet res = null;
        int idFicheiro = this.server.getIdFicheiro(idCliente, nomeFicheiro);
        System.err.println("id ficheiro retornado com o id: " + idFicheiro);
        if (idFicheiro >= 0) {
            try {
                st = this.server.conn.prepareStatement("DELETE FROM ficheiro WHERE idFicheiro=" + idFicheiro + "");
                st.executeUpdate();
            } catch (SQLException ex) {
                System.err.println("erro na query");

                return false;
            }
        } else {
            System.out.println("Ficheiro n existe");
            return false;
        }
        this.server.getRegistoid(idCliente).EleminaFicheiroRegisto(nomeFicheiro);
        return true;
    }

    public boolean AdicionaHistorico(String nomeFichero, Autenticacao destinoFicheiro) {
        int idOrigemFicheiro = this.server.getRegistoDonoFicheiro(nomeFichero).getIdUtlizador();
        int idDestinoFicheiro = this.server.getIdRegisto(destinoFicheiro.getUsername(), destinoFicheiro.getPassword());
        PreparedStatement st = null;
        if (idDestinoFicheiro >= 0 && idOrigemFicheiro >= 0) {
            try {

                st = server.conn.prepareStatement("INSERT INTO historico (NomeFicheiro,UsernameOrigem,UsernameDestino,idUtilizadorDestino) VALUES(?,?,?,?)");
                st.setString(1, nomeFichero);
                st.setString(2, this.server.getRegistoid(idOrigemFicheiro).getUserName());
                st.setString(3, this.server.getRegistoid(idDestinoFicheiro).getUserName());
                st.setInt(4, idDestinoFicheiro);
                st.executeUpdate();

            } catch (SQLException ex) {
                System.err.println("erro na query");
                return false;
            }
            //adionar historico ao destino do ficheiro

            this.server.getRegistoid(idDestinoFicheiro).addHistorico(new Historico(nomeFichero, this.server.getRegistoid(idOrigemFicheiro).getUserName(),
                    this.server.getRegistoid(idDestinoFicheiro).getUserName(),idDestinoFicheiro));

            return true;
        }
        System.err.println("erro ao econtrar destino e origem");
        return false;
    }

}
