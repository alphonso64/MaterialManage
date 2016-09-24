package com.thingword.alphonso.materialmanage.bean.dbbean;

import android.database.Cursor;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class UnLoadingInfo {
    private String cBatch;
    private String date;
    private String iQuantity;
    private String cInvStd;
    private String cInvName;
    private String cInvCode;
    private String cInvDefine8;
    private String invcode;
    private String shopnum;
    private String linenum;
    private String checked;
    private String checked_distri;
    private String cMoCode;
    private String uploadbatch;

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

    public static UnLoadingInfo fromCursor(Cursor cursor) {
        UnLoadingInfo loadingInfo= cupboard().withCursor(cursor).get(UnLoadingInfo.class);
        return loadingInfo;
    }

    public String getcInvDefine8() {
        return cInvDefine8;
    }

    public void setcInvDefine8(String cInvDefine8) {
        this.cInvDefine8 = cInvDefine8;
    }

    public String getInvcode() {
        return invcode;
    }

    public void setInvcode(String invcode) {
        this.invcode = invcode;
    }

    public String getShopnum() {
        return shopnum;
    }

    public void setShopnum(String shopnum) {
        this.shopnum = shopnum;
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

    public String getLinenum() {
        return linenum;
    }

    public void setLinenum(String linenum) {
        this.linenum = linenum;
    }

    public String getcMoCode() {
        return cMoCode;
    }

    public void setcMoCode(String cMoCode) {
        this.cMoCode = cMoCode;
    }

    public String getUploadbatch() {
        return uploadbatch;
    }

    public void setUploadbatch(String uploadbatch) {
        this.uploadbatch = uploadbatch;
    }

    public String getChecked_distri() {
        return checked_distri;
    }

    public void setChecked_distri(String checked_distri) {
        this.checked_distri = checked_distri;
    }
}
