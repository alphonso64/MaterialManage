package com.thingword.alphonso.materialmanage.http.ServerConfig;

import com.google.gson.Gson;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;

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

    private static String loadingErr;
    private static String unloadingErr;
    private static String distriErr;
    private static String productionErr;

    public static List<LoadingInfo> parseLoadingInfo(String val) {
        List<LoadingInfo> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    LoadingInfo ld = gson.fromJson(array.getString(i), LoadingInfo.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }else{
                loadingErr = (String) object.get(ServerMessage.RETURN_MSG);
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
                    UnLoadingInfo ld = gson.fromJson(array.getString(i), UnLoadingInfo.class);
                    ld.setChecked("false");
                    ld.setChecked_distri("false");
                    ls.add(ld);
                }
            }else{
                unloadingErr = (String) object.get(ServerMessage.RETURN_MSG);
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
                    DistributionInfo ld = gson.fromJson(array.getString(i), DistributionInfo.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }else{
                distriErr = (String) object.get(ServerMessage.RETURN_MSG);
            }
        } catch (JSONException e) {
        }
        return ls;
    }

    public static List<ProductionInfo> parseProductionInfo(String val) {
        List<ProductionInfo> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    ProductionInfo ld = gson.fromJson(array.getString(i), ProductionInfo.class);
                    ls.add(ld);
                }
            }else{
                productionErr = (String) object.get(ServerMessage.RETURN_MSG);
            }
        } catch (JSONException e) {
        }
        return ls;
    }

    public static List<ProductDetail> parseProductionDetail(String val) {
        List<ProductDetail> ls = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(val);
            String result = (String) object.get(ServerMessage.RETURN_CODE);
            if (result.equals(ServerMessage.RETURN_SUCCESS)) {
                JSONArray array = new JSONArray(object.getString(ServerMessage.DATA));
                for (int i = 0; i < array.length(); i++) {
                    Gson gson = new Gson();
                    ProductDetail ld = gson.fromJson(array.getString(i), ProductDetail.class);
                    ld.setChecked("false");
                    ls.add(ld);
                }
            }
        } catch (JSONException e) {
        }
        return ls;
    }

    public static String getLoadingErr() {
        return loadingErr;
    }

    public static String getUnloadingErr() {
        return unloadingErr;
    }

    public static String getDistriErr() {
        return distriErr;
    }

    public static String getProductionErr() {
        return productionErr;
    }
}
