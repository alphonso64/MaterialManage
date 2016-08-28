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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thingword.alphonso.materialmanage.CursorAdapter.DistriInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.CursorAdapter.UnLoadingInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.DataBase.DistributionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UnLoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.DistriScanCamActivity;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.ScanCamActivity;
import com.thingword.alphonso.materialmanage.app.MApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class DistributionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Unbinder unbinder;
    @BindView(R.id.distri_toolbar)
    Toolbar toolbar;
    @BindView(R.id.distri_recyclerview)
    RecyclerView mRecyclerView;
    private DistributionInfoDataHelper mDataHelper;
    private DistriInfoCursorAdapter mAdapter;

    private static final int DATE_LIST = 1;
    private Calendar calendar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_distribution, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_distribution);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_distri);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.distri_add:
                        loadDiag();
                        break;
                    case R.id.distri_clear:
                        getLoaderManager().destroyLoader(DATE_LIST);
                        break;
                    case R.id.distri_cam:
                        if(calendar != null){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String date = simpleDateFormat.format(calendar.getTime());
                            MApplication app = (MApplication) getActivity().getApplication();
                            app.setDistriWorkDate(date);
                            Intent intent = new Intent(getActivity(), DistriScanCamActivity.class);
                            startActivity(intent);
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                                    setTitle("请添加工作列表").
                                    setPositiveButton("确定",null)
                                    .create();
                            alertDialog.show();
                        }
                    default:
                }
                return false;
            }
        });
        SearchView mSearchView = (SearchView) toolbar.findViewById(R.id.load_search);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataHelper = new DistributionInfoDataHelper(getActivity());
        mAdapter = new DistriInfoCursorAdapter(getActivity());
//        mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    public static DistributionFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        DistributionFragment fragment = new DistributionFragment();
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
                .customView(R.layout.dialog_distri_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        calendar = tempCalendar;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(calendar.getTime());
                        getLoaderManager().restartLoader(DATE_LIST, null, DistributionFragment.this);
                    }
                })
                .negativeText(R.string.cancle).build();
        CalendarView cv = (CalendarView) materialDialog.getCustomView().findViewById(R.id.distri_calendarView);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                tempCalendar.set(year, month, dayOfMonth);
            }
        });
        materialDialog.show();
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
}
