package com.thingword.alphonso.materialmanage.http.ServerConfig;

import android.util.Log;

import com.thingword.alphonso.materialmanage.bean.LoadingInfo;
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
        try {
            List<LoadingInfo> ls = new ArrayList<>();
            JSONArray array = new JSONArray(val);
            Log.e("testcc", "array" + array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject jobject = (JSONObject) array.get(i);
                LoadingInfo ld = new LoadingInfo();
                String value = (String) jobject.get("cBatch");
                if (!(value.equals("null") && value != null)) {
                    ld.setcBatch(value);
                }

                value = (String) jobject.get("date");
                if (!(value.equals("null") && value != null)) {
                    ld.setcDate(value);
                }
                value = (String) jobject.get("iQuantity");
                if (!(value.equals("null") && value != null)) {
                    ld.setiQuantity(value);
                }
                value = (String) jobject.get("cInvStd");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvStd(value);
                }
                value = (String) jobject.get("cInvName");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvName(value);
                }
                value = (String) jobject.get("cInvCode");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvCode(value);
                }
                ls.add(ld);
            }
            return ls;
        } catch (JSONException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static List<UnLoadingInfo> parseUnLoadingInfo(String val) {
        try {
            List<UnLoadingInfo> ls = new ArrayList<>();
            JSONArray array = new JSONArray(val);
            Log.e("testcc", "array" + array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject jobject = (JSONObject) array.get(i);
                UnLoadingInfo ld = new UnLoadingInfo();
                String value = (String) jobject.get("cBatch");
                if (!(value.equals("null") && value != null)) {
                    ld.setcBatch(value);
                }

                value = (String) jobject.get("date");
                if (!(value.equals("null") && value != null)) {
                    ld.setcDate(value);
                }
                value = (String) jobject.get("iQuantity");
                if (!(value.equals("null") && value != null)) {
                    ld.setiQuantity(value);
                }
                value = (String) jobject.get("cInvStd");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvStd(value);
                }
                value = (String) jobject.get("cInvName");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvName(value);
                }
                value = (String) jobject.get("cInvCode");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvCode(value);
                }
                value = (String) jobject.get("cInvDefine8");
                if (!(value.equals("null") && value != null)) {
                    ld.setcInvDefine8(value);
                }
                value = (String) jobject.get("invcode");
                if (!(value.equals("null") && value != null)) {
                    ld.setInvcode(value);
                }
                value = (String) jobject.get("shopnum");
                if (!(value.equals("null") && value != null)) {
                    ld.setShopnum(value);
                }

                ls.add(ld);
            }
            return ls;
        } catch (JSONException e) {
            // e.printStackTrace();
        }
        return null;
    }
}
