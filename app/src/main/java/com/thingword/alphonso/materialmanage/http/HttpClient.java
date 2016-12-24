package com.thingword.alphonso.materialmanage.http;

/**
 * Created by thingword-A on 2016/8/23.
 */
import android.util.Log;

import com.google.gson.Gson;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.impl.huc.HttpUrlClient;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.FileRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.DataBase.DistributionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.DoMainSharedPreferences;
import com.thingword.alphonso.materialmanage.DataBase.ProductDetailDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.BatchData;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Authority;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by alphonso on 2016/7/28.
 */
public class HttpClient {
    private LiteHttp liteHttp;
    private static HttpClient single = null;
    private static  String DOMAIN_NAME ;//="http://192.168.3.21:8089/";////"http://192.200.5.194:8089/";//"http://192.168.0.9:8089/";//

    //登陆判断
    public static final String LOGIN_URL =  "TestServer/rest/materail/reqUserLoginInfo";
    public static final String LOADING_URL = "TestServer/rest/materail/reqLoadingInfo";
    public static final String PRODUCTIONDETAILBYCODE_URL =  "TestServer/rest/materail/reqProductionInfoDetailByCode";
    public static final String UNLOADING_URL =  "TestServer/rest/materail/reqAllUnLoadingInfo";
    public static final String UPDATEINFO_URL =  "TestServer/rest/materail/reqUpdateVerion";
    public static final String BATCH_UNLOADING_URL = "TestServer/rest/materail/reqUnLoadingInfoByBatch";
    public static final String DISTRI_URL = "TestServer/rest/materail/reqDistriInfo";
    public static final String STOREPRODUCTION_URL =  "TestServer/rest/materail/reqStoreProductionInfo";
    public static final String PRODUCTION_URL ="TestServer/rest/materail/reqProductionInfo";
    public static final String PRODUCTIONDETAIL_URL =  "TestServer/rest/materail/reqProductionInfoDetail";
    public static final String UPDATE_URL =  "TestServer/rest/materail/reqUpdateFile";

    private HttpClient() {
        DOMAIN_NAME = DoMainSharedPreferences.getIP(MApplication.getContext());
        liteHttp = LiteHttp.build(null)
                .setHttpClient(new HttpUrlClient())      // http
                .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                .setSocketTimeout(3000)           // socket timeout: 10s
                .setConnectTimeout(3000)// connect timeout: 10s
                .create();
    }

    public static void changeIP(String ip){
        DOMAIN_NAME = ip;
    }

    public static HttpClient getInstance() {
        if (single == null) {
            single = new HttpClient();
        }
        return single;
    }

    public void checkLogin(HttpListener<String> listener, String name, String pwd) {
        if (listener == null || name == null || pwd == null) return;
        JSONObject object = new JSONObject();
        try {
            object.put("username", name);
            object.put("passwd", pwd);
        } catch (JSONException e) {
            return;
        }
        CLog.e("testcc",DOMAIN_NAME+LOGIN_URL);
        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+LOGIN_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);
        liteHttp.executeAsync(stringRequest);
    }

    public void getUpdateInfo(HttpListener<String> listener) {
        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+UPDATEINFO_URL)
                .setMethod(HttpMethods.Post).setHttpListener(listener);
        liteHttp.executeAsync(stringRequest);
    }

    public void updateFile(HttpListener<File> listener){
        FileRequest request = new FileRequest(DOMAIN_NAME+UPDATE_URL)
                .setMethod(HttpMethods.Get).setHttpListener(listener);
        liteHttp.executeAsync(request);
    }

//    public void getLoadingInfo(HttpListener<String> listener, String date, String person) {
//        if (listener == null || date == null || person == null) return;
//        JSONObject object = new JSONObject();
//        try {
//            object.put("date", date);
//            object.put("person", person);
//        } catch (JSONException e) {
//            return;
//        }
//        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+LOADING_URL)
//                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);
//
//        liteHttp.executeAsync(stringRequest);
//    }
//
//    public void getUnLoadingInfo(HttpListener<String> listener, String date, String person) {
//        if (listener == null || date == null || person == null) return;
//        JSONObject object = new JSONObject();
//        try {
//            object.put("date", date);
//            object.put("person", person);
//        } catch (JSONException e) {
//            return;
//        }
//        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+UNLOADING_URL)
//                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);
//
//        liteHttp.executeAsync(stringRequest);
//    }

    public List<String> getAllInfo(String date,String name,String linenum,int authority){
        List<String>  val = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            object.put("date", date);
            object.put("person", name);
            object.put("linenum",linenum);
        } catch (JSONException e) {
        }
        LinkedHashMap<String, String> header = new LinkedHashMap<>();
