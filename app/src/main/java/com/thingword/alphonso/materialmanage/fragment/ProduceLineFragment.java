package com.thingword.alphonso.materialmanage.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thingword.alphonso.materialmanage.CursorAdapter.ProductDetailCursorAdapter;
import com.thingword.alphonso.materialmanage.DataBase.ProductDetailDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductionInfoDataHelper;
import com.thingword.alphonso.materialmanage.ProductionScanCamActivity;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.ReloadingGunScanActivity;
import com.thingword.alphonso.materialmanage.ReloadingScanActivity;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.http.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class ProduceLineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Unbinder unbinder;
    @BindView(R.id.productLine_toolbar)
    Toolbar toolbar;
    @BindView(R.id.productLine_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.pline_editText)
    EditText editText;
    @BindView(R.id.pline_result_text)
    TextView textView;
    @BindView(R.id.pline_result_right_text)
    TextView scanRightView;
    @BindView(R.id.pline_result_wrong_text)
    TextView scanWrongView;
    @BindView(R.id.pline_result_view)
    LinearLayout resultView;

    private ProductDetailDataHelper mDataHelper;
    private ProductDetailCursorAdapter mAdapter;
    private static final int ALL_LIST = 2;
    private ProgressDialog progressDialog;
    private boolean hasFile ;
    private boolean isChecking;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 2){
                progressDialog.dismiss();
                CLog.e("testcc","get msg receive:");
                int size = (int) msg.obj;
                if(size == 0){
                    getLoaderManager().destroyLoader(ALL_LIST);
                    new MaterialDialog.Builder(getActivity()).title("无记录！").positiveText(R.string.sure).build().show();
                }else{
                    hasFile = true;
                    initCheckView();
                    getLoaderManager().restartLoader(ALL_LIST, null, ProduceLineFragment.this);
                }
            }else if(msg.what == 0){
                if(isChecking){
                    textView.setText((String)msg.obj);
                    editText.getText().clear();
                    checkDataValid();
                }else{
                    textView.setText((String)msg.obj);
                    editText.getText().clear();
                }
            }else if(msg.what == 1){
                scanRightView.setVisibility(View.VISIBLE);
                scanWrongView.setVisibility(View.GONE);
            }else if(msg.what == 3){
                List<String>  ls= (List<String>) msg.obj;
                mDataHelper.setDataCheckedAndNum(ls.get(1),ls.get(0));
                scanRightView.setVisibility(View.VISIBLE);
                scanWrongView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("hasFile",hasFile);
        super.onSaveInstanceState(outState);
    }

    public void initCheckView(){
        resultView.setVisibility(View.VISIBLE);
        textView.setText("");
        scanRightView.setVisibility(View.INVISIBLE);
        scanWrongView.setVisibility(View.GONE);
        isChecking = true;
    }

    public void uninitCheckView(){
        resultView.setVisibility(View.VISIBLE);
        textView.setText("");
        scanRightView.setVisibility(View.INVISIBLE);
        scanWrongView.setVisibility(View.GONE);
        isChecking = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producline, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_line);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_line);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.line_add:
                        loadDiag();
                        break;
                    case R.id.line_reloading:
                        Intent intent = new Intent(getActivity(), ReloadingGunScanActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.line_clear:
                        getLoaderManager().destroyLoader(ALL_LIST);
                        uninitCheckView();
                        hasFile = false;
                        break;
                    default:
                }
                return false;
            }
        });
//        SearchView mSearchView = (SearchView) toolbar.findViewById(R.id.load_search);
        isChecking = false;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataHelper = new ProductDetailDataHelper(getActivity());
        mAdapter = new ProductDetailCursorAdapter(getActivity());
        //mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

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
        hasFile = false;
        checkSaveInstanceState(savedInstanceState);
    }

    private void checkSaveInstanceState(Bundle state){
        if(state!=null){
            boolean flag = state.getBoolean("hasFile",false);
            if(flag) {
                hasFile = true;
                getLoaderManager().restartLoader(ALL_LIST, null, ProduceLineFragment.this);
                initCheckView();
            }
        }
    }

    private void loadDiag() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("输入产品编码与物料单号")
                .customView(R.layout.dialog_edittext2, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText edit_productcode = (EditText) dialog.getCustomView().findViewById(R.id.editText_productcode);
                        EditText edit_tasknum = (EditText) dialog.getCustomView().findViewById(R.id.editText_tasknumber);
//                        edit_tasknum.setText("0000006990");
//                        edit_productcode.setText("2290902810");
                        final String productcode = edit_productcode.getText().toString();
                        final String tasknum = edit_tasknum.getText().toString();
                        if(productcode.length() != 10 || tasknum.length() == 0){
                            new MaterialDialog.Builder(getActivity()).title("产品编码或料单号格式错误！").positiveText(R.string.sure).build().show();
                            return;
                        }
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("更新数据中");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        new Thread() {
                            @Override
                            public void run() {
                                Message msg = mHandler.obtainMessage();
                                msg.what = 2;
                                msg.obj = HttpClient.getInstance().getProductDetailInfofByCode(productcode,tasknum);
                                mHandler.sendMessage(msg);
                            }
                        }.start();
                    }
                })
                .negativeText(R.string.cancle).build();
        materialDialog.show();
    }

    public static ProduceLineFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        ProduceLineFragment fragment = new ProduceLineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       if(id == ALL_LIST){
            return mDataHelper.getCursorLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void checkDataValid(){
        String parsedResult = textView.getText().toString();
        parsedResult = parsedResult.substring(0,parsedResult.length()-1);
        boolean res = mDataHelper.isDataExist(parsedResult);
        if(res){
           loadNumPickDialog(parsedResult);
        }else{
            scanWrongView.setVisibility(View.VISIBLE);
            scanRightView.setVisibility(View.GONE);
        }
//        if(res){
//            scanRightView.setVisibility(View.VISIBLE);
//            scanWrongView.setVisibility(View.GONE);
//        }else{
//            scanWrongView.setVisibility(View.VISIBLE);
//            scanRightView.setVisibility(View.GONE);
//        }
    }

    private void loadNumPickDialog(final String invcode){
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("输入物料数量")
                .customView(R.layout.dialog_edittext3, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText edit_num = (EditText) dialog.getCustomView().findViewById(R.id.editText_num);
                        final String numText = edit_num.getText().toString();
                        Message msg = mHandler.obtainMessage();
                        msg.what = 3;
                        List<String> ls = new ArrayList<String>();
                        ls.add(numText);
                        ls.add(invcode);
                        msg.obj = ls;
                        mHandler.sendMessage(msg);
                    }
                })
                .negativeText(R.string.cancle).build();
        materialDialog.show();
    }

    private void setDataValid(int num){
//        String parsedResult = textView.getText().toString();
//        parsedResult = parsedResult.substring(0,parsedResult.length()-1);
//        boolean res = mDataHelper.setDataChecked(parsedResult);
//        if(res){
//            scanRightView.setVisibility(View.VISIBLE);
//            scanWrongView.setVisibility(View.GONE);
//        }else{
//            scanWrongView.setVisibility(View.VISIBLE);
//            scanRightView.setVisibility(View.GONE);
//        }
    }
}



