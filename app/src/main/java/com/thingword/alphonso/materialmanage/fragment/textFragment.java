package com.thingword.alphonso.materialmanage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thingword.alphonso.materialmanage.R;

import org.w3c.dom.Text;

/**
 * Created by WangChang on 2016/5/15.
 */
public class TextFragment extends Fragment{
    TextView text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        text = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        text.setText(getArguments().getString("ARGS"));
    }

    public static TextFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        TextFragment fragment = new TextFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
