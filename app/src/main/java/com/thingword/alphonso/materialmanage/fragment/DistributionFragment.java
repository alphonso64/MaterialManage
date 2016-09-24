package com.thingword.alphonso.materialmanage.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DistributionInfoDataHelper disDataHelper;
    private List<Map<String, String>> worshopList;
    private static final int DATE_LIST = 1;
    private Calendar calendar;
    private DistributionInfo distributionInfo;

    private UnLoadingInfoDataHelper mDataHelper;
    private UnLoadingInfoCursorAdapter mAdapter;

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
                        if(mRecyclerView.getAdapter().getItemCount()!= 0){
                            MApplication app = (MApplication) getActivity().getApplication();
                            app.setDistributionInfo(distributionInfo);
                            Intent intent = new Intent(getActivity(), DistriScanCamActivity.class);
                            startActivity(intent);
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                                    setTitle("工作列表不能为空").
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
        mDataHelper = new UnLoadingInfoDataHelper(getActivity());
        mAdapter = new UnLoadingInfoCursorAdapter(getActivity());
        disDataHelper = new DistributionInfoDataHelper(getActivity());
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
//                        getLoaderManager().restartLoader(DATE_LIST, null, DistributionFragment.this);
                        loadWorkDiag();
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

    private void loadWorkDiag(){
        worshopList = new ArrayList<Map<String, String>>();
        Map<String, String> keyValuePair = new HashMap<String, String>();
        keyValuePair.put("Name", "一车间");
        Map<String, String> keyValuePair_ = new HashMap<String, String>();
        keyValuePair_.put("Name", "二三车间");
        worshopList.add(keyValuePair);
        worshopList.add(keyValuePair_);

        ListAdapter adapter = new SimpleAdapter(getActivity(), worshopList,
                android.R.layout.simple_list_item_1, new String[] { "Name"}, new int[] { android.R.id.text1 });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("选择车间");
        builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadStoreProductionInfoView(i);
            }
        });
        builder.show();
    }

    private void loadStoreProductionInfoView(int pos){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("选择产品");
        final Cursor cusor = disDataHelper.getWorshopCursor(pos);
        Log.e("testcc","loadStoreProductionInfoView "+cusor.getCount());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.product_adapter_item,
                cusor, new String[]{"spec","productcode","tasknumber"},
                new int[]{R.id.title_a,R.id.title_b,R.id.title_c}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cusor.moveToPosition(i);
                distributionInfo = DistributionInfo.fromCursor(cusor);
                getLoaderManager().restartLoader(DATE_LIST, null, DistributionFragment.this);
            }
        });
        builder.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DATE_LIST) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            //return mDataHelper.getCursorLoader();
            return mDataHelper.getDetailCursorLoader(distributionInfo);
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
