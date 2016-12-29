package com.thingword.alphonso.materialmanage.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.CursorAdapter.UnLoadingInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.ScanCamActivity;
import com.thingword.alphonso.materialmanage.Util.BarCodeCreator;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.http.HttpClient;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class UnloadingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Unbinder unbinder;
    @BindView(R.id.unload_toolbar)
    Toolbar toolbar;
    @BindView(R.id.uld_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.result_text)
    TextView textView;
    @BindView(R.id.result_right_text)
    TextView scanRightView;
    @BindView(R.id.result_wrong_text)
    TextView scanWrongView;
    @BindView(R.id.unloading_result_view)
    LinearLayout resultView;

    private UnLoadingInfoDataHelper mDataHelper;
    private UnLoadingInfoCursorAdapter mAdapter;
    private static final int DATE_LIST = 1;
    private static final int DATE_LIST_NAME = 2;
    private static final int DATE_LIST_LINE = 3;
    private static final int DATE_LIST_CBATCH = 4;
    private static final int DATE_TEST = 5;
    private int printnum;
    private int printmaxNum = 10;
    private int printminNum = 1;
    private Calendar calendar;
    private boolean isChecking;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
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
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unloading, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_unloading);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_unloading);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.unload_add:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        uninitCheckView();
                        loadDiag();
                        break;
                    case R.id.unload_clear:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        uninitCheckView();
                        calendar = null;
                        break;
                    case R.id.unload_print:
                        String res = textView.getText().toString();
                        if(res.length()>0){
                            loadPrintDialog(res.substring(0,res.length()-1));
                        }else{
                            loadNoPrintDialog();
                        }
                        break;
                    case R.id.unload_name_sort:
                        if(mRecyclerView.getAdapter().getItemCount()!= 0){
                            getLoaderManager().restartLoader(DATE_LIST_NAME, null, UnloadingFragment.this);
                        }
                        break;
                    case R.id.unload_cbatch_sort:
                        if(mRecyclerView.getAdapter().getItemCount()!= 0){
                            getLoaderManager().restartLoader(DATE_LIST_CBATCH, null, UnloadingFragment.this);
                        }
                        break;
                    case R.id.unload_line_sort:
                        if(mRecyclerView.getAdapter().getItemCount()!= 0){
                            getLoaderManager().restartLoader(DATE_LIST_LINE, null, UnloadingFragment.this);
                        }
                        break;
                    default:
                }
                return false;
            }
        });

        isChecking = false;
        return view;
    }

    private void checkSaveInstanceState(Bundle state){
        if(state!=null){
            String dates = state.getString("date",null);
            if(dates!=null){
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = sdf.parse(dates);
                    calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    CLog.e("testcc",dates);
                    getLoaderManager().restartLoader(DATE_LIST, null, UnloadingFragment.this);
                    initCheckView();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(calendar != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            outState.putString("date",date);
            CLog.e("testcc","fragment onSaveInstanceState");
        }

        super.onSaveInstanceState(outState);
    }

    private void loadNoPrintDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("无扫描条码打印").
                setPositiveButton("确定", null).
                create();
        alertDialog.show();
    }

    private void loadPrintDialog(final String res) {
        printnum = 1;
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("打印条码")
                .customView(R.layout.dialog_unload_print, true).onNeutral(new MaterialDialog.SingleButtonCallback(){
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        List<Bitmap> list = new ArrayList<Bitmap>();
                        Bitmap newb =  BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015,res);
                        list.add(newb);
                        try {
                            MApplication app = (MApplication) getActivity().getApplication();
                            int printInt = app.getPrinter().printLable(list, printnum, -1, getActivity(),"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } )
                .negativeText(R.string.cancle).neutralText(R.string.print).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_un_barcode);
        tx.setText(res);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataHelper = new UnLoadingInfoDataHelper(getActivity());
        mAdapter = new UnLoadingInfoCursorAdapter(getActivity(),UnLoadingInfoCursorAdapter.UNLOADING);
//        mAdapter.setOnItemClickListener(itemClickListener);
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
        CLog.e("testcc","onActivityCreated");
        checkSaveInstanceState(savedInstanceState);
    }

    public static UnloadingFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        UnloadingFragment fragment = new UnloadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void loadDiag() {
        final Calendar tempCalendar = Calendar.getInstance();
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                .customView(R.layout.dialog_unload_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        calendar = tempCalendar;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(calendar.getTime());
                        TextView tx = (TextView) dialog.getCustomView().findViewById(R.id.excutor);
                        String person = tx.getText().toString();
                        if(mDataHelper.isExist(date)){
                            getLoaderManager().restartLoader(DATE_LIST, null, UnloadingFragment.this);
                            initCheckView();
                        }else {
                            loadNoDataDialog();
                        }
                    }
                })
                .negativeText(R.string.cancle).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.excutor);
        tx.setText(UserSharedPreferences.getCusUser(getActivity()).getEmploy_name());
        LinearLayout ly = (LinearLayout) materialDialog.getCustomView().findViewById(R.id.linenum_ly);
        ly.setVisibility(View.GONE);
        CalendarView cv = (CalendarView) materialDialog.getCustomView().findViewById(R.id.unload_calendarView);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                tempCalendar.set(year, month, dayOfMonth);
            }
        });
        materialDialog.show();
    }

    private void loadNoDataDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("没有数据").
                setPositiveButton("确定", null).
                create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DATE_LIST) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            return mDataHelper.getDatePersonCursorLoader(date,UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
        }else if(id == DATE_LIST_NAME){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            return mDataHelper.getDatePersonCursorLoaderOrderName(date,UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
        }else if(id == DATE_LIST_CBATCH){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            return mDataHelper.getDatePersonCursorLoaderOrderBatch(date,UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
        }else if(id == DATE_LIST_LINE){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            return mDataHelper.getDatePersonCursorLoaderOrderLine(date,UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name());
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
        //boolean res = unLoadingInfoDataHelper.setDataChecked(date,parsedResult.getDisplayResult());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(calendar.getTime());
        String parsedResult = textView.getText().toString();
        parsedResult = parsedResult.substring(0,parsedResult.length()-1);
        String employname = UserSharedPreferences.getCusUser(MApplication.getContext()).getEmploy_name();
        Cursor cursor = mDataHelper.getDataCheckedCurosr(date,parsedResult,
                employname);
        Cursor cursor_ = mDataHelper.getDataCheckedCurosr_(date,parsedResult,
                employname);
        if(cursor.getCount() == 1){
            scanRightView.setVisibility(View.VISIBLE);
            scanWrongView.setVisibility(View.GONE);
            mDataHelper.updateData(UnLoadingInfo.fromCursor(cursor));
        }else if(cursor.getCount() == 0){
            cursor = mDataHelper.getDataCheckedFuzyCurosr(date,parsedResult,
                    employname);
            cursor_ = mDataHelper.getDataCheckedFuzyCurosr_(date,parsedResult,
                    employname);
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("注意！");
        builder.setMessage("条码正确，批次错误，确认继续？");
        builder.setNegativeButton(R.string.cancle,null);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(cusor.getCount() == 1){
                    mDataHelper.updateData_(UnLoadingInfo.fromCursor(cusor));
                    mHandler.sendEmptyMessage(1);
                }else{
                    if(cusor_.getCount()>0){
                        LoadFuzyDialogInner(cusor_);
                    }else{
                        mHandler.sendEmptyMessage(1);
                    }
                }

            }
        });
        builder.show();
    }

    private  void LoadFuzyDialogInner(final Cursor cursor){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("选择对应型号");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.product_adapter_item4,
                cursor, new String[]{"cInvName","invcode","cMoCode","iQuantity"},
                new int[]{R.id.title_a,R.id.title_b,R.id.title_c,R.id.title_d}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cursor.moveToPosition(i);
                UnLoadingInfo unLoadingInfo = UnLoadingInfo.fromCursor(cursor);
                mDataHelper.updateData_(unLoadingInfo);
                mHandler.sendEmptyMessage(1);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private  void LoadDialog(final Cursor cursor){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("选择对应型号");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.product_adapter_item4,
                cursor, new String[]{"cInvName","invcode","cMoCode","iQuantity"},
                new int[]{R.id.title_a,R.id.title_b,R.id.title_c,R.id.title_d}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cursor.moveToPosition(i);
                UnLoadingInfo unLoadingInfo = UnLoadingInfo.fromCursor(cursor);
                mDataHelper.updateData(unLoadingInfo);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
