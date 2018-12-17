package com.spresto.righttobeforgotten.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spresto.righttobeforgotten.MySharedPreferences;
import com.spresto.righttobeforgotten.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sPresto Co.,Ltd on 2018-10-04.
 */

public class SettingFragment extends PreferenceFragment{
    private static final String TAG = SettingFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference siteUrl = findPreference(getResources().getString(R.string.pref_select_url));
        siteUrl.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showMultipleSiteUrlDialog();
                return true;
            }
        });

        CheckBoxPreference wholeSitePref = (CheckBoxPreference) findPreference(getResources().getString(R.string.pref_whole_key));
        wholeSitePref.setChecked(MySharedPreferences.getPref(getActivity()));
        wholeSitePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MySharedPreferences.setPref(getActivity(), (boolean) newValue);
                return true;
            }
        });

        Preference aboutDonate = findPreference(getString(R.string.pref_donate_key));
        aboutDonate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDonateDialog();
                return true;
            }
        });

        Preference aboutCompany = findPreference(getString(R.string.pref_company_key));
        aboutCompany.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showCompanyInfoDialog();
                return true;
            }
        });

    }

    private void showMultipleSiteUrlDialog(){
        final List<String> urlKeyItems = new ArrayList<>();
        final List selectedItems = new ArrayList();
        boolean[] isChecked;
        Set<String> savedSet;
        String[] keys = getResources().getStringArray(R.array.site_key);
        for(int i = 0; i < keys.length; i++){
            urlKeyItems.add(keys[i]);
        }

        if(MySharedPreferences.getSiteUrl(getActivity()) == null){
            isChecked = new boolean[keys.length];
            for(int i = 0; i < isChecked.length; i++){
                isChecked[i] = false;
            }
        } else {
            savedSet = MySharedPreferences.getSiteUrl(getActivity());
            isChecked = new boolean[keys.length];
            // 처음에 false로 초기화
            for(int startIndex = 0; startIndex < isChecked.length; startIndex++){
                isChecked[startIndex] = false;
            }

            for(String key : savedSet){
                for(int i = 0 ; i < keys.length; i++){
                    if(key.equals(keys[i])){
                        isChecked[i] = true;
                        selectedItems.add(i);
                    }
                }
            }
        }

        final CharSequence[] items = urlKeyItems.toArray(new String[urlKeyItems.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.site_url_checker_title));
        builder.setMultiChoiceItems(items, isChecked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Log.e(TAG, "isChecked: "+isChecked);
                        Log.e(TAG, "which: "+which);
                        if(isChecked){
                            selectedItems.add(which);
                        } else if(selectedItems.contains(which)) {
                            selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });

        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 아무것도 선택 안 할 때, 1개 이상 선택 할 때 나누어 처리
                        Set<String> set = new HashSet<>();
                        for(int i = 0; i < selectedItems.size(); i++){
                            int index = (int) selectedItems.get(i);
                            set.add(urlKeyItems.get(index));
                        }

                        if(set.size() == 0){
                            MySharedPreferences.deleteSiteUrl(getActivity());
                        } else {
                            MySharedPreferences.setSiteUrl(getActivity(), set);
                        }
                    }
                });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }

    private void showDonateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.donate_dialog_title));
        builder.setMessage(getString(R.string.donate_dialog_message));
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void showCompanyInfoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.company_dialog_title));
        builder.setMessage(getString(R.string.company_dialog_message));
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
