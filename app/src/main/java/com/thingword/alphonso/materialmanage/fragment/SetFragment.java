package com.thingword.alphonso.materialmanage.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.http.HttpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class SetFragment extends Fragment{
    private Unbinder unbinder;
    private Calendar calendar;
    private  ProgressDialog progressDialog;
    @BindView(R.id.set_toolbar)
    Toolbar toolbar;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                progressDialog.dismiss();
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle(R.string.tab_set);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    public static SetFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        SetFragment fragment = new SetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ly_quit)
    public void quitClick(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("确定退出登录？").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserSharedPreferences.setLogged(getActivity(),false);
                        getActivity().finish();
                    }
                }).
                setNegativeButton("取消", null).
                create();
        alertDialog.show();
    }

    @OnClick(R.id.ly_updatedata)
    public void updatedataClick(){
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity()).title("选择日期")
                .customView(R.layout.dialog_unload_calendar, true)
                .positiveText(R.string.sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        final String date = simpleDateFormat.format(calendar.getTime());
                        TextView tx = (TextView) dialog.getCustomView().findViewById(R.id.excutor);
                        final String person = tx.getText().toString();

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("更新数据中");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        new Thread(){

                            @Override
                            public void run() {
                                HttpClient.getInstance().getAllInfo(date,person);
                                mHandler.sendEmptyMessage(0);
                            }}.start();
                    }
                })
                .negativeText(R.string.cancle).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.excutor);
        tx.setText(UserSharedPreferences.getCusUser(getActivity()).getUsername());
        CalendarView cv = (CalendarView) materialDialog.getCustomView().findViewById(R.id.unload_calendarView);
        calendar = Calendar.getInstance();
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });
        materialDialog.show();
    }
}
