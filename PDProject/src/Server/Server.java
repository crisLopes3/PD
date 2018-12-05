package Server;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Constantes{

    public ServerSocket socketAutenticacao;
    public ServerSocket socketRegista;
    public List<Registo> Registos = new ArrayList<>();
    public List<Autenticacao> inClientes = new ArrayList<>();

    public Server(String args[], boolean debug) {

        socketRegista = null;
        try {
            socketRegista = new ServerSocket(6001);
            socketAutenticacao=new ServerSocket(6002);
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na criação do socket de escuta:\n\t" + e);
            socketRegista = null;
        }
    }
    
    public boolean lancaThreads(){
        Thread t1=new RegistrationRequest(this);
        t1.setDaemon(true);
        t1.start();
        
        Thread t2=new AuthenticationRequest(this);
        t2.setDaemon(true);
        t2.start();
        
        return t1!=null && t2!=null;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Server timeServer;

        Connection conn=null;
        
        try{
            System.out.println("Connected");
            conn=DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
            System.out.println("Connected");
        }catch(SQLException e){
            System.err.print(e);
        }
        
//        timeServer = new Server(args, true);
//        timeServer.lancaThreads();
        
//        try {
//            System.out.println("Comando:");
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//           
//            while (true) {
//
//                String cast = in.readLine();
//
//                if (cast.equals("exit")) {
//                    return;
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        
    }

}
