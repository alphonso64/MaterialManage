package com.thingword.alphonso.materialmanage.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.Util.BarCodeCreator;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.LoadingInfo;
import com.thingword.alphonso.materialmanage.http.HttpClient;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;
import com.thingword.alphonso.materialmanage.http.ServerConfig.ServerMessage;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Unbinder unbinder;
    @BindView(R.id.load_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ld_recyclerview)
    RecyclerView mRecyclerView;
    private LoadingInfoDataHelper mDataHelper;
    private LoadingInfoCursorAdapter mAdapter;
    private Calendar calendar;

    private static final int CLEAR_LIST = 0;
    private static final int DATE_LIST = 1;

    private int batch;
    private int printnum;
    private int printmaxNum = 10;
    private int printminNum = 1;
    private int batchmaxNum = 99;
    private int batchminNum = 0;

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
                        loadDiag();
                        break;
                    case R.id.load_clear:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        break;
                    default:
                }
                return false;
            }
        });
        SearchView mSearchView = (SearchView) toolbar.findViewById(R.id.load_search);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataHelper = new LoadingInfoDataHelper(getActivity());
        mAdapter = new LoadingInfoCursorAdapter(getActivity());
        mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static LoadingFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
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
                        setLoadingInfoHttpReq(date, person);
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

    public void setLoadingInfoHttpReq(final String date, String person) {
        HttpListener listener = new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                syncLoadingInfo(s, date);
            }

            @Override
            public void onFailure(HttpException e, Response<String> response) {
            }
        };
        HttpClient.getInstance().getLoadingInfo(listener, date, person);
    }

    public void syncLoadingInfo(String content, String date) {
        getLoaderManager().restartLoader(DATE_LIST, null, this);
        List<LoadingInfo> ls = Parser.parseLoadingInfo(content);
        if (ls.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(),
                    R.string.no_record, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            LoadingInfoDataHelper loadingInfoDataHelper = new LoadingInfoDataHelper(getActivity());
            loadingInfoDataHelper.deleteByCondition("cDate = ?", new String[]{date});
            loadingInfoDataHelper.bulkInsert(ls);
        }
    }

    private OnAdpaterItemClickListener itemClickListener = new OnAdpaterItemClickListener() {
        @Override
        public void onItemClick(Object obj, int p) {
            final LoadingInfo loadinfo = (LoadingInfo) obj;
            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                    .customView(R.layout.dialog_load_print, true).onNeutral(new MaterialDialog.SingleButtonCallback(){
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            List<Bitmap> list = new ArrayList<Bitmap>();
                            Bitmap newb =  BarCodeCreator.createBarcode(BarCodeCreator.FORMAT_4015,generateBarcode(loadinfo));
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
            batch = 0;
            printnum = 1;
            TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_code);
            tx.setText(loadinfo.getcInvCode());
            tx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_date);
            tx.setText(loadinfo.getcDate());
            final TextView batchtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_batch);
            batchtx.setText(String.format("%02d", batch));
            final TextView barcodetx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_barcode);
            barcodetx.setText(generateBarcode(loadinfo));
            final TextView printnumtx = (TextView) materialDialog.getCustomView().findViewById(R.id.d_printnum);
            printnumtx.setText(String.format("%01d", printnum));

            ImageView img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_batch_add);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(batch<batchmaxNum) {
                        batch++;
                        batchtx.setText(String.format("%02d", batch));
                        barcodetx.setText(generateBarcode(loadinfo));
                    }
                }
            });
            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_batch_minnus);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(batch>batchminNum) {
                        batch--;
                        batchtx.setText(String.format("%02d", batch));
                        barcodetx.setText(generateBarcode(loadinfo));
                    }
                }
            });
            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_pnum_add);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(printnum<printmaxNum) {
                        printnum++;
                        printnumtx.setText(String.format("%01d", printnum));
                    }
                }
            });
            img = (ImageView) materialDialog.getCustomView().findViewById(R.id.d_pnum_minnus);
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
    };

    private String generateBarcode(LoadingInfo info){
        StringBuilder sb = new StringBuilder();
        sb.append(info.getcInvCode());
        sb.append(info.getcDate().replace("-","").substring(2));
        sb.append(String.format("%02d", batch));
        return  sb.toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == DATE_LIST) {
            Log.e("testcc", "DATE_LIST");
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
}
