package Client;

import Server.Registo;
import java.net.*;
import java.io.*;

public class Client {

    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) throws IOException
    {
        
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String response;

        try{
 
            socket = new Socket("localhost", 6001);
            socket.setSoTimeout(TIMEOUT*1000);
            
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            
            out.writeObject(new Registo("cris", "pass2"));
            out.flush();
            

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
