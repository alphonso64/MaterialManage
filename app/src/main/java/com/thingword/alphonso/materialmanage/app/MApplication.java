package com.thingword.alphonso.materialmanage.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import Jack.WewinPrinterHelper.WwPrint;

/**
 * Created by ALPHONSO on 2016/1/7.
 */
public class MApplication extends Application {
    private static Context sContext;
    private WwPrint printer;
    private String unloadWorkDate;
    private String distriWorkDate;
    public WwPrint getPrinter(){
        if(printer == null)
        {
            printer = new WwPrint();
        }
        return  printer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Log.e("testcc", "onCreate");
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getUnloadWorkDate() {
        return unloadWorkDate;
    }

    public void setUnloadWorkDate(String unloadWorkDate) {
        this.unloadWorkDate = unloadWorkDate;
    }

    public String getDistriWorkDate() {
        return distriWorkDate;
    }

    public void setDistriWorkDate(String distriWorkDate) {
        this.distriWorkDate = distriWorkDate;
    }
}
