package com.mc.ink.mcmusicplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

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
        playFragment = new PlayFragment();
        listFragment = new ListFragment();
        fragments.add(listFragment);
        fragments.add(playFragment);
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
