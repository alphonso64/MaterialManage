package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class LoadingInfoDataHelper extends BaseDataHelper implements BaseDBInterface<LoadingInfo> {

    public LoadingInfoDataHelper(Context context) {
        super(context);
    }

    public static final String TABLE_NAME = "LoadingInfo";
    @Override
    protected Uri getContentUri() {
        return DataProvider.LOADING_TABLE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void bulkInsert(List<LoadingInfo> listData) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (LoadingInfo item : listData) {
            ContentValues values = getContentValues(item);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }



    @Override
    public ContentValues getContentValues(LoadingInfo data) {
        ContentValues values = cupboard().withEntity(LoadingInfo.class).toContentValues(data);
        return values;
    }

    public void deleteByCondition(String where, String[] selectionArgs ){
        delete(where,selectionArgs);
    }

    public void insertSingle(LoadingInfo data){
        ContentValues values = cupboard().withEntity(LoadingInfo.class).toContentValues(data);
        insert(values);
    }

//    public void repalceInfo(List<LoadingInfo> listData){
//       delete_("saved = ?", new String[]{"yes"});
//       // delete(null,null);
//        bulkInsert(listData);
//    }

    @Override
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }


    public CursorLoader getDateCursorLoader(String date) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ?",new String[]{date},null);
    }

//    public CursorLoader getEmptyCursorLoader() {
//        return new CursorLoader(getContext(), getContentUri(), null, "cDate = ?",new String[]{" "},null);
//    }


}
