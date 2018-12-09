/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class RegistrationRequest extends Thread {

    private final Server server;

    public RegistrationRequest(Server serverRegistration) {
        this.server = serverRegistration;
    }

    @Override
    public void run() {
        Socket toClientSocket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Registo request;
        String resposta = "";

        if (server.socketRegista == null) {
            return;
        }

        System.out.println("Reiista Server iniciado no porto " + server.socketRegista.getLocalPort() + " ...");

        while (true) {

            try {
                toClientSocket = server.socketRegista.accept();
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
                PreparedStatement st = null;
                try {
                    st = server.conn.prepareStatement("INSERT INTO Utilizador(Username,Password,PortoUDP,PortoTCP,IpUtilizador,Estado) VALUES(?,?,?,?,?,?)");
                } catch (SQLException ex) {
                    Logger.getLogger(RegistrationRequest.class.getName()).log(Level.SEVERE, null, ex);
                }
                st.setString(1, request.getUserName());
                st.setString(2, request.getPassWord());
                st.setInt(3,request.getPortoTCP() );
                st.setInt(4,request.getPortoUDP());
                st.setString(5, request.getIpUtilzador());
                st.setBoolean(6, false);
                st.executeUpdate();

                server.Registos.add(request);
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
            } catch (SQLException ex) {
                Logger.getLogger(RegistrationRequest.class.getName()).log(Level.SEVERE, null, ex);
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

}
