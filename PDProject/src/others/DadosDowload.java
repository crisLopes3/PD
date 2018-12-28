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
public class DadosDowload implements Serializable{

    private String ipDonoFicheiro;
    private int TCPDonoFicheiro;
    private String directoriaDonoFicheiro;
    private String NomeFicheiroDono;

    public DadosDowload(String ipDonoFicheiro, int TCPDonoFicheiro, String directoriaDonoFicheiro, String NomeFicheiroDono) {
        this.ipDonoFicheiro = ipDonoFicheiro;
        this.TCPDonoFicheiro = TCPDonoFicheiro;
        this.directoriaDonoFicheiro = directoriaDonoFicheiro;
        this.NomeFicheiroDono = NomeFicheiroDono;
    }

    public String getIpDonoFicheiro() {
        return ipDonoFicheiro;
    }

    public String getNomeFicheiroDono() {
        return NomeFicheiroDono;
    }

    public int getTCPDonoFicheiro() {
        return TCPDonoFicheiro;
    }

    public String getDirectoriaDonoFicheiro() {
        return directoriaDonoFicheiro;
    }

}
