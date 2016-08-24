package com.thingword.alphonso.materialmanage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.http.ServerConfig.ServerMessage;
import com.thingword.alphonso.materialmanage.http.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class loginActivity extends AppCompatActivity {
    @BindView(R.id.loginNameText)
    AutoCompleteTextView nameText;
    @BindView(R.id.loginPasswordText)
    EditText pwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginbutton)
    void OnLogin(){
        final String name = nameText.getEditableText().toString();
        String pwd = pwdText.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)){
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.logoin_empty_hint, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        HttpListener listener =  new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                try {
                    JSONObject object = new JSONObject(s);
                    String result = (String) object.get(ServerMessage.RETURN_CODE);
                    String authortity = (String) object.get(ServerMessage.RETURN_AUTH);
                    if(result.equals(ServerMessage.RETURN_SUCCESS)){
                        UserSharedPreferences.setUser(loginActivity.this,name,authortity);
                        loginJump();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.logoin_fail, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } catch (JSONException e) {
                    return;
                }
            }
            @Override
            public void onFailure(HttpException e, Response<String> response) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.con_server_fail, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        };
        HttpClient.getInstance().checkLogin(listener,name,pwd);
    }

    public void loginJump(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkLogin(){
        if(UserSharedPreferences.isLogged(this)){
            loginJump();
        }
    }

}
