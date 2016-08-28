package com.thingword.alphonso.materialmanage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.Result;
import com.google.zxing.client.android.decode.CaptureActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.Util.BarCodeCreator;
import com.thingword.alphonso.materialmanage.app.MApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thingword-A on 2016/8/28.
 */
public class ReloadingScanActivity extends CaptureActivity {
    ParsedResult parsedResult1;
    ParsedResult parsedResult2;
    RadioButton r1;
    RadioButton r2;
    private int state;
    private TextView barcode1,barcode2,righttx,wrongtx;
    private Button btn;

    private UnLoadingInfoDataHelper unLoadingInfoDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unLoadingInfoDataHelper = new UnLoadingInfoDataHelper(this);
        state = 1;
        r1 = (RadioButton) findViewById(R.id.radioButton1);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
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
        } );
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r1.isChecked()) {
                    r1.setChecked(false);
                }
                r2.setChecked(true);
                state = 2;
            }
        } );
        btn = (Button)findViewById(R.id.reload_compare);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String b1 = barcode1.getText().toString();
                String b2 = barcode2.getText().toString();
                if(b1.equals(b2)){
                    righttx.setVisibility(View.VISIBLE);
                    wrongtx.setVisibility(View.GONE);
                }else{
                    wrongtx.setVisibility(View.VISIBLE);
                    righttx.setVisibility(View.GONE);
                }
            }
        });
        barcode1 = (TextView) findViewById(R.id.reload_barcode1_textview);
        barcode2 = (TextView) findViewById(R.id.reload_barcode2_textview);
        righttx = (TextView)findViewById(R.id.relodscan_right_text);
        wrongtx = (TextView)findViewById(R.id.relodscan_wrong_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_reload_capture;
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        super.handleDecode(rawResult, barcode, scaleFactor);
        ParsedResult cusparsedResult=  ResultParser.parseResult(rawResult);
        if(state == 1){
            if(parsedResult1 == null){
                parsedResult1 = cusparsedResult;
                beepManager.playBeepSoundAndVibrate();
                barcode1.setText(parsedResult1.getDisplayResult());
            }else{
                if(!parsedResult1.getDisplayResult().equals(cusparsedResult.getDisplayResult())){
                    parsedResult1 = cusparsedResult;
                    beepManager.playBeepSoundAndVibrate();
                    barcode1.setText(parsedResult1.getDisplayResult());
                }
            }
        }else{
            if(parsedResult2 == null){
                parsedResult2 = cusparsedResult;
                beepManager.playBeepSoundAndVibrate();
                barcode2.setText(parsedResult2.getDisplayResult());
            }else{
                if(!parsedResult2.getDisplayResult().equals(cusparsedResult.getDisplayResult())){
                    parsedResult2 = cusparsedResult;
                    beepManager.playBeepSoundAndVibrate();
                    barcode2.setText(parsedResult2.getDisplayResult());
                }
            }
        }
        restartPreviewAfterDelay(0);
    }
}
