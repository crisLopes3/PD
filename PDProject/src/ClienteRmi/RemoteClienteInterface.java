/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteRmi;

import java.util.List;
import others.InformaçaoUtlizador;

/**
 *
 * @author Pedro
 */
public interface RemoteClienteInterface extends java.rmi.Remote {
    public void ShowInformacaoServidor(List<InformaçaoUtlizador> informacao)throws java.rmi.RemoteException;
    
    
}
