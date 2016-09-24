package com.thingword.alphonso.materialmanage.fragment;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thingword.alphonso.materialmanage.CursorAdapter.ProductDetailCursorAdapter;
import com.thingword.alphonso.materialmanage.DataBase.ProductDetailDataHelper;
import com.thingword.alphonso.materialmanage.DataBase.ProductionInfoDataHelper;
import com.thingword.alphonso.materialmanage.ProductionScanCamActivity;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.ReloadingScanActivity;
import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    private ProductDetailDataHelper mDataHelper;
    private ProductDetailCursorAdapter mAdapter;
    private Cursor  mPrudctionCursor;
    private ProductionInfoDataHelper mPrudctionAdapter;
    private Calendar calendar;
    private ProductionInfo productionInfo;
    private static final int DATE_LIST = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producline, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.tab_line);
        ;
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
                    case R.id.line_cam:
                        if(productionInfo != null){
                            MApplication app = (MApplication) getActivity().getApplication();
                            app.setProductionInfo(productionInfo);
                             intent = new Intent(getActivity(), ProductionScanCamActivity.class);
                            startActivity(intent);
                        }else{
                            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).
                                    setTitle("请添加工作列表").
                                    setPositiveButton("确定",null)
                                    .create();
                            alertDialog.show();
                        }
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
        mDataHelper = new ProductDetailDataHelper(getActivity());
        mAdapter = new ProductDetailCursorAdapter(getActivity());
        //mAdapter.setOnItemClickListener(itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadDiag() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                .customView(R.layout.dialog_production_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = simpleDateFormat.format(calendar.getTime());
                        mPrudctionAdapter = new ProductionInfoDataHelper(getActivity());
                        mPrudctionCursor = mPrudctionAdapter.getDateCursor(date);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("选择待换线产品");
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                                R.layout.product_adapter_item,
                                mPrudctionCursor, new String[]{"spec","productcode","tasknumber"},
                                new int[]{R.id.title_a,R.id.title_b,R.id.title_c}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPrudctionCursor.moveToPosition(i);
                                productionInfo = ProductionInfo.fromCursor(mPrudctionCursor);
                                getLoaderManager().restartLoader(DATE_LIST, null, ProduceLineFragment.this);
                            }
                        });
                        builder.show();
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
            //return  mDataHelper.getDateCursorLoader(date);
            return mDataHelper.getDetailCursorLoader(date,productionInfo.getTasknumber(),productionInfo.getProductcode());
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
