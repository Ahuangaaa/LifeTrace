package com.example.a11322.lifetrace;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
    private String name;
    private String email;
    private String phonenumber;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return name;
    }
    public String getPhonenumber(){
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
}
