package com.thingword.alphonso.materialmanage.bean.dbbean;

import android.database.Cursor;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class ProductDetail {
    private String tasknumber;
    private String productcode;
    private String date;
    private String invcode;
    private String invname;
    private String invstd;
    private String qty;
    private String bomqty;
    private String define28;
    private String linenum;
    private String workshop;
    private String checked;
    private String num;

    public static ProductDetail fromCursor(Cursor cursor) {
        ProductDetail productDetail= cupboard().withCursor(cursor).get(ProductDetail.class);
        return productDetail;
    }

    public String getTasknumber() {
        return tasknumber;
    }

    public void setTasknumber(String tasknumber) {
        this.tasknumber = tasknumber;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvcode() {
        return invcode;
    }

    public void setInvcode(String invcode) {
        this.invcode = invcode;
    }

    public String getInvname() {
        return invname;
    }

    public void setInvname(String invname) {
        this.invname = invname;
    }

    public String getInvstd() {
        return invstd;
    }

    public void setInvstd(String invstd) {
        this.invstd = invstd;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getBomqty() {
        return bomqty;
    }

    public void setBomqty(String bomqty) {
        this.bomqty = bomqty;
    }

    public String getDefine28() {
        return define28;
    }

    public void setDefine28(String define28) {
        this.define28 = define28;
    }

    public String getLinenum() {
        return linenum;
    }

    public void setLinenum(String linenum) {
        this.linenum = linenum;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
