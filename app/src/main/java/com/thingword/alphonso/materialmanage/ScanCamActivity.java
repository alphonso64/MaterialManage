package com.thingword.alphonso.materialmanage;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
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
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.Util.BarCodeCreator;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thingword-A on 2016/8/28.
 */
public class ScanCamActivity extends CaptureActivity{
    ParsedResult parsedResult;
    private int printnum;
    private int printmaxNum = 10;
    private int printminNum = 1;
    private UnLoadingInfoDataHelper unLoadingInfoDataHelper;
    String date;

    protected View resultView;
    protected TextView scanRightView;
    protected TextView scanWrongView;
    protected Button printButton;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    scanRightView.setVisibility(View.VISIBLE);
                    scanWrongView.setVisibility(View.GONE);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultView = findViewById(R.id.result_view);
        scanRightView = (TextView) findViewById(R.id.scan_right_text);
        scanWrongView = (TextView) findViewById(R.id.scan_wrong_text);
        printButton = (Button) findViewById(R.id.print_button);
        unLoadingInfoDataHelper =new UnLoadingInfoDataHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        date = ((MApplication)getApplication()).getUnloadWorkDate();
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
            checkDataValid();
        }else{
            if(!parsedResult.getDisplayResult().equals(cusparsedResult.getDisplayResult())){
                parsedResult = cusparsedResult;
                beepManager.playBeepSoundAndVibrate();
                ImageView barcodeImageView = (ImageView) findViewById(com.google.zxing.client.android.R.id.barcode_image_view);
                barcodeImageView.setImageBitmap(barcode);
                TextView contentView = (TextView) findViewById(com.google.zxing.client.android.R.id.content_text_view);
                contentView.setText(parsedResult.getDisplayResult());
                checkDataValid();
            }
        }
        restartPreviewAfterDelay(0);
    }

    private void checkDataValid(){
        //boolean res = unLoadingInfoDataHelper.setDataChecked(date,parsedResult.getDisplayResult());
        Cursor cursor = unLoadingInfoDataHelper.getDataCheckedCurosr(date,parsedResult.getDisplayResult(),
                UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
        Cursor cursor_ = unLoadingInfoDataHelper.getDataCheckedCurosr_(date,parsedResult.getDisplayResult(),
                UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
        if(cursor.getCount() == 1){
            scanRightView.setVisibility(View.VISIBLE);
            scanWrongView.setVisibility(View.GONE);
            unLoadingInfoDataHelper.updateData(UnLoadingInfo.fromCursor(cursor));
        }else if(cursor.getCount() == 0){
            cursor = unLoadingInfoDataHelper.getDataCheckedFuzyCurosr(date,parsedResult.getDisplayResult(),
                    UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
            cursor_ = unLoadingInfoDataHelper.getDataCheckedFuzyCurosr_(date,parsedResult.getDisplayResult(),
                    UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
            if(cursor.getCount() >= 1){
                LoadFuzyDialpg(cursor,cursor_);
            }else if(cursor.getCount() == 0){
                scanWrongView.setVisibility(View.VISIBLE);
                scanRightView.setVisibility(View.GONE);
            }
        }else{
            if(cursor_.getCount() > 0){
                LoadDialog(cursor_);
            }
            scanRightView.setVisibility(View.VISIBLE);
            scanWrongView.setVisibility(View.GONE);
        }
    }

    private void LoadFuzyDialpg(final Cursor cusor,final Cursor cusor_){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注意！");
        builder.setMessage("条码正确，批次错误，确认继续？");
        builder.setNegativeButton(R.string.cancle,null);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(cusor.getCount() == 1){
                    unLoadingInfoDataHelper.updateData_(UnLoadingInfo.fromCursor(cusor));
                    mHandler.sendEmptyMessage(0);
                }else{
                    if(cusor_.getCount()>0){
                        LoadFuzyDialogInner(cusor_);
                    }else{
                        mHandler.sendEmptyMessage(0);
                    }
                }

            }
        });
        builder.show();
    }

    private  void LoadFuzyDialogInner(final Cursor cursor){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择对应得型号");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.product_adapter_item4,
                cursor, new String[]{"cInvName","invcode","cMoCode","iQuantity"},
                new int[]{R.id.title_a,R.id.title_b,R.id.title_c,R.id.title_d}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cursor.moveToPosition(i);
                UnLoadingInfo unLoadingInfo = UnLoadingInfo.fromCursor(cursor);
                unLoadingInfoDataHelper.updateData_(unLoadingInfo);
                mHandler.sendEmptyMessage(0);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private  void LoadDialog(final Cursor cursor){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择对应得型号");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.product_adapter_item4,
                cursor, new String[]{"cInvName","invcode","cMoCode","iQuantity"},
                new int[]{R.id.title_a,R.id.title_b,R.id.title_c,R.id.title_d}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cursor.moveToPosition(i);
                UnLoadingInfo unLoadingInfo = UnLoadingInfo.fromCursor(cursor);
                unLoadingInfoDataHelper.updateData(unLoadingInfo);
            }
        });
        builder.setCancelable(false);
        builder.show();
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
                            int printInt = app.getPrinter().printLable(list, printnum, -1, ScanCamActivity.this,"");
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
