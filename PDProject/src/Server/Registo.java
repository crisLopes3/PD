package Server;

import java.io.Serializable;
import java.net.InetAddress;

public class Registo implements Serializable {
    private int idUtlizador;
    private String userName;
    private String passWord;
    private int portoUDP;
    private int portoTCP;
    private String IpUtilzador;
    private boolean estado=false;

    public Registo(int idUtlizador, String userName, String passWord, int portoUDP, int portoTCP, String IpUtilzador) {
        this.idUtlizador = idUtlizador;
        this.userName = userName;
        this.passWord = passWord;
        this.portoUDP = portoUDP;
        this.portoTCP = portoTCP;
        this.IpUtilzador = IpUtilzador;
    }
    
      public Registo(int idUtlizador, String userName, String passWord, int portoUDP, int portoTCP, String IpUtilzador,boolean estado) {
        this.idUtlizador = idUtlizador;
        this.userName = userName;
        this.passWord = passWord;
        this.portoUDP = portoUDP;
        this.portoTCP = portoTCP;
        this.IpUtilzador = IpUtilzador;
        this.estado=estado;
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
    
    public boolean getEstado(){
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
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

        aux = "idUltizador:"+idUtlizador+ " Username: " + userName + " Password: "+passWord+ " PortoUDP: " +portoUDP+" PortoTCP: " + portoTCP + " Ip: " + IpUtilzador+" Estado: "+estado;
        return aux;
    }

}
