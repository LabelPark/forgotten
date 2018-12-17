package com.spresto.righttobeforgotten.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.spresto.righttobeforgotten.fragments.AnalysisFragment;
import com.spresto.righttobeforgotten.fragments.UserInfoFragment;

/**
 * Created by spresto on 2018-09-30.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter{

    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AnalysisFragment analysisFragment = new AnalysisFragment();
                return analysisFragment;
            case 1:
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                return userInfoFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
