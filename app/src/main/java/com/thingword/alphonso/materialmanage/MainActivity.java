package com.thingword.alphonso.materialmanage;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.thingword.alphonso.materialmanage.DataBase.UserSharedPreferences;
import com.thingword.alphonso.materialmanage.Util.CLog;
import com.thingword.alphonso.materialmanage.bean.User;
import com.thingword.alphonso.materialmanage.fragment.DistributionFragment;
import com.thingword.alphonso.materialmanage.fragment.LoadingFragment;
import com.thingword.alphonso.materialmanage.fragment.ProduceLineFragment;
import com.thingword.alphonso.materialmanage.fragment.SetFragment;
import com.thingword.alphonso.materialmanage.fragment.TextFragment;
import com.thingword.alphonso.materialmanage.fragment.UnloadingFragment;
import com.thingword.alphonso.materialmanage.http.ServerConfig.Authority;
import com.thingword.alphonso.materialmanage.view.NoSrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private NoSrollViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_archive_white_24dp, R.string.tab_loading).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.ic_unarchive_white_24dp, R.string.tab_unloading).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.ic_drag_handle_white_24dp, R.string.tab_line).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.ic_account_check_white_24dp, R.string.tab_check).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.mipmap.ic_settings_white_24dp, R.string.tab_set).setActiveColorResource(R.color.colorPrimaryDark))
//                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setFocusable(false);
        bottomNavigationBar.setTabSelectedListener(this);
        initFragments();
        mViewPager = (NoSrollViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyFragmentPageAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(6);

        if(savedInstanceState!=null){
            int indexTemp= savedInstanceState.getInt("index",0);
            bottomNavigationBar.selectTab(indexTemp);
        }else{
            bottomNavigationBar.selectTab(0);
        }
    }

    public void initFragments() {
        User user = UserSharedPreferences.getCusUser(this);
        mFragments = new ArrayList<>();

        int index = 0;
        Integer authority;
        try {
            authority = Integer.parseInt(user.getAuthority());
        } catch (Exception e) {
            authority = 0;
        }

        if ((authority & Authority.LOADING_AUTHORITY) != 0) {
            mFragments.add(LoadingFragment.newInstance("入库"));
        } else {
            mFragments.add(TextFragment.newInstance("没有权限"));
        }

        if ((authority & Authority.UNLOADING_AUTHORITY) != 0) {
            mFragments.add(UnloadingFragment.newInstance("出库"));
        } else {
            mFragments.add(TextFragment.newInstance("没有权限"));
        }

//        if ((authority & Authority.DISTRIBUTION_AUTHORITY) != 0) {
//            mFragments.add(DistributionFragment.newInstance("配料"));
//        } else {
//            mFragments.add(TextFragment.newInstance("没有权限"));
//        }



        if ((authority & Authority.PRODUCTIONLINE_AUTHORITY) != 0) {
            mFragments.add(ProduceLineFragment.newInstance("产线"));
        } else {
            mFragments.add(TextFragment.newInstance("没有权限"));
        }

        mFragments.add(TextFragment.newInstance("没有权限"));

        mFragments.add(SetFragment.newInstance("设置"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", index);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(int position) {
        index = position;
//        CLog.e("testcc","onTabSelected index "+index);
        mViewPager.setCurrentItem(position, false);
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    public class MyFragmentPageAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

    }


}
