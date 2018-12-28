/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package others;

import java.io.Serializable;



/**
 *
 * @author Pedro
 */
public class Autenticacao implements Serializable{
    private String username;
    private String password;
    private String ipUtlizador;

    public Autenticacao(String username, String password,String ipUtilizador) {
        this.username = username;
        this.password = password;
        this.ipUtlizador=ipUtilizador;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getIpUtlizador() {
        return ipUtlizador;
    }
    
    
}
