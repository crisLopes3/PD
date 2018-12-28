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
public class Ficheiro implements Serializable{
    private int idficheiro;
    private String Nome;
    private String Directoria;
    private int idUtlizador;

    public Ficheiro(int idficheiro, String Nome, String Directoria, int idUtlizador) {
        this.idficheiro = idficheiro;
        this.Nome = Nome;
        this.Directoria = Directoria;
        this.idUtlizador = idUtlizador;
    }
      public Ficheiro(String Nome, String Directoria) {
        this.Nome = Nome;
        this.Directoria = Directoria;
    }

    public int getIdUtlizador() {
        return idUtlizador;
    }

    public String getDirectoria() {
        return Directoria;
    }

    public int getIdficheiro() {
        return idficheiro;
    }

    public String getNome() {
        return Nome;
    }

    @Override
    public String toString() {
       String aux="id: " +idficheiro+" Nome:" +Nome+" Direcotoria: "+Directoria+" idUltilzador: "+idUtlizador;
       return aux;
    }
    
    

        
}
