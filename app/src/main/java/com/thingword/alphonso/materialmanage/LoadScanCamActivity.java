package com.thingword.alphonso.materialmanage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
public class LoadScanCamActivity extends CaptureActivity{
    ParsedResult parsedResult;
    private int printnum;
    private int printmaxNum = 10;
    private int printminNum = 1;
    protected View resultView;
    protected TextView scanRightView;
    protected TextView scanWrongView;
    protected Button printButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultView = findViewById(R.id.result_view);
        scanRightView = (TextView) findViewById(R.id.scan_right_text);
        scanWrongView = (TextView) findViewById(R.id.scan_wrong_text);
        printButton = (Button) findViewById(R.id.print_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        super.handleDecode(rawResult, barcode, scaleFactor);
        ParsedResult cusparsedResult=  ResultParser.parseResult(rawResult);
        if(parsedResult == null){
            printButton.setVisibility(View.VISIBLE);
            parsedResult = cusparsedResult;
            beepManager.playBeepSoundAndVibrate();
            ImageView barcodeImageView = (ImageView) findViewById(com.google.zxing.client.android.R.id.barcode_image_view);
            barcodeImageView.setImageBitmap(barcode);
            TextView contentView = (TextView) findViewById(com.google.zxing.client.android.R.id.content_text_view);
            contentView.setText(parsedResult.getDisplayResult());
        }else{
            if(!parsedResult.getDisplayResult().equals(cusparsedResult.getDisplayResult())){
                parsedResult = cusparsedResult;
                beepManager.playBeepSoundAndVibrate();
                ImageView barcodeImageView = (ImageView) findViewById(com.google.zxing.client.android.R.id.barcode_image_view);
                barcodeImageView.setImageBitmap(barcode);
                TextView contentView = (TextView) findViewById(com.google.zxing.client.android.R.id.content_text_view);
                contentView.setText(parsedResult.getDisplayResult());
            }
        }
        restartPreviewAfterDelay(0);
    }
    
    @Override
    public void printClick(View V) {
        super.printClick(V);
        printnum = 1;
        MaterialDialog materialDialog = new MaterialDialog.Builder(this).title("打印条码")
                .customView(R.layout.dialog_unload_print, true).onNeutral(new MaterialDialog.SingleButtonCallback(){
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        List<Bitmap> list = new ArrayList<Bitmap>();
                        Bitmap newb =  BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015,parsedResult.getDisplayResult());
                        list.add(newb);
                        try {
                            MApplication app = (MApplication) getApplication();
                            int printInt = app.getPrinter().printLable(list, printnum, -1, LoadScanCamActivity.this,"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } )
                .negativeText(R.string.cancle).neutralText(R.string.print).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_un_barcode);
        tx.setText(parsedResult.getDisplayResult());
        final TextView printnumtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_un_printnum);
        printnumtx.setText(String.valueOf(printnum));
        ImageView img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_un_pnum_add);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(printnum<printmaxNum) {
                    printnum++;
                    printnumtx.setText(String.format("%01d", printnum));
                }
            }
        });
        img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_un_pnum_minnus);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(printnum>printminNum) {
                    printnum--;
                    printnumtx.setText(String.format("%01d", printnum));
                }
            }
        });
        materialDialog.show();
    }

}
