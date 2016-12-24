package com.thingword.alphonso.materialmanage.bean;

import com.thingword.alphonso.materialmanage.bean.dbbean.UpdateVersion;

import java.util.List;

public class ReturnUpdateInfo {
    private String return_msg;
    private String return_code;
    private UpdateVersion version;

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

    public UpdateVersion getVerion() {
        return version;
    }

    public void setVerion(UpdateVersion verion) {
        this.version = verion;
    }
}
