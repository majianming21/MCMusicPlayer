package com.mc.ink.mcmusicplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.fragment.ListFragment;
import com.mc.ink.mcmusicplayer.fragment.PlayFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 马坚铭
 * on 2016/12/18.
 */

public class PlayerMainActivity extends FragmentActivity {
    private List<Fragment> fragments;
    private Fragment playFragment, listFragment;
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentInit();
        ViewInit();
    }

    public void FragmentInit() {
        fragments = new ArrayList<>();
        fragments.clear();
        listFragment = new ListFragment();
        playFragment = new PlayFragment();
        fragments.add(playFragment);
        fragments.add(listFragment);
    }

    private void ViewInit() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(1);
    }


}
