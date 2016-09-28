package com.thingword.alphonso.materialmanage.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.http.HttpClient;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
    private UnLoadingInfoDataHelper mDataHelper;
    private UnLoadingInfoCursorAdapter mAdapter;

    private static final int DATE_LIST = 1;
    private static final int DATE_LIST_NAME = 2;
    private static final int DATE_LIST_LINE = 3;
    private static final int DATE_LIST_CBATCH = 4;
    private Calendar calendar;


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
                        loadDiag();
                        break;
                    case R.id.unload_clear:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        break;
                    case R.id.unload_cam:
                        if(mRecyclerView.getAdapter().getItemCount()!= 0){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String date = simpleDateFormat.format(calendar.getTime());
                            MApplication app = (MApplication) getActivity().getApplication();
                            app.setUnloadWorkDate(date);
                            Intent intent = new Intent(getActivity(), ScanCamActivity.class);
                            startActivity(intent);
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                                    setTitle("工作列表不能为空").
                                    setPositiveButton("确定",null)
                                    .create();
                            alertDialog.show();
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
//        SearchView mSearchView = (SearchView) toolbar.findViewById(R.id.load_search);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataHelper = new UnLoadingInfoDataHelper(getActivity());
        mAdapter = new UnLoadingInfoCursorAdapter(getActivity(),UnLoadingInfoCursorAdapter.UNLOADING);
//        mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
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
                        getLoaderManager().restartLoader(DATE_LIST, null, UnloadingFragment.this);
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

    public void setUnLoadingInfoHttpReq(final String date, String person) {
        HttpListener listener = new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                syncUnLoadingInfo(s, date);
            }

            @Override
            public void onFailure(HttpException e, Response<String> response) {
            }
        };
        HttpClient.getInstance().getUnLoadingInfo(listener, date, person);
    }

    public void syncUnLoadingInfo(String content, String date) {
        getLoaderManager().restartLoader(DATE_LIST, null, this);
        List<UnLoadingInfo> ls = Parser.parseUnLoadingInfo(content);
        if (ls.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(),
                    R.string.no_record, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            UnLoadingInfoDataHelper unloadingInfoDataHelper = new UnLoadingInfoDataHelper(getActivity());
            unloadingInfoDataHelper.deleteByCondition("cDate = ?", new String[]{date});
            unloadingInfoDataHelper.bulkInsert(ls);
        }
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
}
