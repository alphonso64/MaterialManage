package com.thingword.alphonso.materialmanage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;
import com.thingword.alphonso.materialmanage.R;
import com.thingword.alphonso.materialmanage.http.HttpClient;

/**
 * Created by WangChang on 2016/5/15.
 */
public class LoadingFragment extends Fragment {
    Button btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView text = (TextView) getActivity().findViewById(R.id.tv3);
        text.setText(getArguments().getString("ARGS"));
    }

    public static LoadingFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
