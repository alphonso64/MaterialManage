package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.thingword.alphonso.materialmanage.bean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.UnLoadingInfo;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class UnLoadingInfoDataHelper extends BaseDataHelper implements BaseDBInterface<UnLoadingInfo> {

    public UnLoadingInfoDataHelper(Context context) {
        super(context);
    }

    public static final String TABLE_NAME = "UnLoadingInfo";
    @Override
    protected Uri getContentUri() {
        return DataProvider.UNLOADING_TABLE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void bulkInsert(List<UnLoadingInfo> listData) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (UnLoadingInfo item : listData) {
            ContentValues values = getContentValues(item);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    @Override
    public ContentValues getContentValues(UnLoadingInfo data) {
        ContentValues values = cupboard().withEntity(UnLoadingInfo.class).toContentValues(data);
        return values;
    }

    public void deleteByCondition(String where, String[] selectionArgs ){
        delete(where,selectionArgs);
    }

    @Override
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }


    public CursorLoader getDateCursorLoader(String date) {
        return new CursorLoader(getContext(), getContentUri(), null, "cDate = ?",new String[]{date},null);
    }

}
