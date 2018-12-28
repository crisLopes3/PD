/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ClienteRmi.RemoteClienteInterface;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import others.InformaçaoUtlizador;

/**
 *
 * @author Pedro
 */
public class ServidorRmi extends UnicastRemoteObject implements RemoteServiceInterface {

    public static final String SERVICE_NAME = "ObservacaoSistema";
    private Server servidor;
    public List<Remote> listaRemotes;

    public ServidorRmi(Server servidor) throws RemoteException {
        this.servidor = servidor;
        listaRemotes = new ArrayList<>();
    }

    public void InicioServidorRmi() {

        /*
         * Trata os argumentos da linha de comando
         */
 /*
        * Lanca o rmiregistry localmente no porto TCP por omissao (1099) ou, caso este ja' se encontre
        * a correr, obtem uma referencia.
         */
        try {

            Registry r;

            try {

                System.out.println("Tentativa de lancamento do registry no porto "
                        + Registry.REGISTRY_PORT + "...");

                r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

                System.out.println("Registry lancado!");

            } catch (RemoteException e) {
                System.out.println("Registry provavelmente ja' em execucao!");
                r = LocateRegistry.getRegistry();
            }

            /*
             * Cria o servico
             */
 /*
             * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
             * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
             */
            r.bind(SERVICE_NAME, this);

            System.out.println("Servico " + SERVICE_NAME + " registado no registry...");

            /*
             * Para terminar um servico RMI do tipo UnicastRemoteObject:
             * 
             *  UnicastRemoteObject.unexportObject(fileService, true);
             */
        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }

    @Override
    public void EnviaInformacaoServidor(List<InformaçaoUtlizador> lista) throws RemoteException {
        for (Remote remote : listaRemotes) {
            RemoteClienteInterface cliente = (RemoteClienteInterface) remote;
            cliente.ShowInformacaoServidor(lista);
        }
    }

    @Override
    public void AddRemoteService(RemoteClienteInterface remote) throws RemoteException {
        if (remote != null) {
            listaRemotes.add(remote);
        }
    }

    @Override
    public void ElminarRemoteService(RemoteClienteInterface remote) throws RemoteException {
        for (Remote remoteAux : listaRemotes) {
            if(remoteAux.equals(remote)){
                listaRemotes.remove(remote);
            }
        }
    }
    

}
