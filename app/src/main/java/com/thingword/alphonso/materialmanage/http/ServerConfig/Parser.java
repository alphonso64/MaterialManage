package com.thingword.alphonso.materialmanage.http.ServerConfig;

import android.util.Log;

import com.google.gson.Gson;
import com.thingword.alphonso.materialmanage.bean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.ReturnData;
import com.thingword.alphonso.materialmanage.bean.UnLoadingInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thingword-A on 2016/8/25.
 */
public class Parser {
    //    public static List<LoadingInfo> parseLoadingInfo(String val){
//        try {
//            List<LoadingInfo> ls = new ArrayList<>();
//            JSONObject object = new JSONObject(val);
//            String result = (String) object.get(ServerMessage.RETURN_CODE);
//            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
//                JSONArray array = object.getJSONArray("data");
//                Log.e("testcc","array"+array.length());
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject jobject = (JSONObject) array.get(i);
//                    LoadingInfo ld = new LoadingInfo();
//                    String value = (String) jobject.get("cBatch");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setcBatch(value);
//                    }
//
//                    value = (String) jobject.get("date");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setDate(value);
//                    }
//                    value = (String) jobject.get("iQuantity");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setiQuantity(value);
//                    }
//                    value = (String) jobject.get("cInvStd");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setcInvStd(value);
//                    }
//                    value = (String) jobject.get("cInvName");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setcInvName(value);
//                    }
//                    value = (String) jobject.get("cInvCode");
//                    if (!(value.equals("null") && value != null)) {
//                        ld.setcInvCode(value);
//                    }
//                    ls.add(ld);
//                }
//            }
//            return ls;
//        } catch (JSONException e) {
//            // e.printStackTrace();
//        }
//        return null;
//    }


    public static List<LoadingInfo> parseLoadingInfo(String val) {
        List<LoadingInfo> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    LoadingInfo ld = gson.fromJson(array.getString(i),LoadingInfo.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }
        } catch (JSONException e) {
        }
        return ls;
    }

    public static List<UnLoadingInfo> parseUnLoadingInfo(String val) {
        List<UnLoadingInfo> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    UnLoadingInfo ld = gson.fromJson(array.getString(i),UnLoadingInfo.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }
        } catch (JSONException e) {
        }
        return ls;
    }


    public static List<DistributionInfo> parseDistriInfo(String val) {
        List<DistributionInfo> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    DistributionInfo ld = gson.fromJson(array.getString(i),DistributionInfo.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }
        } catch (JSONException e) {
        }
        return ls;
    }
}
