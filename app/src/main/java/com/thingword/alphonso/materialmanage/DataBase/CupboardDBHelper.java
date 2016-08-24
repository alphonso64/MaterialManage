package com.thingword.alphonso.materialmanage.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thingword.alphonso.materialmanage.bean.User;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by thingword-A on 2016/8/24.
 */
public class CupboardDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 1;

    static {
        cupboard().register(User.class);
    }

    public CupboardDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
