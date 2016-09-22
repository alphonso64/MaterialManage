package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.thingword.alphonso.materialmanage.bean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.ProductionInfo;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ProductDetailDataHelper extends BaseDataHelper implements BaseDBInterface<ProductDetail> {

    public ProductDetailDataHelper(Context context) {
        super(context);
    }

    public static final String TABLE_NAME = "ProductDetail";
    @Override
    protected Uri getContentUri() {
        return DataProvider.PRODUCDETAIL_TABLE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void bulkInsert(List<ProductDetail> listData) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (ProductDetail item : listData) {
            ContentValues values = getContentValues(item);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    @Override
    public ContentValues getContentValues(ProductDetail data) {
        ContentValues values = cupboard().withEntity(ProductDetail.class).toContentValues(data);
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
        return new CursorLoader(getContext(), getContentUri(), null, "date = ?",new String[]{date},null);
    }

    private String fillTaskCode(String code) {
        StringBuilder stringBuilder = new StringBuilder();
        if (code.length() < 10) {
            int len = 10 - code.length();
            for (int i = 0; i < len; i++) {
                stringBuilder.append('0');
            }
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    private String cutCode(String code) {
        if(code.length()>10){
            return code.substring(0,10);
        }
        return code;
    }

    public CursorLoader getDetailCursorLoader(String date,String tasknumber,String productcode) {
        return new CursorLoader(getContext(), getContentUri(), null, "date =  ? and tasknumber = ? and productcode = ?",
                new String[]{date,fillTaskCode(tasknumber),productcode},null);//
    }


    public boolean setDataChecked(ProductionInfo productionInfo ,String code){
        ContentValues values = new ContentValues();
        values.put("checked","true");
        int count  = update(values,"date = ? and tasknumber = ? and productcode = ? and invcode = ?"
                ,new String[]{productionInfo.getDate(),fillTaskCode(productionInfo.getTasknumber())
                        ,productionInfo.getProductcode(),cutCode(code)});
        Log.e("testcc","setDataChecke "+cutCode(code));
//        Log.e("testcc","checkDataValid "+count);
        if(count == 0){
            return  false;
        }
        return true;
    }

}