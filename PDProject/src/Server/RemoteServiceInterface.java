/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ClienteRmi.RemoteClienteInterface;
import java.util.List;
import others.InformaçaoUtlizador;

/**
 *
 * @author Pedro
 */
public interface RemoteServiceInterface extends java.rmi.Remote {

    public void EnviaInformacaoServidor(List<InformaçaoUtlizador> lista) throws java.rmi.RemoteException;
    public void AddRemoteService(RemoteClienteInterface remote) throws java.rmi.RemoteException;
    public void ElminarRemoteService(RemoteClienteInterface remote) throws java.rmi.RemoteException;
}
