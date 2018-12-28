/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteRmi;

import Server.RemoteServiceInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import others.InformaçaoUtlizador;

/**
 *
 * @author Pedro
 */
public class ClienteRmi extends UnicastRemoteObject implements RemoteClienteInterface {

    public ClienteRmi() throws RemoteException {
    }

    public static void main(String[] args) throws IOException {
        String objectUrl;

        ClienteRmi myRemoteService = null;
        RemoteServiceInterface remoteService;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        /*
         * Trata os argumentos da linha de comando 
         */
        System.out.println("Digite o ip do servidor: ");
        // String ipServidor = in.readLine();
        String ipServidor = "localhost";
        objectUrl = "rmi://" + ipServidor + "/ObservacaoSistema";

        try {

            remoteService = (RemoteServiceInterface) Naming.lookup(objectUrl);

            /*
             * Lanca o servico local para acesso remoto por parte do servidor.
             */
            myRemoteService = new ClienteRmi();

            remoteService.AddRemoteService(myRemoteService);
            System.out.println("Servico Remoto Adiconado ");
            
            System.out.println("Digite (sair) para sair:");
            String opcao;
            do{
             opcao=in.readLine();
            }while(opcao.compareTo("sair")!=0);
            
            remoteService.ElminarRemoteService(myRemoteService);
            /*
             * Passa ao servico RMI LOCAL uma referencia para o objecto localFileOutputStream
             
            
            /*
            
             * Obtem o ficheiro pretendido, invocando o metodo getFile no servico remoto.
             */
        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch (IOException e) {
            System.out.println("Erro E/S - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        } finally {

            if (myRemoteService != null) {

                try {
                    UnicastRemoteObject.unexportObject((Remote) myRemoteService, true);
                } catch (NoSuchObjectException e) {
                }
            }
        }

    }

    @Override
    public void ShowInformacaoServidor(List<InformaçaoUtlizador> informacao) {
        for (InformaçaoUtlizador informaçaoUtlizador : informacao) {
            System.out.println(informaçaoUtlizador.toString());
        }
    }
}
