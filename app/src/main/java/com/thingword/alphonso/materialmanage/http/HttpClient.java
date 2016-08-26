package com.thingword.alphonso.materialmanage.http;

/**
 * Created by thingword-A on 2016/8/23.
 */
import android.util.Log;

import com.litesuits.http.LiteHttp;
import com.litesuits.http.data.GsonImpl;
import com.litesuits.http.impl.huc.HttpUrlClient;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.content.StringBody;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by alphonso on 2016/7/28.
 */
public class HttpClient {
    private LiteHttp liteHttp;
    private static HttpClient single = null;

    private static final String DOMAIN_NAME = "http://192.168.3.21:8089/";

    //登陆判断
    public static final String LOGIN_URL = DOMAIN_NAME + "MaterialManage/rest/json/reqUserLoginInfo";
    public static final String LOADING_URL = DOMAIN_NAME + "MaterialManage/rest/json/reqLoadingInfo";

    private HttpClient() {
        liteHttp = LiteHttp.build(null)
                .setHttpClient(new HttpUrlClient())       // http
                .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                .setSocketTimeout(1000)           // socket timeout: 10s
                .setConnectTimeout(1000)         // connect timeout: 10s
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
