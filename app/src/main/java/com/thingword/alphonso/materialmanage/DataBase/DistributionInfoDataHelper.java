package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.thingword.alphonso.materialmanage.Util.Util;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class DistributionInfoDataHelper extends BaseDataHelper implements BaseDBInterface<DistributionInfo> {

    public DistributionInfoDataHelper(Context context) {
        super(context);
    }

    public static final String TABLE_NAME = "DistributionInfo";
    @Override
    protected Uri getContentUri() {
        return DataProvider.DISTRIBUTION_TABLE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void bulkInsert(List<DistributionInfo> listData) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (DistributionInfo item : listData) {
            ContentValues values = getContentValues(item);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    @Override
    public ContentValues getContentValues(DistributionInfo data) {
        ContentValues values = cupboard().withEntity(DistributionInfo.class).toContentValues(data);
        return values;
    }

    public void deleteByCondition(String where, String[] selectionArgs ){
        delete(where,selectionArgs);
    }

    @Override
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }

    public Cursor getWorshopCursor(int pos) {
        if(pos == 0)
            return query(getContentUri(), null, "workshop = ?",new String[]{"一车间"}, null);
        return query(getContentUri(), null, "workshop = ?",new String[]{"二车间"}, null);
    }

    public CursorLoader getDateCursorLoader(String date) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ?",new String[]{date},null);
    }


}
