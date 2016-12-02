package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.Util.Util;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;

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

    public boolean ifUploadBatchExist(String uoloadbatch ,String date){
        Cursor cursor = query(null,"date = ? and uploadbatch = ?",new String[]{date,uoloadbatch},null);
        if(cursor.getCount()>0){
            return true;
        }
        return false;
    }

    @Override
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }


    public CursorLoader getDateCursorLoader(String date) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ?",new String[]{date},null);
    }

    public CursorLoader getDatePersonCursorLoader(String date,String person) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ? and executor = ?",new String[]{date,person},null);
    }

    public CursorLoader getDatePersonCursorLoaderOrderName(String date,String person) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ? and executor = ?",new String[]{date,person},"cInvName");
    }

    public CursorLoader getDatePersonCursorLoaderOrderBatch(String date,String person) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ? and executor = ?",new String[]{date,person},"cBatch");
    }
    public CursorLoader getDatePersonCursorLoaderOrderLine(String date,String person) {
        return new CursorLoader(getContext(), getContentUri(), null, "date = ? and executor = ?",new String[]{date,person},"linenum");
    }

    public CursorLoader getDetailCursorLoader(DistributionInfo distributionInfo) {
        CLog.e("testcc","getDetailCursorLoader"+" "+distributionInfo.getDate()
                +" "+Util.fillTaskCode(distributionInfo.getTasknumber())
                +" "+ Util.fillProductCode(distributionInfo.getProductcode(),distributionInfo.getWorkshop()));
        return new CursorLoader(getContext(), getContentUri(), null, "date =  ? and cMoCode = ? and invcode = ?",
                new String[]{distributionInfo.getDate(), Util.fillTaskCode(distributionInfo.getTasknumber()),
                        Util.fillProductCode(distributionInfo.getProductcode(),distributionInfo.getWorkshop())},null);//

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

    public void updateData(UnLoadingInfo unLoadingInfo){
        unLoadingInfo.setChecked("true");
        ContentValues values = getContentValues(unLoadingInfo);
        update(values,"date = ? and cBatch = ? and executor = ? and cMoCode = ? and invcode = ? "
                ,new String[]{unLoadingInfo.getDate(),unLoadingInfo.getcBatch(),unLoadingInfo.getExecutor(),
                unLoadingInfo.getcMoCode(),unLoadingInfo.getInvcode()});
    }

    public void updateData_(UnLoadingInfo unLoadingInfo){
        unLoadingInfo.setChecked("true");
        ContentValues values = getContentValues(unLoadingInfo);
        update(values,"date = ? and cInvCode = ? and executor = ? and cMoCode = ? and invcode = ? "
                ,new String[]{unLoadingInfo.getDate(),unLoadingInfo.getcInvCode(),unLoadingInfo.getExecutor(),
                        unLoadingInfo.getcMoCode(),unLoadingInfo.getInvcode()});
    }

    public Cursor getDataCheckedCurosr(String date ,String code,String name){
        return query(null,"date = ? and cBatch = ? and executor = ?",new String[]{date,code,name},null);
    }

    public Cursor getDataCheckedCurosr_(String date ,String code,String name){
        return query(null,"date = ? and cBatch = ? and executor = ? and checked = ?",new String[]{date,code,name,"false"},null);
    }

    public Cursor getDataCheckedFuzyCurosr(String date ,String code,String name){
        return query(null,"date = ? and cInvCode = ? and executor = ?",new String[]{date,Util.cutCode(code),name},null);
    }

    public Cursor getDataCheckedFuzyCurosr_(String date ,String code,String name){
        return query(null,"date = ? and cInvCode = ? and executor = ? and checked = ?",new String[]{date,Util.cutCode(code),name,"false"},null);
    }

    public boolean setDistriDataChecked(String code,DistributionInfo distributionInfo){
        ContentValues values = new ContentValues();
        values.put("checked_distri","true");
        int count  = update(values,"date = ? and cBatch = ? and cMoCode = ? and invcode = ? ",new String[]{distributionInfo.getDate(),code,Util.fillTaskCode(distributionInfo.getTasknumber()),
                Util.fillProductCode(distributionInfo.getProductcode(),distributionInfo.getWorkshop())});
        if(count == 0){
            return  false;
        }
        return true;
    }
}
