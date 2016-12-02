package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ProductionInfoDataHelper extends BaseDataHelper implements BaseDBInterface<ProductionInfo> {

    public ProductionInfoDataHelper(Context context) {
        super(context);
    }

    public static final String TABLE_NAME = "ProductionInfo";
    @Override
    protected Uri getContentUri() {
        return DataProvider.PRODUCTION_TABLE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void bulkInsert(List<ProductionInfo> listData) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (ProductionInfo item : listData) {
            ContentValues values = getContentValues(item);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    @Override
    public ContentValues getContentValues(ProductionInfo data) {
        ContentValues values = cupboard().withEntity(ProductionInfo.class).toContentValues(data);
        return values;
    }

    public void deleteByCondition(String where, String[] selectionArgs ){
        delete(where,selectionArgs);
    }

    @Override
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }

    public Cursor getCursor_() {
        return query(getContentUri(), null, null, null, null);
    }

    public Cursor getDateCursor(String date) {
        return query(getContentUri(), null, "date = ?",new String[]{date}, null);
    }

    public CursorLoader getDateCursorLoader(String date) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ?",new String[]{date},null);
    }

    public boolean setDataChecked(String date ,String code){
        ContentValues values = new ContentValues();
        values.put("checked","true");
        int count  = update(values,"date = ? and cBatch = ?",new String[]{date,code});
        if(count == 0){
            return  false;
        }
        return true;
    }

}
