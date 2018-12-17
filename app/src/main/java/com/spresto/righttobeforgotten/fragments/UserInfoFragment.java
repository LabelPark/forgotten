package com.spresto.righttobeforgotten.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.adapter.InfoManageAdapter;
import com.spresto.righttobeforgotten.async.HttpNotFoundCheckTask;
import com.spresto.righttobeforgotten.dialog.SendEmailDialog;
import com.spresto.righttobeforgotten.interfaces.HttpNotFoundListener;
import com.spresto.righttobeforgotten.model.ResultModel;
import com.spresto.righttobeforgotten.model.SQLiteModel;
import com.spresto.righttobeforgotten.sqlite.DBHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sPresto Co.,Ltd on 2018-09-30.
 */

public class UserInfoFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.frag_guideLayout) LinearLayout guideLayout;
    @BindView(R.id.frag_infoListView) ListView infoListView;
    @BindView(R.id.frag_noPost) TextView noPostText;
    @BindView(R.id.frag_progressBar) ProgressBar progressBar;

    private static final String TAG = UserInfoFragment.class.getSimpleName();
    private static boolean isOpen = false;

    private DBHelper dbHelper;
    private ArrayList<SQLiteModel> data;
    private InfoManageAdapter adapter;
    private HttpNotFoundListener httpNotFoundListener;
    private HttpNotFoundCheckTask runTask;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userinfo, container,false);
        ButterKnife.bind(this, view);

        dbHelper = new DBHelper(
                getContext(),
                "RIGHT_TABLE",
                null,1
        );
        dbHelper.testDB();

        httpNotFoundListener = new HttpNotFoundListener() {
            @Override
            public void onClear(ArrayList<SQLiteModel> data) {
                setAdapter(data);
            }
        };

        //checkNoDataFromDB();

        return view;
    }

    private void setAdapter(final ArrayList<SQLiteModel> data){
        infoListView.setVisibility(View.VISIBLE);
        adapter = new InfoManageAdapter(getContext(),R.layout.info_list_item,data);
        infoListView.setAdapter(adapter);

        adapter.setOnResendEmail(new InfoManageAdapter.onReSendEmailListener() {
            @Override
            public void onResendEmail(int position) {
                ResultModel model = new ResultModel(
                  null,null,
                        data.get(position).getProb(),
                        data.get(position).getTitle(),
                        data.get(position).getSite()
                );
                //Toast.makeText(getContext(), "이메일 재전송",Toast.LENGTH_SHORT).show();
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    SendEmailDialog dialog = new SendEmailDialog(getContext(), getActivity(), model);
                    dialog.show();
                } else {
                    SendEmailDialog dialog = new SendEmailDialog(getContext(), getActivity(), model);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                }
            }
        });
    }

    private void checkNoDataFromDB(){
        if(getDataFromSQLiteDB() == 0){
            guideLayout.setVisibility(View.GONE);
            infoListView.setVisibility(View.GONE);
            noPostText.setVisibility(View.VISIBLE);
        }else{
            // data가 있다는 조건, 404확인
            runTask = new HttpNotFoundCheckTask(data, progressBar, httpNotFoundListener);
            runTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private int getDataFromSQLiteDB(){
        data = dbHelper.getAllModel();
        Log.e(TAG, "data.size()"+data.size());
        return data.size();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !isOpen){
            checkNoDataFromDB();
            isOpen = true;
        }else{

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(runTask != null && runTask.getStatus() == AsyncTask.Status.RUNNING){
            runTask.cancel(true);
        }

    }
}
