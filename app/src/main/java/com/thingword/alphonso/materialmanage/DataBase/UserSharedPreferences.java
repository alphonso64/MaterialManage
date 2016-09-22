package com.thingword.alphonso.materialmanage.DataBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;

import com.thingword.alphonso.materialmanage.Util.SYSConfigure;
import com.thingword.alphonso.materialmanage.bean.User;

/**
 * Created by thingword-A on 2016/8/24.
 */
public class UserSharedPreferences {


    public static void setUser(Context ctx, User user) {
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.LOGIN_DB,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SYSConfigure.LOGIN_DB_COLB, SYSConfigure.HAS_LOGED);
        ed.putString(SYSConfigure.LOGIN_DB_COLA, user.getUsername());
        ed.putString(SYSConfigure.LOGIN_DB_COLC, user.getAuthority());
        ed.putString(SYSConfigure.LOGIN_DB_COLD, user.getEmploy_name());
        ed.putString(SYSConfigure.LOGIN_DB_COLE, user.getEmploy_code());
        ed.commit();
    }

    public static User getCusUser(Context ctx) {
        User user = new User();
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.LOGIN_DB,
                Activity.MODE_PRIVATE);
        String name = sp.getString(SYSConfigure.LOGIN_DB_COLA, null);
        String authortity = sp.getString(SYSConfigure.LOGIN_DB_COLC, null);
        String e_name = sp.getString(SYSConfigure.LOGIN_DB_COLD, null);
        String e_code = sp.getString(SYSConfigure.LOGIN_DB_COLE, null);
        user.setUsername(name);
        user.setAuthority(authortity);
        user.setEmploy_name(e_name);
        user.setEmploy_code(e_code);
        return user;
    }

    public static void setLogged(Context ctx, boolean flag) {
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.LOGIN_DB,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (flag) {
            ed.putString(SYSConfigure.LOGIN_DB_COLB, SYSConfigure.HAS_LOGED);
        } else {
            ed.putString(SYSConfigure.LOGIN_DB_COLB, SYSConfigure.NOT_LOGED);
        }
        ed.commit();
    }

    public static boolean isLogged(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SYSConfigure.LOGIN_DB,
                Activity.MODE_PRIVATE);
        String name = sp.getString(SYSConfigure.LOGIN_DB_COLB, null);
        if (SYSConfigure.HAS_LOGED.equals(sp.getString(SYSConfigure.LOGIN_DB_COLB, null))) {
            return true;
        }
        return false;
    }
}
