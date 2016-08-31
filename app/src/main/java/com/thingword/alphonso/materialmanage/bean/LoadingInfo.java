package com.thingword.alphonso.materialmanage.bean;

import android.database.Cursor;

import com.google.gson.annotations.Expose;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class LoadingInfo {
    @Expose
    private String cBatch;
    @Expose
    private String date;
    @Expose
    private String iQuantity;
    @Expose
    private String cInvStd;
    @Expose
    private String cInvName;
    @Expose
    private String cInvCode;

    private String checked;

    public String printfInfo(){
        return "cBatch:"+cBatch+" cDate:"+date+" iQuantity:"+iQuantity+" cInvStd:"+cInvStd+" cInvName:"+cInvName+" cInvCode:"+cInvCode+" checked:"+checked;
    }

    public String getcInvCode() {
        return cInvCode;
    }

    public void setcInvCode(String cInvCode) {
        this.cInvCode = cInvCode;
    }

    public String getcInvName() {
        return cInvName;
    }

    public void setcInvName(String cInvName) {
        this.cInvName = cInvName;
    }

    public String getcInvStd() {
        return cInvStd;
    }

    public void setcInvStd(String cInvStd) {
        this.cInvStd = cInvStd;
    }

    public String getiQuantity() {
        return iQuantity;
    }

    public void setiQuantity(String iQuantity) {
        this.iQuantity = iQuantity;
    }

    public String getcBatch() {
        return cBatch;
    }

    public void setcBatch(String cBatch) {
        this.cBatch = cBatch;
    }

    public static LoadingInfo fromCursor(Cursor cursor) {
        LoadingInfo loadingInfo= cupboard().withCursor(cursor).get(LoadingInfo.class);
        return loadingInfo;
    }


    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
