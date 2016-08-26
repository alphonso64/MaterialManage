package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.support.v4.content.CursorLoader;

import java.util.List;


public interface BaseDBInterface<T> {

    public void bulkInsert(List<T> listData);

    public ContentValues getContentValues(T data);

    public CursorLoader getCursorLoader();
}
