package br.com.ledstock.led_stock.led_stock.utils;

/**
 * Created by Gustavo on 06/09/2016.
 */
public class Global {

    private static String usuario = null;
    private static String senha = null;
    private static String acesso = null;

    public void setAcesso(String access){
        acesso = access;
    }

    public void setSenha(String pass){
        senha = pass;
    }

    public void setUsuario(String user) {
        usuario = user;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getAcesso() {
        return acesso;
    }

    public String getSenha() {
        return senha;
    }
}
