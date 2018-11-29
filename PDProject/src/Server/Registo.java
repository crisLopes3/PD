
package Server;

import java.io.Serializable;

public class Registo implements Serializable{
    private String userName;
    private String passWord;

    public Registo(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }
    
    
    
    
}
