package com.thingword.alphonso.materialmanage;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.thingword.alphonso.materialmanage.fragment.BlankFragment;
import com.thingword.alphonso.materialmanage.fragment.BookFragment;
import com.thingword.alphonso.materialmanage.fragment.GameFragment;
import com.thingword.alphonso.materialmanage.fragment.HomeFragment;
import com.thingword.alphonso.materialmanage.fragment.MusicFragment;
import com.thingword.alphonso.materialmanage.fragment.TvFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements BottomNavigationBar.OnTabSelectedListener {
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_DEFAULT);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        BadgeItem numberBadgeItem = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColor(Color.RED)
                .setText("5")
                .setHideOnSelect(true);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_home_white_24dp, "Home").setActiveColorResource(R.color.orange).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.ic_book_white_24dp, "Books").setActiveColorResource(R.color.teal))
                .addItem(new BottomNavigationItem(R.mipmap.ic_music_note_white_24dp, "Music").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.mipmap.ic_tv_white_24dp, "Movies & TV").setActiveColorResource(R.color.brown))
                .addItem(new BottomNavigationItem(R.mipmap.ic_videogame_asset_white_24dp, "Games").setActiveColorResource(R.color.grey).setBadgeItem(numberBadgeItem))
                .setFirstSelectedPosition(0)
                .initialise();

        mViewPager = (ViewPager)findViewById(R.id.viewPager) ;
        mFragments = getFragments();
        mViewPager.setAdapter(new MyFragmentPageAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(6);
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int position) {
        Log.e("testcc","onTabSelected:"+position);
        mViewPager.setCurrentItem(position,false);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance("HomeFragment"));
        fragments.add(BookFragment.newInstance("BookFragment"));
        fragments.add(GameFragment.newInstance("GameFragment"));
        fragments.add(MusicFragment.newInstance("MusicFragment"));
        fragments.add(TvFragment.newInstance("TvFragment"));
        return fragments;
    }

    public class MyFragmentPageAdapter extends FragmentPagerAdapter {

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
