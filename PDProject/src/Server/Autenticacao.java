/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.Serializable;
import java.net.InetAddress;



/**
 *
 * @author Pedro
 */
public class Autenticacao implements Serializable{
    private Registo registo;
    private InetAddress ipCliente;

    public Autenticacao(Registo registo, InetAddress ipCliente) {
        this.registo = registo;
        this.ipCliente = ipCliente;
    }

    public Registo getRegisto() {
        return registo;
    }

    public InetAddress getIpCliente() {
        return ipCliente;
    }
    
    
}
