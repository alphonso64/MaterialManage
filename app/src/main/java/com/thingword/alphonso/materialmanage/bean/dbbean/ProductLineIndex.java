package com.thingword.alphonso.materialmanage.bean.dbbean;

/**
 * Created by thingword-A on 2016/12/28.
 */
public class ProductLineIndex {
    private Long _id;
    private String tasknumber;
    private String productcode;
    private String line;
    private String executor;
    private String checker;
    private String hasUpdate;
    private String totalNum;
    private String checkNum;

    public String getTasknumber() {
        return tasknumber;
    }

    public void setTasknumber(String tasknumber) {
        this.tasknumber = tasknumber;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(String hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum = checkNum;
    }
}
