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
public class Historico implements Serializable {

    private int idFicheiro;
    private int idUSernameDestino;
    private String NomeFicheiro;
    private String UsernameOrigem;
    private String USernameDestino;

    public Historico(int idFicheiro, String NomeFicheiro, String UsernameOrigem, String USernameDestino, int idUSernameDestino) {
        this.idFicheiro = idFicheiro;
        this.NomeFicheiro = NomeFicheiro;
        this.UsernameOrigem = UsernameOrigem;
        this.USernameDestino = USernameDestino;
        this.idFicheiro = idUSernameDestino;
    }

    public Historico(String NomeFicheiro, String UsernameOrigem, String USernameDestino, int idUSernameDestino) {
        this.NomeFicheiro = NomeFicheiro;
        this.UsernameOrigem = UsernameOrigem;
        this.USernameDestino = USernameDestino;
        this.idFicheiro = idUSernameDestino;
    }

    public int getIdFicheiro() {
        return idFicheiro;
    }

    public String getNomeFicheiro() {
        return NomeFicheiro;
    }

    public String getUSernameDestino() {
        return USernameDestino;
    }

    public String getUsernameOrigem() {
        return UsernameOrigem;
    }

    public int getIdUSernameDestino() {
        return idUSernameDestino;
    }
    

    @Override
    public String toString() {
        String aux = "";
        aux += " Ficheiro: " + NomeFicheiro + " Origem: " + UsernameOrigem + " Destino: " + USernameDestino;
        return aux;
    }

}