//        header.put("contentType", "utf-8");
//        header.put("Content-type", "application/x-java-serialized-object");
        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+LOADING_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString()));
        try {
            Response<String> result;

//            if((authority& Authority.LOADING_AUTHORITY)!=0){
//                result = liteHttp.execute(stringRequest);
//                List<LoadingInfo> ls =Parser.parseLoadingInfo(result.getResult());
//                if(ls.size()>0){
//                    LoadingInfoDataHelper loadingInfoDataHelper = new LoadingInfoDataHelper(MApplication.getContext());
//                    loadingInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
//                    loadingInfoDataHelper.bulkInsert(ls);
//                }else{
//                    val.add("入库:"+Parser.getLoadingErr());
//                }
//            }

            int flag = 0;

            if((authority&Authority.UNLOADING_AUTHORITY) != 0){
                stringRequest.setUri(DOMAIN_NAME+BATCH_UNLOADING_URL);
                result = liteHttp.execute(stringRequest);
                List<UnLoadingInfo> lsa =Parser.parseBatchUnLoadingInfo(result.getResult(),date);
                if(lsa.size()>0){
                    UnLoadingInfoDataHelper unloadingInfoDataHelper = new UnLoadingInfoDataHelper(MApplication.getContext());
                    unloadingInfoDataHelper.bulkInsert(lsa);
                    flag = 1;
                }else{
                    if(!Parser.getUnloadingErr().equals("true")){
                        val.add("出库:"+Parser.getUnloadingErr());
                        flag = 2;
                    }
                    flag = 1;
                }
            }

//            if((authority&Authority.DISTRIBUTION_AUTHORITY)!= 0){
//                if(flag == 0){
//                    stringRequest.setUri(BATCH_UNLOADING_URL);
//                    result = liteHttp.execute(stringRequest);
//                    List<UnLoadingInfo> lsa =Parser.parseBatchUnLoadingInfo(result.getResult(),date);
//
//                    if(lsa.size()>0){
//                        UnLoadingInfoDataHelper unloadingInfoDataHelper = new UnLoadingInfoDataHelper(MApplication.getContext());
////                        unloadingInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
//                        unloadingInfoDataHelper.bulkInsert(lsa);
//                        flag = 1;
//                    }else{
//                        if(!Parser.getUnloadingErr().equals("true")){
//                            flag = 2;
//                        }
//                        flag = 1;
//                    }
//                }
//                if(flag == 2){
//                    val.add("配料:"+Parser.getDistriErr());
//                }else if(flag == 1){
//                    stringRequest.setUri(STOREPRODUCTION_URL);
//                    result = liteHttp.execute(stringRequest);
//                    List<DistributionInfo> lsb =Parser.parseDistriInfo(result.getResult());
//                    if(lsb.size()>0){
//                        DistributionInfoDataHelper distributionInfoDataHelper = new DistributionInfoDataHelper(MApplication.getContext());
//                        distributionInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
//                        distributionInfoDataHelper.bulkInsert(lsb);
//                    }else{
//                        val.add("配料:"+Parser.getDistriErr());
//                    }
//                }
//
//            }

//            if((authority&Authority.PRODUCTIONLINE_AUTHORITY) != 0){
//                stringRequest.setUri(PRODUCTION_URL);
//                result = liteHttp.execute(stringRequest);
//                List<ProductionInfo> lsc =Parser.parseProductionInfo(result.getResult());
//                if(lsc.size()>0){
//                    ProductionInfoDataHelper productionInfoDataHelper = new ProductionInfoDataHelper(MApplication.getContext());
//                    productionInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
//                    productionInfoDataHelper.bulkInsert(lsc);
//                }else{
//                    val.add("产线:"+Parser.getProductionErr());
//                }
//                stringRequest.setUri(PRODUCTIONDETAIL_URL);
//                result = liteHttp.execute(stringRequest);
//                List<ProductDetail> lsd =Parser.parseProductionDetail(result.getResult());
//                if(lsd.size()>0){
//                    ProductDetailDataHelper productDetailDataHelper = new ProductDetailDataHelper(MApplication.getContext());
//                    productDetailDataHelper.deleteByCondition("date = ?", new String[]{date});
//                    productDetailDataHelper.bulkInsert(lsd);
//                }
//
//            }

        }catch (Exception e) {

        }
        return val;
    }

    public int getProductDetailInfofByCode(String productcode,String tasknum){
        List<ProductDetail>  val = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            object.put("tasknumber", tasknum);
            object.put("productcode", productcode);
            CLog.e("testcc","tasknumber"+tasknum+"productcode"+productcode);
        } catch (JSONException e) {

        }

        LinkedHashMap<String, String> header = new LinkedHashMap<>();
        StringRequest stringRequest = new StringRequest(DOMAIN_NAME+PRODUCTIONDETAILBYCODE_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString()));
        try {
            Response<String> result;
            result = liteHttp.execute(stringRequest);
            val = Parser.parseProductionDetail(result.getResult());
            if(val.size() >0){
                ProductDetailDataHelper productDetailDataHelper = new ProductDetailDataHelper(MApplication.getContext());
                productDetailDataHelper.deleteByCondition(null, null);
                productDetailDataHelper.bulkInsert(val);
            }
        }catch (Exception e) {

        }
        return val.size();
    }



//    public void parse(HttpListener<String> listener, String content) {
//        if (listener == null || content == null) return;
//
//        String path = "http://115.29.193.236/rest/web/index.php?name="+content;
//        LinkedHashMap<String, String> header = new LinkedHashMap<>();
//        header.put("contentType", "utf-8");
//        header.put("Content-type", "application/x-java-serialized-object");
//        StringRequest ss = new StringRequest(path).setHeaders(header)
//                .setMethod(HttpMethods.Get).setHttpBody(new StringBody("")).setHttpListener(listener);
//        liteHttp.executeAsync(ss);
//        Log.e("tcc",ss.getHttpBody().toString());
//    }

}
