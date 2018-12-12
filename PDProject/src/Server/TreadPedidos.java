/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class TreadPedidos extends Thread {

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
        Autenticacao novaAutenticacao;
        Registo novoRegisto;
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
                        case 1:
                            System.out.println("Pedido 1");
                            ///in = new ObjectInputStream(socketToCleinte.getInputStream());
                            novoRegisto = (Registo) in.readObject();
                            adicionaRegisto(novoRegisto);
                            resposta += "Registado";
                            break;
                        case 2:
                            System.out.println("Pedido 2");
                            //in = new ObjectInputStream(socketToCleinte.getInputStream());
                            novaAutenticacao = (Autenticacao) in.readObject();
                            if (!VerificaRegisto(novaAutenticacao)) {
                                resposta += "nao esta autenticado";
                                break;
                            }
                            actualizaBaseDados(novaAutenticacao);
                            resposta += "Autenticado";
                            break;

                        default:
                            System.out.println("Pedido estranho");
                            resposta += "PedidoEstranho";
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

    public boolean actualizaBaseDados(Autenticacao aux) {

        PreparedStatement st = null;
        ResultSet res = null;

        try {
            st = server.conn.prepareStatement("UPDATE utilizador SET utilizador.Estado=" + true + " WHERE utilizador.idUtilizador=" + this.server.getIdRegisto(aux.getUsername(), aux.getPassword()) + ""); // t
            st.executeUpdate();
            this.server.ActualizaListasDados();

            System.out.println("entrou no actualiza");

        } catch (SQLException ex) {
            System.out.println(" n entrou");
            return false;
        }

        return true;
    }

    private boolean VerificaRegisto(Autenticacao autenticacao) {

        for (int i = 0; i < server.Registos.size(); i++) {
            if (autenticacao.getUsername().compareTo(server.Registos.get(i).getUserName()) == 0
                    && autenticacao.getPassword().compareTo(server.Registos.get(i).getPassWord()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean adicionaRegisto(Registo novo) {

        PreparedStatement st = null;
        try {
            st = server.conn.prepareStatement("INSERT INTO Utilizador(Username,Password,PortoUDP,PortoTCP,IpUtilizador,Estado) VALUES(?,?,?,?,?,?)");

            st.setString(1, novo.getUserName());
            st.setString(2, novo.getPassWord());
            st.setInt(3, novo.getPortoTCP());
            st.setInt(4, novo.getPortoUDP());
            st.setString(5, novo.getIpUtilzador());
            st.setBoolean(6, false);
            st.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("erro na query");
        }
        server.Registos.add(novo);
        
        return true;
    }
}
