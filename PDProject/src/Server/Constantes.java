/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author Pedro
 */
public interface Constantes {

    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
    public static final String CONN_STRING = "jdbc:mysql://localhost/pdbasedados?useTimezone=true&serverTimezone=UTC";
    public static final String AUTENTICADO = "autenticado";
    public static final String REGISTADO = "registado";

    public static final int PEDIDO_REGISTO = 1;
    public static final int PEDIDO_AUTENTICACAO = 2;
    public static final int DISPONIBLIZAR_FICHEIROS = 3;

}
