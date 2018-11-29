package Client;

import Server.Registo;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

     public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) throws IOException
    {
        
        InetAddress serverAddr = null;
        int serverPort = -1;
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;
        
//        if(args.length != 2){
//            System.out.println("Sintaxe: java TcpTimeClient serverAddress serverUdpPort");
//            return;
//        }

        try{

//            serverAddr = InetAddress.getByName("localhost");
//            serverPort = Integer.parseInt(args[1]);   
            socket = new Socket("localhost", 6001);
            socket.setSoTimeout(TIMEOUT*1000);
            
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            
            out.writeObject(new Registo("cris", "pass2"));
            out.flush();
            
            //A resposta deve terminar com uma mundança de linha.
            //Os caracteres de mudança de linha não são copiados para "response"
            response = (String)in.readObject();
          
            System.out.println(response);
            
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }catch(ClassNotFoundException e){
             System.out.println("O objecto recebido nao e' do tipo esperado:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
   }
}
