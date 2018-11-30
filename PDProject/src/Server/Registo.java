package Server;

import java.io.Serializable;

public class Registo implements Serializable {

    private String userName;
    private String passWord;

    public Registo(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
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

}
