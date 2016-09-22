package com.thingword.alphonso.materialmanage.bean;

/**
 * Created by thingword-A on 2016/8/23.
 */
public class User {
    private String username;
    private String passwd;
    private String authority;
    private String employ_name;
    private String employ_code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getEmploy_name() {
        return employ_name;
    }

    public void setEmploy_name(String employ_name) {
        this.employ_name = employ_name;
    }

    public String getEmploy_code() {
        return employ_code;
    }

    public void setEmploy_code(String employ_code) {
        this.employ_code = employ_code;
    }
}
