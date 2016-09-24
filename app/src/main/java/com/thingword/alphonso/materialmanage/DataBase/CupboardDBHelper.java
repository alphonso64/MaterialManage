package com.thingword.alphonso.materialmanage.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.bean.User;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/24.
 */
public class CupboardDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data";
    private static final int DB_NEW_VERSION = 2;
    private static final int DB_BASE_VERSION = 1;
    private String name;

    static {
        cupboard().register(User.class);
        cupboard().register(LoadingInfo.class);
        cupboard().register(UnLoadingInfo.class);
        cupboard().register(DistributionInfo.class);
        cupboard().register(ProductionInfo.class);
        cupboard().register(ProductDetail.class);
    }

    public CupboardDBHelper(Context context,String name) {
        super(context,  DATABASE_NAME + "_"+name + ".db", null, DB_BASE_VERSION);
        Log.e("testcc", DATABASE_NAME + "_"+name + ".db");
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
