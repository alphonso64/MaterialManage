package com.thingword.alphonso.materialmanage.bean;

public class ReturnLoginInfo {
    private String return_msg;
    private String return_code;
    private String employ_name;
    private String employ_code;
    private String authority;

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
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
