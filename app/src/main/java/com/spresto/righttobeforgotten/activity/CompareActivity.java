package com.spresto.righttobeforgotten.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.adapter.CompareAdapter;
import com.spresto.righttobeforgotten.application.MyApplication;
import com.spresto.righttobeforgotten.async.downloader.VideoDownloader;
import com.spresto.righttobeforgotten.dialog.SendEmailDialog;
import com.spresto.righttobeforgotten.model.ResultModel;
import com.spresto.righttobeforgotten.sqlite.DBHelper;
import com.spresto.righttobeforgotten.utils.AudioExtractor;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sPresto Co.,Ltd on 2018-09-30.
 */

public class CompareActivity extends AppCompatActivity implements
        DiscreteScrollView.ScrollListener<CompareAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<CompareAdapter.ViewHolder> {

    @BindView(R.id.sendEmailButton) Button sendEmailButton;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private static final String TAG = CompareActivity.class.getSimpleName();
    private ArrayList<ResultModel> data = null;
    private CompareAdapter adapter;
    private DBHelper dbHelper;

    private VideoDownloader videoDownloader;
    private AudioExtractor audioExtractor;
    private String upload_filePath, download_filePath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        upload_filePath = getIntent().getStringExtra("upload_url");

        MyApplication application = (MyApplication) getApplicationContext();
        data = application.getLists();
        Log.e(TAG, "list size: " + data.size());
        adapter = new CompareAdapter(this, data, sendEmailButton);
        recyclerView.setAdapter(adapter);
        dbHelper = new DBHelper(
                CompareActivity.this,
                "RIGHT_TABLE",
                null, 1
        );
        dbHelper.testDB();

        adapter.setOnAudioAnalysisListener(new CompareAdapter.onAudioAnalysisListener() {
            @Override
            public void onAnalysis(int position) {
                videoDownloader = new VideoDownloader(CompareActivity.this, data.get(position).getSite());
                videoDownloader.execute();
                videoDownloader.setOnResult(new VideoDownloader.onResult() {
                    @Override
                    public void onResult(String filePath) {
                        Log.e(TAG, "onResult file Path: "+filePath);
                        download_filePath = filePath;
                        showAudioDialog();
                    }
                });
            }
        });

        adapter.setOnSendEmailListener(new CompareAdapter.onSendEmailListener() {
            @Override
            public void onSendEmail(int position) {
                // 이메일 형식 dialog Pop Up

                ResultModel model = data.get(position);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    SendEmailDialog dialog = new SendEmailDialog(CompareActivity.this, CompareActivity.this, model);
                    dialog.show();
                } else {
                    SendEmailDialog dialog = new SendEmailDialog(CompareActivity.this, CompareActivity.this, model);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                }
            }

        });
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable CompareAdapter.ViewHolder currentHolder, @Nullable CompareAdapter.ViewHolder newCurrent) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable CompareAdapter.ViewHolder viewHolder, int adapterPosition) {

    }


    private void showAudioDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("오디오 분석 수행");
        builder.setMessage("오디오 분석을 수행 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        audioExtractor = new AudioExtractor(CompareActivity.this, upload_filePath, download_filePath);
                        audioExtractor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        audioExtractor.setOnResultListener(new AudioExtractor.onResultListener() {
                            @Override
                            public void onResult(String upload, String download) {
                                Log.e(TAG, "up: "+upload+"down: "+download);
                                AnalyzeAudio(upload, download);
                            }
                        });
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // 취소..
                    }
                });
        builder.show();
    }


    private void AnalyzeAudio(String upload_audio_filePath, String download_audio_filePath){
        // 수행...
    }

}
