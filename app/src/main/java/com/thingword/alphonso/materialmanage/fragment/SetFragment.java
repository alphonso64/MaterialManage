package com.thingword.alphonso.materialmanage.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.bean.ReturnUpdateInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UpdateVersion;
import com.thingword.alphonso.materialmanage.http.HttpClient;
import com.thingword.alphonso.materialmanage.http.ServerConfig.ServerMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WangChang on 2016/5/15.
 */
public class SetFragment extends Fragment {
    private Unbinder unbinder;
    private Calendar calendar;
    private ProgressDialog progressDialog;
    @BindView(R.id.set_toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_version)
    TextView app_text;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            List<String> ls = (List<String>) msg.obj;
            if (ls.size() == 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle("数据更新成功").
                        setPositiveButton("确定", null).
                        create();
                alertDialog.show();
            }else{
                ListAdapter adapter =  new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        ls);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle("数据更新失败").
                        setPositiveButton("确定", null).setAdapter(adapter,null).
                        create();
                alertDialog.show();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        unbinder = ButterKnife.bind(this, view);
        app_text.setText("版本更新 (当前"+getResources().getString(R.string.show_version)+")");
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
    public void quitClick() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("确定退出登录？").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserSharedPreferences.setLogged(getActivity(), false);
                        getActivity().finish();
                    }
                }).
                setNegativeButton("取消", null).
                create();
        alertDialog.show();
    }

    @OnClick(R.id.ly_update)
    public void updateClick() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("查询更新信息中");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        HttpListener<String> listner = new HttpListener<String>(true, true, false) {
            @Override
            public void onSuccess(String str, Response<String> response) {
                progressDialog.dismiss();
                if(str!=null){
                    Gson gson = new Gson();
                    ReturnUpdateInfo returnUpdateInfo = gson.fromJson(str, ReturnUpdateInfo.class);
                    if(ServerMessage.RETURN_SUCCESS.equals(returnUpdateInfo.getReturn_code())){
                        UpdateVersion updateVersion = returnUpdateInfo.getVerion();
                        if(updateVersion == null){
                            updateFailDialog();
                            return;
                        }
                        if(updateVersion.getVersion() == null){
                            updateFailDialog();
                            return;
                        }
                        updateSuccessDialog(updateVersion.getVersion());
                    }else{
                        updateFailDialog();
                    }
                }else{
                    updateFailDialog();
                }

            }

            @Override
            public void onFailure(HttpException e, Response<String> response) {
                progressDialog.dismiss();
                updateFailDialog();
            }
        };
        HttpClient.getInstance().getUpdateInfo(listner);
    }

    private void  updateFailDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle("获取版本信息失败").
                setPositiveButton("确定", null).
                create();
        alertDialog.show();
    }

    private void  updateSuccessDialog(String verion){
        String cur_version = getResources().getString(R.string.version);
        if(cur_version.equals(verion)){
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                    setTitle("当前已是最新版本").
                    setPositiveButton("确定", null).
                    create();
            alertDialog.show();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                    setTitle("有新的版本，确定更新?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CLog.e("testcc","更新");
                            startUpdate();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create();
            alertDialog.show();
        }
    }

    private void startUpdate(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("下载更新文件");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();

        HttpListener<File> listner = new HttpListener<File>(true, true, false) {
            @Override
            public void onLoading(AbstractRequest<File> request, long total, long len) {
                float temp = (float)len/(float)total * 100;
                progressDialog.setProgress((int)temp);
            }

            @Override
            public void onSuccess(File file, Response<File> response) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onFailure(HttpException e, Response<File> response) {
                progressDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle("下载更新文件失败").
                        setPositiveButton("确定", null).
                        create();
                alertDialog.show();
            }
        };
        HttpClient.getInstance().updateFile(listner);
    }

    @OnClick(R.id.ly_updatedata)
    public void updatedataClick() {
        final int authotity = Integer.valueOf(UserSharedPreferences.getCusUser(getActivity()).getAuthority());
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
                        tx = (TextView) dialog.getCustomView().findViewById(R.id.linenum);
                        final String linenum = tx.getText().toString();

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("更新数据中");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        new Thread() {
                            @Override
                            public void run() {
                                List<String> val = HttpClient.getInstance().getAllInfo(date, person, linenum,authotity);
                                Message message = mHandler.obtainMessage();
                                message.obj = val;
                                mHandler.sendMessage(message);
                            }
                        }.start();
                    }
                })
                .negativeText(R.string.cancle).build();
        TextView tx = (TextView) materialDialog.getCustomView().findViewById(R.id.excutor);
        tx.setText(UserSharedPreferences.getCusUser(getActivity()).getEmploy_name());
        tx = (TextView) materialDialog.getCustomView().findViewById(R.id.linenum);
        tx.setText(UserSharedPreferences.getCusUser(getActivity()).getEmploy_code());
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
