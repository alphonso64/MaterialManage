package com.thingword.alphonso.materialmanage.DataBase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.thingword.alphonso.materialmanage.Util.SYSConfigure;
import com.thingword.alphonso.materialmanage.bean.User;

/**
 * Created by thingword-A on 2016/8/24.
 */
public class DoMainSharedPreferences {


    public static void setIP(Context ctx, String  ip) {
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.DOMIAN_DB,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SYSConfigure.DOMIAN_DB_IP, ip);
        ed.commit();
    }

    public static String getIP(Context ctx) {
        User user = new User();
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.DOMIAN_DB,
                Activity.MODE_PRIVATE);
        String ip = sp.getString(SYSConfigure.DOMIAN_DB_IP, "http://192.168.3.21:8089/");
        return ip;
    }


}
