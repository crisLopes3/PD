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
    private String username;
    private String password;

    public Autenticacao(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    

  
    
}
