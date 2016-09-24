package com.thingword.alphonso.materialmanage.bean.dbbean;

import android.database.Cursor;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class DistributionInfo {
    private String workshop;
    private String productline;
    private String tasknumber;
    private String productcode;
    private String spec;
    private String schedulednum;
    private String dailynum;
    private String date;
    private String remark;
    private String processflow;
    private String boardcode;
    private String uploadbatch;
    private String checked;

    public static DistributionInfo fromCursor(Cursor cursor) {
        DistributionInfo distributionInfo= cupboard().withCursor(cursor).get(DistributionInfo.class);
        return distributionInfo;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getProductline() {
        return productline;
    }

    public void setProductline(String productline) {
        this.productline = productline;
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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getSchedulednum() {
        return schedulednum;
    }

    public void setSchedulednum(String schedulednum) {
        this.schedulednum = schedulednum;
    }

    public String getDailynum() {
        return dailynum;
    }

    public void setDailynum(String dailynum) {
        this.dailynum = dailynum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProcessflow() {
        return processflow;
    }

    public void setProcessflow(String processflow) {
        this.processflow = processflow;
    }

    public String getBoardcode() {
        return boardcode;
    }

    public void setBoardcode(String boardcode) {
        this.boardcode = boardcode;
    }

    public String getUploadbatch() {
        return uploadbatch;
    }

    public void setUploadbatch(String uploadbatch) {
        this.uploadbatch = uploadbatch;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
