package com.thingword.alphonso.materialmanage.http;

/**
 * Created by thingword-A on 2016/8/23.
 */
import android.util.Log;

import com.litesuits.http.LiteHttp;
import com.litesuits.http.impl.huc.HttpUrlClient;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.DataBase.DistributionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductDetailDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Authority;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by alphonso on 2016/7/28.
 */
public class HttpClient {
    private LiteHttp liteHttp;
    private static HttpClient single = null;

    private static final String DOMAIN_NAME ="http://192.168.3.21:8089/";

    //登陆判断
    public static final String LOGIN_URL = DOMAIN_NAME + "TestServer/rest/materail/reqUserLoginInfo";
    public static final String LOADING_URL = DOMAIN_NAME + "TestServer/rest/materail/reqLoadingInfo";
    public static final String UNLOADING_URL = DOMAIN_NAME + "TestServer/rest/materail/reqAllUnLoadingInfo";
    public static final String DISTRI_URL = DOMAIN_NAME + "TestServer/rest/materail/reqDistriInfo";
    public static final String STOREPRODUCTION_URL = DOMAIN_NAME + "TestServer/rest/materail/reqStoreProductionInfo";
    public static final String PRODUCTION_URL = DOMAIN_NAME + "TestServer/rest/materail/reqProductionInfo";
    public static final String PRODUCTIONDETAIL_URL = DOMAIN_NAME + "TestServer/rest/materail/reqProductionInfoDetail";

    private HttpClient() {
        liteHttp = LiteHttp.build(null)
                .setHttpClient(new HttpUrlClient())       // http
                .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                .setSocketTimeout(10000)           // socket timeout: 10s
                .setConnectTimeout(10000)         // connect timeout: 10s
                .create();
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
        StringRequest stringRequest = new StringRequest(LOGIN_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);

        liteHttp.executeAsync(stringRequest);
    }

    public void getLoadingInfo(HttpListener<String> listener, String date, String person) {
        if (listener == null || date == null || person == null) return;
        JSONObject object = new JSONObject();
        try {
            object.put("date", date);
            object.put("person", person);
        } catch (JSONException e) {
            return;
        }
        StringRequest stringRequest = new StringRequest(LOADING_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);

        liteHttp.executeAsync(stringRequest);
    }

    public void getUnLoadingInfo(HttpListener<String> listener, String date, String person) {
        if (listener == null || date == null || person == null) return;
        JSONObject object = new JSONObject();
        try {
            object.put("date", date);
            object.put("person", person);
        } catch (JSONException e) {
            return;
        }
        StringRequest stringRequest = new StringRequest(UNLOADING_URL)
                .setMethod(HttpMethods.Post).setHttpBody(new JsonBody(object.toString())).setHttpListener(listener);

        liteHttp.executeAsync(stringRequest);
    }

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
        StringRequest stringRequest = new StringRequest(LOADING_URL)
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

            if((authority&Authority.UNLOADING_AUTHORITY) != 0){
                stringRequest.setUri(UNLOADING_URL);
                result = liteHttp.execute(stringRequest);
                List<UnLoadingInfo> lsa =Parser.parseUnLoadingInfo(result.getResult());
                if(lsa.size()>0){
                    UnLoadingInfoDataHelper unloadingInfoDataHelper = new UnLoadingInfoDataHelper(MApplication.getContext());
                    unloadingInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
                    unloadingInfoDataHelper.bulkInsert(lsa);
                }else{
                    val.add("出库:"+Parser.getUnloadingErr());
                }
            }

            if((authority&Authority.DISTRIBUTION_AUTHORITY)!= 0){
                stringRequest.setUri(STOREPRODUCTION_URL);
                result = liteHttp.execute(stringRequest);
                List<DistributionInfo> lsb =Parser.parseDistriInfo(result.getResult());
                if(lsb.size()>0){
                    DistributionInfoDataHelper distributionInfoDataHelper = new DistributionInfoDataHelper(MApplication.getContext());
                    distributionInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
                    distributionInfoDataHelper.bulkInsert(lsb);
                }else{
                    val.add("配料:"+Parser.getDistriErr());
                }
            }

            if((authority&Authority.PRODUCTIONLINE_AUTHORITY) != 0){
                stringRequest.setUri(PRODUCTION_URL);
                result = liteHttp.execute(stringRequest);
                List<ProductionInfo> lsc =Parser.parseProductionInfo(result.getResult());
                if(lsc.size()>0){
                    ProductionInfoDataHelper productionInfoDataHelper = new ProductionInfoDataHelper(MApplication.getContext());
                    productionInfoDataHelper.deleteByCondition("date = ?", new String[]{date});
                    productionInfoDataHelper.bulkInsert(lsc);
                }else{
                    val.add("产线:"+Parser.getProductionErr());
                }
                Log.e("testcc","parseProductionInfo:"+lsc.size());

                stringRequest.setUri(PRODUCTIONDETAIL_URL);
                result = liteHttp.execute(stringRequest);
                List<ProductDetail> lsd =Parser.parseProductionDetail(result.getResult());
                if(lsd.size()>0){
                    ProductDetailDataHelper productDetailDataHelper = new ProductDetailDataHelper(MApplication.getContext());
                    productDetailDataHelper.deleteByCondition("date = ?", new String[]{date});
                    productDetailDataHelper.bulkInsert(lsd);
                }

            }

        }catch (Exception e) {

        }
        return val;
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
