/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package others;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class InformaçaoUtlizador implements Serializable{

    public String Username;
    public List<Ficheiro> listaFicheiros;
    public List<Historico>historico;
    

    public InformaçaoUtlizador(String Username) {
        this.Username = Username;
        this.listaFicheiros = new ArrayList<>();
        this.historico=new ArrayList<>();
    }

    public List<Ficheiro> getListaFicheiros() {
        return listaFicheiros;
    }

    public String getUsername() {
        return Username;
    }

    public List<Historico> getHistorico() {
        return historico;
    }
    

    @Override
    public String toString() {
        String aux = "";
        aux += "Username: " + this.Username + ":";
        for (Ficheiro ficheiro : listaFicheiros) {
            aux += " " + ficheiro.getNome() + ",";
        }
        aux += "\n";
        return aux;
    }

}
