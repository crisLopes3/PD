package others;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Registo implements Serializable {

    private int idUtlizador;
    private String userName;
    private String passWord;
    private int portoUDP;
    private int portoTCP;
    private String IpUtilzador;
    private boolean estado = false;
    private int portoTCPdowload;
    private List<Ficheiro> listaFicheiros;
    private List<Historico> historico;

    public Registo(int idUtlizador, String userName, String passWord, int portoUDP, int portoTCP, String IpUtilzador, int portoTCPdowload) {
        this.idUtlizador = idUtlizador;
        this.userName = userName;
        this.passWord = passWord;
        this.portoUDP = portoUDP;
        this.portoTCP = portoTCP;
        this.IpUtilzador = IpUtilzador;
        this.listaFicheiros = new ArrayList<>();
        this.portoTCPdowload = portoTCPdowload;
        this.historico = new ArrayList<>();
    }

    public Registo(int idUtlizador, String userName, String passWord, int portoUDP, int portoTCP, String IpUtilzador, boolean estado, int portoTCPdowload) {
        this.idUtlizador = idUtlizador;
        this.userName = userName;
        this.passWord = passWord;
        this.portoUDP = portoUDP;
        this.portoTCP = portoTCP;
        this.IpUtilzador = IpUtilzador;
        this.estado = estado;
        this.portoTCPdowload = portoTCPdowload;
        this.listaFicheiros = new ArrayList<>();
        this.historico = new ArrayList<>();
    }

    public String getPassWord() {
        return passWord;
    }

    public String getUserName() {
        return userName;
    }

    public int getPortoTCP() {
        return portoTCP;
    }

    public String getIpUtilzador() {
        return IpUtilzador;
    }

    public int getPortoUDP() {
        return portoUDP;
    }

    public int getIdUtlizador() {
        return idUtlizador;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getPortoTCPdowload() {
        return portoTCPdowload;
    }

    public void AddFicheiros(List<Ficheiro> lista) {
        for (int i = 0; i < lista.size(); i++) {
            listaFicheiros.add(lista.get(i));
        }
    }

    public void addFicheiro(Ficheiro novo) {
        listaFicheiros.add(novo);
    }

    public boolean ExisteFicheiro(String nomeFicheiro) {
        for (int i = 0; i < listaFicheiros.size(); i++) {
            System.out.println("Nome: " + listaFicheiros.get(i).getNome() + " Nome: " + nomeFicheiro);
            if (listaFicheiros.get(i).getNome().compareTo(nomeFicheiro) == 0) {
                return true;
            }
        }
        return false;
    }

    public Ficheiro getDadosFicheiro(String nomeFicheiro) {
        for (int i = 0; i < listaFicheiros.size(); i++) {
            System.out.println("Nome: " + listaFicheiros.get(i).getNome() + " Nome: " + nomeFicheiro);
            if (listaFicheiros.get(i).getNome().compareTo(nomeFicheiro) == 0) {
                return listaFicheiros.get(i);
            }
        }
        return null;
    }

    public List<Ficheiro> getListaFicheiros() {
        return listaFicheiros;
    }

    public void EleminaFicheiroRegisto(String nomeFicheiro) {
        this.listaFicheiros.remove(this.getDadosFicheiro(nomeFicheiro));
    }

    public List<Historico> getHistorico() {
        return historico;
    }

    public void addHistorico(Historico novo) {
        this.historico.add(novo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Registo)) {
            return false;
        }

        Registo aux = (Registo) obj;
        if (aux.passWord.equals(this.passWord) && aux.userName.equals(this.userName)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String aux;

        aux = "idUltizador:" + idUtlizador + " Username: " + userName + " Password: " + passWord + " PortoUDP: " + portoUDP + " PortoTCP: "
                + portoTCP + " Ip: " + IpUtilzador + " Estado: " + estado + " PortoTCPdowload: " + portoTCPdowload + ": ";

        return aux;
    }

}
