/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.Cliente.IS_AUTENTICADO;
import static Client.Cliente.REGISTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Pedro
 */
public class mainCliente {

    public static void main(String[] args) throws IOException {
        int opcao;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //System.out.println("Degite o IP do servidor que se pretender conectar: ");
        //String ipServidor=in.readLine();
        //System.out.println("Degite a directoria que pretende guardar os ficheiros ");
        //String destinoFicheiros=in.readLine();
        Cliente cliente = new Cliente("localhost", "C:\\Users\\Pedro\\Documents\\NetBeansProjects\\PD\\PD\\PDProject");
        while (!cliente.exitCliente) {
            System.out.println("Menu:");
            System.out.println("1-Novo Registo");
            System.out.println("2-Autenticacao");
            System.out.println("3-Disponiblizar Ficheiro");
            System.out.println("4-Fazer dowload");
            System.out.println("5-Listar Informaçao");
            System.out.println("6-Listar historico");
            System.out.println("7-Sair\n");
            System.out.println("Comando:");
            switch (in.readLine()) {

                case "1":
                    if (!REGISTO) {
                        System.out.println("Pedido de Registo:");
                        System.out.println("Username: ");
                        String username = in.readLine();
                        System.out.println("Password:");
                        String Password = in.readLine();
                        cliente.PedidoDeRegisto(username, Password);
                    }
                    break;
                case "2":
                    if (!IS_AUTENTICADO) {
                        System.out.println("Pedido de autenticaçao:");
                        System.out.println("Username: ");
                        String username = in.readLine();
                        System.out.println("Password:");
                        String Password = in.readLine();
                        cliente.PedidoDeAutenticacao(username, Password);
                    }
                    break;
                case "3":
                    if (IS_AUTENTICADO) {
                        System.out.println("Disponiblizar Ficheiro:");
                        System.out.println("Utruduza Nome Ficheiro: ");
                        String Nome = in.readLine();
                        System.out.println("Introduza Directoria Ficheiro ");
                        String Directoria = in.readLine();
                        cliente.DisponiblizarFicheiro(Nome, Directoria);
                    }
                    break;
                case "4":
                    if (IS_AUTENTICADO) {
                        System.out.println("Dowload Ficheiro:");
                        System.out.println("Nome do Ficheiro: ");
                        String nomeFicneiro = in.readLine();
                        cliente.DowloadFicheiro(nomeFicneiro);
                    }
                    break;
                case "5":
                    if (IS_AUTENTICADO) {
                        System.out.println("Informaçao:");
                        cliente.listarInformacaoActualzada();
                    }
                    break;
                case "6":
                    if (IS_AUTENTICADO) {
                        System.out.println("Historico:");
                        cliente.ListarHistorico();
                    }
                    break;
                case "7":
                    if (IS_AUTENTICADO) {
                        System.out.println("Disconectar");
                        cliente.PedidoDesconect();
                        return;
                    } else {
                        return;
                    }

            }
        }
        System.out.println("Acabou");
    }
}
