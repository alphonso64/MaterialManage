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
    ParsedResult parsedResult;
    RadioButton r1;
    RadioButton r2;

    private UnLoadingInfoDataHelper unLoadingInfoDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unLoadingInfoDataHelper = new UnLoadingInfoDataHelper(this);
        r1 = (RadioButton) findViewById(R.id.radioButton1);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (r2.isChecked()) {
                    r2.setChecked(false);
                }
                r1.setChecked(true);
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (r1.isChecked()) {
                    r1.setChecked(false);
                }
                r2.setChecked(true);
            }
        });
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
        ParsedResult cusparsedResult = ResultParser.parseResult(rawResult);
        if (parsedResult == null) {
            parsedResult = cusparsedResult;
            beepManager.playBeepSoundAndVibrate();
//            ImageView barcodeImageView = (ImageView) findViewById(com.google.zxing.client.android.R.id.barcode_image_view);
//            barcodeImageView.setImageBitmap(barcode);
//            TextView contentView = (TextView) findViewById(com.google.zxing.client.android.R.id.content_text_view);
//            contentView.setText(parsedResult.getDisplayResult());
        } else {
            if (!parsedResult.getDisplayResult().equals(cusparsedResult.getDisplayResult())) {
                parsedResult = cusparsedResult;
                beepManager.playBeepSoundAndVibrate();
//                ImageView barcodeImageView = (ImageView) findViewById(com.google.zxing.client.android.R.id.barcode_image_view);
//                barcodeImageView.setImageBitmap(barcode);
//                TextView contentView = (TextView) findViewById(com.google.zxing.client.android.R.id.content_text_view);
//                contentView.setText(parsedResult.getDisplayResult());
            }
        }
        restartPreviewAfterDelay(0);
    }
}
