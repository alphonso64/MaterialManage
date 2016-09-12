package com.thingword.alphonso.materialmanage.fragment;

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
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thingword.alphonso.materialmanage.CursorAdapter.LoadingInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.CursorAdapter.ProductionInfoCursorAdapter;
import com.thingword.alphonso.materialmanage.DataBase.LoadingInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductionInfoDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.ReloadingScanActivity;
import com.thingword.alphonso.materialmanage.ScanCamActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class ProduceLineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Unbinder unbinder;
    @BindView(R.id.productLine_toolbar)
    Toolbar toolbar;
    @BindView(R.id.productLine_recyclerview)
    RecyclerView mRecyclerView;

    private ProductionInfoDataHelper mDataHelper;
    private ProductionInfoCursorAdapter mAdapter;
    private Calendar calendar;
    private static final int DATE_LIST = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producline, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_line);;
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
                        Intent intent = new Intent(getActivity(), ReloadingScanActivity.class);
                        startActivity(intent);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataHelper = new ProductionInfoDataHelper(getActivity());
        mAdapter = new ProductionInfoCursorAdapter(getActivity());
        //mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadDiag() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                .customView(R.layout.dialog_production_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(calendar.getTime());
                        Log.e("testcc","date:"+date);
                        getLoaderManager().restartLoader(DATE_LIST, null, ProduceLineFragment.this);
                    }
                })
                .negativeText(R.string.cancle).build();
        CalendarView cv = (CalendarView) materialDialog.getCustomView().findViewById(R.id.production_calendarView);
        calendar = Calendar.getInstance();
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });
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
