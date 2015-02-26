package com.pdv.heli.message.detail;

/**
 * Created by via on 2/3/15.
 */
public class LoginRequestMessage {

    private String username;
    private String password;



    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
