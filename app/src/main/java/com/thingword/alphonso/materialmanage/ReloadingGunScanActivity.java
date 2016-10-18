package com.thingword.alphonso.materialmanage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.zxing.Result;
import com.google.zxing.client.android.decode.CaptureActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.Util.Util;

/**
 * Created by thingword-A on 2016/8/28.
 */
public class ReloadingGunScanActivity extends AppCompatActivity {
    RadioButton r1;
    RadioButton r2;
    private int state;
    private TextView barcode1, barcode2, righttx, wrongtx;
    private Button btn;
    private EditText editText;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (state == 1) {
                    barcode1.setText((String) msg.obj);
                    editText.getText().clear();
                } else {
                    barcode2.setText((String) msg.obj);
                    editText.getText().clear();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_gun);
        Toolbar toolbar = (Toolbar)findViewById(R.id.reloadgun_toolbar);
        toolbar.setTitle(R.string.reloading_title);
        state = 1;
        r1 = (RadioButton) findViewById(R.id.radioButton1);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
        editText = (EditText) findViewById(R.id.reloadgun_editText);
        r1.setChecked(true);
        r2.setChecked(false);
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r2.isChecked()) {
                    r2.setChecked(false);
                }
                r1.setChecked(true);
                state = 1;
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r1.isChecked()) {
                    r1.setChecked(false);
                }
                r2.setChecked(true);
                state = 2;
            }
        });
        btn = (Button) findViewById(R.id.reload_compare);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String b1 = Util.cutCode(barcode1.getText().toString());
                String b2 = Util.cutCode(barcode2.getText().toString());
                if (b1.equals(b2)) {
                    righttx.setVisibility(View.VISIBLE);
                    wrongtx.setVisibility(View.GONE);
                } else {
                    wrongtx.setVisibility(View.VISIBLE);
                    righttx.setVisibility(View.GONE);
                }
            }
        });

        editText.setInputType(InputType.TYPE_NULL);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String val = s.toString();
                if(val.endsWith(" ")){
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = val;
                    mHandler.sendMessage(msg);
                }
            }
        });

        barcode1 = (TextView) findViewById(R.id.reload_barcode1_textview);
        barcode2 = (TextView) findViewById(R.id.reload_barcode2_textview);
        righttx = (TextView) findViewById(R.id.relodscan_right_text);
        wrongtx = (TextView) findViewById(R.id.relodscan_wrong_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
