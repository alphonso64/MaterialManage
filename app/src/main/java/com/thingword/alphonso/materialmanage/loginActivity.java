package com.thingword.alphonso.materialmanage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.DataBase.DataProvider;
import com.thingword.alphonso.materialmanage.DataBase.DoMainSharedPreferences;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.bean.ReturnLoginInfo;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.http.ServerConfig.ServerMessage;
import com.thingword.alphonso.materialmanage.http.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class loginActivity extends AppCompatActivity {
    @BindView(R.id.loginNameText)
    AutoCompleteTextView nameText;
    @BindView(R.id.loginPasswordText)
    EditText pwdText;
    private final String[] server = {"http://192.168.0.9:8089/","http://192.168.5.9:8089/","http://192.168.3.21:8089/"};
    private final String[] items = {"0服务器","5服务器","测试服务器"};
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.selectbutton)
    void  OnSelect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择服务器地址");
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , items);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DoMainSharedPreferences.setIP(loginActivity.this,server[i]);
                HttpClient.changeIP(server[i]);
            }
        });
        builder.show();
    }

    @OnClick(R.id.loginbutton)
    void OnLogin() {
        final String name = nameText.getEditableText().toString();
        String pwd = pwdText.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.logoin_empty_hint, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在登陆");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        HttpListener listener = new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                progressDialog.dismiss();
                if(s!=null){
                    Gson gson = new Gson();
                    ReturnLoginInfo returnLoginInfo = gson.fromJson(s, ReturnLoginInfo.class);
                    if (ServerMessage.RETURN_SUCCESS.equals(returnLoginInfo.getReturn_code())) {
                        User user = new User();
                        user.setEmploy_name(returnLoginInfo.getEmploy_name());
                        user.setEmploy_code(returnLoginInfo.getEmploy_code());
                        user.setUsername(name);
                        user.setAuthority(returnLoginInfo.getAuthority());
                        UserSharedPreferences.setUser(loginActivity.this, user);
                        DataProvider.resetDBHelper();
                        loginJump();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.logoin_fail, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.con_server_fail, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(HttpException e, Response<String> response) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.con_server_fail, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        };
        HttpClient.getInstance().checkLogin(listener, name, pwd);
    }

    public void loginJump() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkLogin() {
        if (UserSharedPreferences.isLogged(this)) {
            loginJump();
        }
    }

}
