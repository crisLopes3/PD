/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author Pedro
 */
public class Ficheiro {
    private int idficheiro;
    private String Nome;
    private String Directoria;
    private String idUtlizador;

    public Ficheiro(int idficheiro, String Nome, String Directoria, String idUtlizador) {
        this.idficheiro = idficheiro;
        this.Nome = Nome;
        this.Directoria = Directoria;
        this.idUtlizador = idUtlizador;
    }
      public Ficheiro(String Nome, String Directoria) {
        this.Nome = Nome;
        this.Directoria = Directoria;
    }

    public String getIdUtlizador() {
        return idUtlizador;
    }

    public String getDirectoria() {
        return Directoria;
    }

    public int getIdficheiro() {
        return idficheiro;
    }
    

        
}
