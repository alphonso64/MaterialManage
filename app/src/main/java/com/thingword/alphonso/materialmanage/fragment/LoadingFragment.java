package com.thingword.alphonso.materialmanage.fragment;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.thingword.alphonso.materialmanage.CursorAdapter.LoadingInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.CursorAdapter.OnAdpaterItemClickListener;
import com.thingword.alphonso.materialmanage.DataBase.DataProvider;
import com.thingword.alphonso.materialmanage.DataBase.LoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.LoadScanCamActivity;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.Util.BarCodeCreator;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.http.HttpClient;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;

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
public class LoadingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DATE_LIST = 1;
    @BindView(R.id.load_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ld_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.load_editText)
    EditText editText;
    @BindView(R.id.load_result_text)
    TextView textView;
    @BindView(R.id.load_result_right_text)
    TextView scanRightView;
    @BindView(R.id.load_result_wrong_text)
    TextView scanWrongView;
    @BindView(R.id.loading_result_view)
    LinearLayout resultView;
    private Unbinder unbinder;
    private LoadingInfoDataHelper mDataHelper;
    private LoadingInfoCursorAdapter mAdapter;
    private Calendar calendar;
    private boolean isChecking;
    private int batch;
    private int printnum;
    private int printmaxNum = 10;
    private int printminNum = 1;
    private int batchmaxNum = 99;
    private int batchminNum = 0;
    private String printt_batch;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (isChecking) {
                    textView.setText((String) msg.obj);
                    editText.getText().clear();
                    checkDataValid();
                } else {
                    textView.setText((String) msg.obj);
                    editText.getText().clear();
                }
            } else if (msg.what == 1) {
                scanRightView.setVisibility(View.VISIBLE);
                scanWrongView.setVisibility(View.GONE);
            }
        }
    };

    public static LoadingFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DataProvider.getDBHelper();
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_loading);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_loading);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.load_add:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        uninitCheckView();
                        loadDiag();
                        break;
                    case R.id.load_print:
                        String res = textView.getText().toString();
                        if (res.length() > 0) {
                            loadPrintDialog(res.substring(0, res.length() - 1));
                        } else {
                            loadNoPrintDialog();
                        }
                        break;
                    case R.id.load_clear:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        uninitCheckView();
                        calendar = null;
                        break;
                    default:
                }
                return false;
            }
        });
        return view;
    }

    private void loadNoPrintDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("无扫描条码打印").
                setPositiveButton("确定", null).
                create();
        alertDialog.show();
    }

    private void loadPrintDialog(final String res) {
        printnum = 1;
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("打印条码")
                .customView(R.layout.dialog_unload_print, true).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        List<Bitmap> list = new ArrayList<Bitmap>();
                        Bitmap newb = BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015, res);
                        list.add(newb);
                        try {
                            MApplication app = (MApplication) getActivity().getApplication();
                            int printInt = app.getPrinter().printLable(list, printnum, -1, getActivity(), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .negativeText(R.string.cancle).neutralText(R.string.print).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_un_barcode);
        tx.setText(res);
        final TextView printnumtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_un_printnum);
        printnumtx.setText(String.valueOf(printnum));
        ImageView img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_un_pnum_add);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (printnum < printmaxNum) {
                    printnum++;
                    printnumtx.setText(String.format("%01d", printnum));
                }
            }
        });
        img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_un_pnum_minnus);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (printnum > printminNum) {
                    printnum--;
                    printnumtx.setText(String.format("%01d", printnum));
                }
            }
        });
        materialDialog.show();
    }

    private void checkDataValid() {
        String parsedResult = textView.getText().toString();
        parsedResult = parsedResult.substring(0, parsedResult.length() - 1);
        boolean res = mDataHelper.setDataChecked(parsedResult);
        if (res) {
            scanRightView.setVisibility(View.VISIBLE);
            scanWrongView.setVisibility(View.GONE);
        } else {
            scanWrongView.setVisibility(View.VISIBLE);
            scanRightView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataHelper = new LoadingInfoDataHelper(getActivity());
        mAdapter = new LoadingInfoCursorAdapter(getActivity());
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
                if (val.endsWith(" ")) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = val;
                    mHandler.sendMessage(msg);
                }
            }
        });
        checkSaveInstanceState(savedInstanceState);
    }

    private void checkSaveInstanceState(Bundle state) {
        if (state != null) {
            String dates = state.getString("date", null);
            if (dates != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = sdf.parse(dates);
                    calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    getLoaderManager().restartLoader(DATE_LIST, null, LoadingFragment.this);
                    initCheckView();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (calendar != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            outState.putString("date", date);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initCheckView() {
        resultView.setVisibility(View.VISIBLE);
        textView.setText("");
        scanRightView.setVisibility(View.INVISIBLE);
        scanWrongView.setVisibility(View.GONE);
        isChecking = true;
    }

    public void uninitCheckView() {
        resultView.setVisibility(View.VISIBLE);
        textView.setText("");
        scanRightView.setVisibility(View.INVISIBLE);
        scanWrongView.setVisibility(View.GONE);
        isChecking = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadDiag() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                .customView(R.layout.dialog_load_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(calendar.getTime());
                        TextView tx = (TextView) dialog.getCustomView().findViewById(R.id.auditor);
                        String person = tx.getText().toString();
                        //setLoadingInfoHttpReq(date, person);
                        if (mDataHelper.isExist(date)) {
                            getLoaderManager().restartLoader(DATE_LIST, null, LoadingFragment.this);
                            initCheckView();
                        } else {
                            loadNoDataDialog();
                        }

                    }
                })
                .negativeText(R.string.cancle).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.auditor);
        tx.setText(UserSharedPreferences.getCusUser(getActivity()).getUsername());
        CalendarView cv = (CalendarView) materialDialog.getCustomView().findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
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
            return mDataHelper.getDateCursorLoader(date);
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

//    public void setLoadingInfoHttpReq(final String date, String person) {
//        HttpListener listener = new HttpListener<String>() {
//            @Override
//            public void onSuccess(String s, Response<String> response) {
//                syncLoadingInfo(s, date);
//            }
//
//            @Override
//            public void onFailure(HttpException e, Response<String> response) {
//            }
//        };
//        HttpClient.getInstance().getLoadingInfo(listener, date, person);
//    }
//
//    public void syncLoadingInfo(String content, String date) {
//        getLoaderManager().restartLoader(DATE_LIST, null, this);
//        List<LoadingInfo> ls = Parser.parseLoadingInfo(content);
//        if (ls.isEmpty()) {
//            Toast toast = Toast.makeText(getActivity(),
//                    R.string.no_record, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//        } else {
//            LoadingInfoDataHelper loadingInfoDataHelper = new LoadingInfoDataHelper(getActivity());
//            loadingInfoDataHelper.deleteByCondition("cDate = ?", new String[]{date});
//            loadingInfoDataHelper.bulkInsert(ls);
//        }
//    }

//    private void loadTempDialog(){
//        printnum = 1;
//        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
//                .customView(R.layout.dialog_load_print_temp, true).onNeutral(new MaterialDialog.SingleButtonCallback(){
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        List<Bitmap> list = new ArrayList<Bitmap>();
//                        Bitmap newb =  BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015,printt_batch);
//                        list.add(newb);
//                        try {
//                            MApplication app = (MApplication) getActivity().getApplication();
//                            int printInt = app.getPrinter().printLable(list, printnum, -1, getActivity(),"");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } )
//                .negativeText(R.string.cancle).neutralText(R.string.print).build();
//
//        final EditText editText_code = (EditText) materialDialog.getCustomView().findViewById(R.id.d_code_temp);
//        editText_code.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                printt_batch= editable.toString();
//            }
//        });
//        final EditText editText_num = (EditText) materialDialog.getCustomView().findViewById(R.id.d_num_temp);
//        editText_num.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try {
//                    printnum= Integer.valueOf(editable.toString());
//                    if(printnum>10){
//                        printnum = 10;
//                    }
//                }catch (Exception e){
//                    printnum = 1;
//                }
//
//            }
//        });
//        materialDialog.show();
//    }


//    private OnAdpaterItemClickListener itemClickListener = new OnAdpaterItemClickListener() {
//        @Override
//        public void onItemClick(Object obj, int p) {
//            final LoadingInfo loadinfo = (LoadingInfo) obj;
//            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
//                    .customView(R.layout.dialog_load_print, true).onNeutral(new MaterialDialog.SingleButtonCallback(){
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            List<Bitmap> list = new ArrayList<Bitmap>();
//                            Bitmap newb =  BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015,generateBarcode(loadinfo));
//                            list.add(newb);
//                            try {
//                                MApplication app = (MApplication) getActivity().getApplication();
//                                int printInt = app.getPrinter().printLable(list, printnum, -1, getActivity(),"");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } )
//                    .negativeText(R.string.cancle).neutralText(R.string.print).build();
//            batch = 0;
//            printnum = 1;
//            TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_code);
//            tx.setText(loadinfo.getcInvCode());
//            tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_date);
//            tx.setText(loadinfo.getDate());
//            final TextView batchtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_batch);
//            batchtx.setText(String.format("%02d", batch));
//            final TextView barcodetx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_barcode);
//            barcodetx.setText(generateBarcode(loadinfo));
//            final TextView printnumtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_printnum);
//            printnumtx.setText(String.format("%01d", printnum));
//
//            ImageView img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_batch_add);
//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(batch<batchmaxNum) {
//                        batch++;
//                        batchtx.setText(String.format("%02d", batch));
//                        barcodetx.setText(generateBarcode(loadinfo));
//                    }
//                }
//            });
//            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_batch_minnus);
//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(batch>batchminNum) {
//                        batch--;
//                        batchtx.setText(String.format("%02d", batch));
//                        barcodetx.setText(generateBarcode(loadinfo));
//                    }
//                }
//            });
//            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_pnum_add);
//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(printnum<printmaxNum) {
//                        printnum++;
//                        printnumtx.setText(String.format("%01d", printnum));
//                    }
//                }
//            });
//            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_pnum_minnus);
//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(printnum>printminNum) {
//                        printnum--;
//                        printnumtx.setText(String.format("%01d", printnum));
//                    }
//                }
//            });
//            materialDialog.show();
//        }
//    };

//    private String generateBarcode(LoadingInfo info){
//        StringBuilder sb = new StringBuilder();
//        sb.append(info.getcInvCode());
//        sb.append(info.getDate().replace("-","").substring(2));
//        sb.append(String.format("%02d", batch));
//        return  sb.toString();
//    }

}
