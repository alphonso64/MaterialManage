package com.thingword.alphonso.materialmanage.bean;

import android.database.Cursor;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class DistributionInfo {
    private String cBatch;
    private String cDate;
    private String iQuantity;
    private String cInvStd;
    private String cInvName;
    private String cInvCode;
    private String shopnum;
    private String cMoCode;
    private String invcode;
    private String cinvstd_1;
    private String checked;

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

    public static DistributionInfo fromCursor(Cursor cursor) {
        DistributionInfo loadingInfo= cupboard().withCursor(cursor).get(DistributionInfo.class);
        return loadingInfo;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getShopnum() {
        return shopnum;
    }

    public void setShopnum(String shopnum) {
        this.shopnum = shopnum;
    }

    public String getcMoCode() {
        return cMoCode;
    }

    public void setcMoCode(String cMoCode) {
        this.cMoCode = cMoCode;
    }

    public String getInvcode() {
        return invcode;
    }

    public void setInvcode(String invcode) {
        this.invcode = invcode;
    }

    public String getCinvstd_1() {
        return cinvstd_1;
    }

    public void setCinvstd_1(String cinvstd_1) {
        this.cinvstd_1 = cinvstd_1;
    }
}
