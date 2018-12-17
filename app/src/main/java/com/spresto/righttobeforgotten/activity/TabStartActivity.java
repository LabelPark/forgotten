package com.spresto.righttobeforgotten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.adapter.TabPagerAdapter;
import com.spresto.righttobeforgotten.utils.Permissions;
import com.spresto.righttobeforgotten.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sPresto Co.,Ltd on 2018-09-30.
 */

public class TabStartActivity extends AppCompatActivity {
    private static final String TAG = TabStartActivity.class.getSimpleName();
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    @BindView(R.id.viewpager_container)
    ViewPager viewPager;
    @BindView(R.id.app_toolBar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_start);
        ButterKnife.bind(this);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        initTabLayout();

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        checkPermission();
    }

    private void checkPermission() {
        if (Utils.checkPermissionsArray(getApplicationContext(), TAG, Permissions.PERMISSIONS)) {
            Toast.makeText(getApplicationContext(), getString(R.string.permission), Toast.LENGTH_SHORT).show();
        } else {
            verifyPermissions(TAG, Permissions.PERMISSIONS);
        }
    }

    public void verifyPermissions(String TAG, String[] permissions) {

        ActivityCompat.requestPermissions(
                TabStartActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.analysis)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.management)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
