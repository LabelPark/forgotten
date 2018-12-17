package com.spresto.righttobeforgotten.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.spresto.righttobeforgotten.MySharedPreferences;
import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.activity.CompareActivity;
import com.spresto.righttobeforgotten.application.MyApplication;
import com.spresto.righttobeforgotten.async.DoHistogramParallelTask;
import com.spresto.righttobeforgotten.async.DoWholeHistogramParallelTask;
import com.spresto.righttobeforgotten.async.GetPornDataTask;
import com.spresto.righttobeforgotten.async.GetWholePornDataTask;
import com.spresto.righttobeforgotten.audio.CheapWAV;
import com.spresto.righttobeforgotten.audio.CompareWAV;
import com.spresto.righttobeforgotten.dialog.GifProgressDialog;
import com.spresto.righttobeforgotten.helper.GetFramesFromVideo;
import com.spresto.righttobeforgotten.interfaces.DoGetFramesFromVideoListener;
import com.spresto.righttobeforgotten.interfaces.DoGetFramesOverFromVideoListener;
import com.spresto.righttobeforgotten.interfaces.DoHistogramTaskListener;
import com.spresto.righttobeforgotten.interfaces.DoNextPageTaskListener;
import com.spresto.righttobeforgotten.interfaces.PornDataTaskListener;
import com.spresto.righttobeforgotten.interfaces.WholeDoHistogramTaskListener;
import com.spresto.righttobeforgotten.interfaces.WholePornDataTaskListener;
import com.spresto.righttobeforgotten.model.GetSiteData;
import com.spresto.righttobeforgotten.model.GetWholeSiteData;
import com.spresto.righttobeforgotten.model.ResultModel;
import com.spresto.righttobeforgotten.sqlite.DBHelper;
import com.spresto.righttobeforgotten.utils.AudioExtractor;
import com.spresto.righttobeforgotten.utils.SexualSiteLists;
import com.spresto.righttobeforgotten.utils.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sPresto Co.,Ltd on 2018-09-30.
 */

public class AnalysisFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mainScrollView) ScrollView mainScrollView;
    @BindView(R.id.next_site_button) Button nextSiteButton;
    @BindView(R.id.left_button) Button leftButton;
    @BindView(R.id.right_button) Button rightButton;
    @BindView(R.id.testButton) Button testButton;



    private final static String TAG = AnalysisFragment.class.getSimpleName();
    private static final int REQUEST_VIDEO = 1112;
    private static int MAX_FRAME_COUNT = 140;
    private int count = 0;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mGetFramesDialog;

    private GifProgressDialog gifProgressDialog;

    private DBHelper dbHelper;
    private String filePath;
    private Uri videoUri = null;

    private DoGetFramesFromVideoListener doGetFramesFromVideoListener; // 영상 추출 Under 145
    private DoGetFramesOverFromVideoListener doGetFramesOverFromVideoListener; // 영상 추출 Over 145
    private PornDataTaskListener pornDataTaskListener;                 // 사이트 썸네일 파싱 완료 리스너
    private WholePornDataTaskListener wholePornDataTaskListener;       // 사이트 전체 썸네일 파싱 완료 리스너
    private DoHistogramTaskListener doHistogramTaskListener;           // Histogram 결과 완료 리스너
    private WholeDoHistogramTaskListener wholeDoHistogramTaskListener; // 전체 Histogram 결과 완료 리스너
    private DoNextPageTaskListener doNextPageTaskListener;


    private LongSparseArray<Bitmap> mArrayList = new LongSparseArray<>();          // 유저 비디오 비트맵 리스트
    private ArrayList<GetSiteData> countList = null;            // 파싱 완료 리스트
    private ArrayList<GetWholeSiteData> wholeCountList = null;  // 전체 파싱 완료 리스트
    private ArrayList<ResultModel> _lists = new ArrayList<>();  // Histogram 분석이 완료된 더미 리스트

    private ArrayList<String> default_site = null;
    private HashMap<String, String> siteMap = null;

    private int siteCount = 0; // 사용자가 분석을 할 사이트 갯수
    private int siteIndex = 0; // 분석할 list를 참조할 변수
    private int getFramesMaxCount = 0;
    private int max_multiple_count = 1;


    long get_frame_start_time;
    long get_frame_end_time;

    private boolean isThreadSleep = false;

    private String currentSite;

    private int pornHubIndex = 1;
    private int yazaralIndex = 1;

    private String nextPagePornHub;
    private String nextPageYazaral;


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "OpenCV library found inside package. Using it");
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallBack);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getActivity(), mLoaderCallBack);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.e(TAG, "OPenCV loaded successfully");
                }
                break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        ButterKnife.bind(this, view);
        //userVideoUploadButton = view.findViewById(R.id.all_button);
        mProgressDialog = new ProgressDialog(getContext());
        gifProgressDialog = new GifProgressDialog(getActivity());
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.gradient_end));
        dbHelper = new DBHelper(
                getContext(),
                "RIGHT_TABLE",
                null, 1
        );
        dbHelper.testDB();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                siteCount = 0;
                siteIndex = 0;
                getFramesMaxCount = 0;
                max_multiple_count = 1;
                count = 0;
                isThreadSleep = false;
                videoUri = null;

                if (mArrayList != null)
                    mArrayList.clear();
                if (countList != null)
                    countList.clear();
                if (wholeCountList != null)
                    wholeCountList.clear();
                if (_lists != null)
                    _lists.clear();
                if (default_site != null)
                    default_site.clear();
                if (siteMap != null)
                    siteMap.clear();

                leftButton.setText(getString(R.string.upload));
                rightButton.setVisibility(View.GONE);
                nextSiteButton.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "초기화 완료", Toast.LENGTH_SHORT).show();
            }
        });

        String folderName = Environment.getExternalStorageDirectory() + File.separator + "audioTest";
        Log.e(TAG, "folderName : " + folderName);
        final File folder = new File(folderName);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                Utils.copyFileToExternalStorage(getContext(), folderName, R.raw.audio1, "audio1");
                Utils.copyFileToExternalStorage(getContext(), folderName, R.raw.audio1_cut, "audio1_cut");
            }
        } else {
            File file = new File(folderName + File.separator + "audio1");
            if (!file.exists()) {
                Utils.copyFileToExternalStorage(getContext(), folderName, R.raw.audio1, "audio1");
            }

            file = new File(folderName + File.separator + "audio1_cut");
            if (!file.exists()) {
                Utils.copyFileToExternalStorage(getContext(), folderName, R.raw.audio1_cut, "audio1_cut");
            }
        }

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MySharedPreferences.getSiteUrl(getActivity()) == null) {
                    Toast.makeText(getContext(), "설정에서 분석할 사이트를 선택해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (leftButton.getText().toString().equals(getString(R.string.upload))) {
                        SexualSiteLists.allClear();
                        SexualSiteLists.initialize(getActivity(), MySharedPreferences.getSiteUrl(getActivity()));
                        default_site = SexualSiteLists.keys; // 선택된 site
                        siteMap = SexualSiteLists.siteMap;   // 선택된 site의 detail 페이지
                        siteCount = default_site.size();
                        currentSite = default_site.get(siteIndex);
                        Log.e(TAG, "siteCount: " + siteCount);
                        videoFromGallery();

                    } else if (leftButton.getText().toString().equals(getString(R.string.analysis_start))) {
                        if (videoUri == null) {
                            Toast.makeText(getContext(), "분석을 진행 하려면 비디오를 업로드 하세요", Toast.LENGTH_SHORT).show();
                        } else if (mArrayList == null) {
                            Toast.makeText(getContext(), "분석을 진행 하려면 비디오 분석을 먼저 수행 하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            //mProgressDialog.setTitle(currentSite + " " + getString(R.string.site_analysis));
                            //mProgressDialog.setMessage("영상을 분석 중....\n" + "잠시만 기다려 주십시오.");
                            //mProgressDialog.setCanceledOnTouchOutside(false);
                            //mProgressDialog.show();
                            gifProgressDialog.showDialog();
                            if (MySharedPreferences.getPref(getActivity())) {
                                // 전체 페이지 분석
                                new GetWholePornDataTask(getContext(), wholePornDataTaskListener, default_site.get(siteIndex), siteMap.get(default_site.get(siteIndex))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                // 단일 페이지 분석
                                new GetPornDataTask(getContext(), pornDataTaskListener, default_site.get(siteIndex), siteMap.get(default_site.get(siteIndex))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                    } else if (leftButton.getText().toString().equals(getString(R.string.analysis_result_check))) {
                        Intent intent = new Intent(getContext(), CompareActivity.class);
                        intent.putExtra("site_url", currentSite);
                        intent.putExtra("upload_url",filePath);
                        startActivity(intent);
                    }
                }

            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _lists.clear();
                count = 0;

                if (currentSite.contains("pornhub")) {
                    pornHubIndex++;
                    nextPagePornHub = currentSite + "/video?c=103&page=" + pornHubIndex;
                } else {
                    yazaralIndex++;
                    //nextPageYazaral = currentSite + "/bbs/board.php?bo_table=korea_video&page="+yazaralIndex;
                    nextPageYazaral = "https://www.yazaral.org" + "/bbs/board.php?bo_table=korea_video&page=" + yazaralIndex;
                }

                if (MySharedPreferences.getPref(getActivity())) {
                    wholeCountList.clear();
//                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    mProgressDialog.setTitle(currentSite + " " + getString(R.string.site_analysis));
//                    mProgressDialog.setMessage("영상을 분석 중....\n" + "잠시만 기다려 주십시오.");
//                    mProgressDialog.setCanceledOnTouchOutside(false);
//                    mProgressDialog.show();
                    gifProgressDialog.showDialog();
                    if (currentSite.contains("pornhub")) {
                        new GetWholePornDataTask(getContext(), wholePornDataTaskListener, currentSite, nextPagePornHub).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new GetWholePornDataTask(getContext(), wholePornDataTaskListener, currentSite, nextPageYazaral).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    countList.clear();
//                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    mProgressDialog.setTitle(currentSite + " " + getString(R.string.site_analysis));
//                    mProgressDialog.setMessage("영상을 분석 중....\n" + "잠시만 기다려 주십시오.");
//                    mProgressDialog.setCanceledOnTouchOutside(false);
//                    mProgressDialog.show();
                    gifProgressDialog.showDialog();
                    if (currentSite.contains("pornhub")) {
                        new GetPornDataTask(getContext(), pornDataTaskListener, currentSite, nextPagePornHub).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new GetPornDataTask(getContext(), pornDataTaskListener, currentSite, nextPageYazaral).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        });

        nextSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteIndex++;
                Log.e(TAG, "siteCount: " + (siteCount - 1));
                Log.e(TAG, "siteIndex: " + siteIndex);
                if (siteCount - 1 >= siteIndex) {
                    currentSite = default_site.get(siteIndex);
                    // 기존 lists 초기화
                    _lists.clear();
                    count = 0;
                    if (MySharedPreferences.getPref(getActivity())) {
                        wholeCountList.clear();
                    } else {
                        countList.clear();
                    }
                    // 다음 페이지 분석 진행
//                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    mProgressDialog.setTitle(currentSite + " " + getString(R.string.site_analysis));
//                    mProgressDialog.setMessage("영상을 분석 중....\n" + "잠시만 기다려 주십시오.");
//                    mProgressDialog.setCanceledOnTouchOutside(false);
//                    mProgressDialog.show();
                    gifProgressDialog.showDialog();
                    if (MySharedPreferences.getPref(getActivity())) {
                        // 전체 페이지 분석
                        new GetWholePornDataTask(getContext(), wholePornDataTaskListener, default_site.get(siteIndex), siteMap.get(default_site.get(siteIndex))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        // 단일 페이지 분석
                        new GetPornDataTask(getContext(), pornDataTaskListener, default_site.get(siteIndex), siteMap.get(default_site.get(siteIndex))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    // 분석할 페이지 없음
                    Toast.makeText(getContext(), "더는 분석할 사이트가 없습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pornDataTaskListener = new PornDataTaskListener() {
            @Override
            public void onCompleteListener(ArrayList<GetSiteData> lists) {
                if (lists != null) {
                    countList = lists;

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    for (int i = 0; i < lists.size(); i++) {
                        new DoHistogramParallelTask(getContext(), doHistogramTaskListener, mArrayList, lists.get(i)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                } else {
                    Toast.makeText(getContext(), "더 이상 분석할 페이지가 없습니다", Toast.LENGTH_SHORT).show();
                }


            }
        };
        wholePornDataTaskListener = new WholePornDataTaskListener() {
            @Override
            public void onComplete(ArrayList<GetWholeSiteData> lists) {
                wholeCountList = lists;
                Log.e(TAG, "lists size: " + lists.size());
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                for (int i = 0; i < lists.size(); i++) {
                    new DoWholeHistogramParallelTask(getContext(), wholeDoHistogramTaskListener, mArrayList, lists.get(i)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            }
        };

        doNextPageTaskListener = new DoNextPageTaskListener() {
            @Override
            public void onComplete(ResultModel model) {

            }
        };


        wholeDoHistogramTaskListener = new WholeDoHistogramTaskListener() {
            @Override
            public void onCompleteHistogramTask(ResultModel model) {
                _lists.add(model);
                count++;
                //mProgressDialog.setMessage("분석 대상: " + currentSite + "\n" + "진행상황: " + wholeCountList.size() + "개의 영상중에서 " + count + "개 분석 완료");
                gifProgressDialog.setStateMessage("분석 대상: " + currentSite + "\n" + "진행상황: " + wholeCountList.size() + "개의 영상중에서 " + count + "개 분석 완료");
                if (_lists.size() == wholeCountList.size()) {
                    Comparator comparator = new Comparator();
                    Collections.sort(_lists, comparator);
                    for (int j = 0; j < _lists.size(); j++) {
                        _lists.get(j).setRanking(String.valueOf(j + 1));
                    }
                    //mProgressDialog.dismiss();
                    gifProgressDialog.hideDialog();
                    //userVideoUploadButton.setText(getString(R.string.analysis_result_check));
                    rightButton.setVisibility(View.VISIBLE);
                    rightButton.setText(getString(R.string.next_site_button));
                    leftButton.setText(getString(R.string.analysis_result_check));
                    Toast.makeText(getContext(), "분석 완료!", Toast.LENGTH_SHORT).show();
                    // key.size() > 1 : visible
                    if (default_site.size() > 1) {
                        nextSiteButton.setVisibility(View.VISIBLE);
                        mainScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mainScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                    // 분석 완료 되면 참조 인덱스 ++
                    //siteIndex++;

                    /////////////////////////////////////////////////////////////////////////////////
                    ////////// Histogram 작업 까지 완료 된 후, List, Adapter 등의 setting //////////////
                    /////////////////////////////////////////////////////////////////////////////////

                    MyApplication application = (MyApplication) getActivity().getApplicationContext();
                    application.setLists(_lists);
                    Log.e(TAG, "app lists: " + application.getLists().size());

                    Intent intent = new Intent(getContext(), CompareActivity.class);
                    //intent.putExtra("site_url", default_site.get(siteIndex - 1));
                    intent.putExtra("site_url", currentSite);
                    intent.putExtra("upload_url",filePath);
                    startActivity(intent);

                } else {
                    Log.e(TAG, "Not Enough!!");
                }
            }
        };

        doHistogramTaskListener = new DoHistogramTaskListener() {
            @Override
            public void onHistogramCompleteListener(ResultModel model) {
                _lists.add(model);
                count++;
                //mProgressDialog.setMessage("분석 대상: " + currentSite + "\n" + "진행상황: " + countList.size() + "개의 영상중에서 " + count + "개 분석 완료");
                gifProgressDialog.setStateMessage("분석 대상: " + currentSite + "\n" + "진행상황: " + countList.size() + "개의 영상중에서 " + count + "개 분석 완료");
                Log.e(TAG, "count: " + count);
                Log.e(TAG, "_lists.size(): " + _lists.size());
                Log.e(TAG, "countList.size(): " + countList.size());
                if (_lists.size() == countList.size()) {
                    Comparator comparator = new Comparator();
                    Collections.sort(_lists, comparator);
                    for (int j = 0; j < _lists.size(); j++) {
                        _lists.get(j).setRanking(String.valueOf(j + 1));
                    }
                    //mProgressDialog.dismiss();
                    gifProgressDialog.hideDialog();
//                    userVideoUploadButton.setText(getString(R.string.analysis_result_check));
                    rightButton.setVisibility(View.VISIBLE);
                    rightButton.setText(getString(R.string.next_site_button));
                    leftButton.setText(getString(R.string.analysis_result_check));
                    Toast.makeText(getContext(), "분석 완료!", Toast.LENGTH_SHORT).show();
                    // key.size() > 1 : visible
                    if (default_site.size() > 1) {
                        nextSiteButton.setVisibility(View.VISIBLE);
                    }
                    // 분석 완료 되면 참조 인덱스 ++
                    //siteIndex++;

                    /////////////////////////////////////////////////////////////////////////////////
                    ////////// Histogram 작업 까지 완료 된 후, List, Adapter 등의 setting //////////////
                    /////////////////////////////////////////////////////////////////////////////////

                    MyApplication application = (MyApplication) getActivity().getApplicationContext();
                    application.setLists(_lists);
                    Log.e(TAG, "app lists: " + application.getLists().size());

                    Intent intent = new Intent(getContext(), CompareActivity.class);
                    //intent.putExtra("site_url", default_site.get(siteIndex - 1));
                    intent.putExtra("site_url", currentSite);
                    intent.putExtra("upload_url",filePath);
                    startActivity(intent);

                } else {
                    Log.e(TAG, "Not Enough!!");
                }

            }
        };
        return view;
    }

    @OnClick(R.id.testButton)
    public void onTestButtonClick(View view){
        /**
         * 16-bits wav file 해당
         */
        String folderName = Environment.getExternalStorageDirectory() + File.separator + "audioTest";
        CheapWAV cWAVCompare = new CheapWAV();
        int[] getFrameGainsForCompare = null;
        try {
            cWAVCompare.ReadFile(new File(folderName + File.separator + "audio1"));
            getFrameGainsForCompare = cWAVCompare.getFrameGains();

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        CheapWAV cWAVReference = new CheapWAV();
        int[] getFrameGainsForReference = null;
        try {
            cWAVReference.ReadFile(new File(folderName + File.separator + "audio1_cut"));
            getFrameGainsForReference = cWAVReference.getFrameGains();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        if (getFrameGainsForCompare == null || getFrameGainsForReference == null){
            Log.e(TAG, "FAIL!!");
            return;
        }

        Log.e(TAG, "getFrameGainsForCompare.length : " + getFrameGainsForCompare.length+" S "+cWAVCompare.getSampleRate()+" C"+cWAVCompare.getChannels());
        Log.e(TAG, "getFrameGainsForReference.length: " + getFrameGainsForReference.length+" S "+cWAVReference.getSampleRate()+" C"+cWAVReference.getChannels());
        if (getFrameGainsForCompare.length < getFrameGainsForReference.length) {
            CompareWAV cWAV = new CompareWAV(getFrameGainsForReference, getFrameGainsForCompare);
            ArrayList<Integer> frames = new ArrayList<>(cWAV.CompareExecute());

            ArrayList<Float> milliseconds = new ArrayList<>();
            for (int frameIdx =0; frameIdx<frames.size(); frameIdx++){
                milliseconds.add(cWAVReference.convertToMilliseconds(frames.get(frameIdx)));
            }

            Log.e(TAG, "frames.size() : "+milliseconds.size());
            if (frames.size() > 0)
                Log.e(TAG, "frames toString() 1: " + milliseconds.toString());
            else
                Log.e(TAG, "##FAIL##");
        }
        else {
            CompareWAV cWAV = new CompareWAV(getFrameGainsForCompare, getFrameGainsForReference);
            ArrayList<Integer> frames = new ArrayList<>(cWAV.CompareExecute());

            ArrayList<Float> milliseconds = new ArrayList<>();
            for (int frameIdx =0; frameIdx<frames.size(); frameIdx++){
                milliseconds.add(cWAVCompare.convertToMilliseconds(frames.get(frameIdx)));
            }

            Log.e(TAG, "frames.size() : "+milliseconds.size());
            if (frames.size() > 0)
                Log.e(TAG, "frames toString() : " + milliseconds.toString());
            else
                Log.e(TAG, "##FAIL##");
        }
    }


    private void startUserVideoExtract() {
        get_frame_start_time = System.currentTimeMillis();
        mGetFramesDialog = new ProgressDialog(getContext());
        mGetFramesDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mGetFramesDialog.setTitle(getString(R.string.video_extract));
        mGetFramesDialog.setMessage(getString(R.string.video_extract_message));
        mGetFramesDialog.setCanceledOnTouchOutside(false);
        mGetFramesDialog.show();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getContext(), videoUri);
        long videoLength = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000; // seconds
        final long thumbs = ((videoLength / 1000) / (Integer.parseInt("1") * 1000));
        final long interVal = (videoLength / (thumbs * 1000)) * 1000;
        if (thumbs > 1800)
            isThreadSleep = true;
        Log.e(TAG, "thumbs: " + thumbs);
        doGetFramesFromVideoListener = new DoGetFramesFromVideoListener() {
            @Override
            public void onComplete(Bitmap bitmap) {
                mArrayList.put(getFramesMaxCount, bitmap);
                getFramesMaxCount++;
                mGetFramesDialog.setMessage("영상에서 이미지를 추출하고 있습니다.. (" + getFramesMaxCount + "/" + thumbs + ")");
                Log.e(TAG, "getFramesMaxCount: " + getFramesMaxCount);
                Log.e(TAG, "thumbs: " + thumbs);


                if (getFramesMaxCount == thumbs) {
                    // get video frame task complete
                    get_frame_end_time = System.currentTimeMillis();
                    //userVideoUploadButton.setText(getString(R.string.analysis_start));
                    leftButton.setText(getString(R.string.analysis_start));
                    mGetFramesDialog.dismiss();

                    Log.e(TAG, "GET FRAME TIME: " + (get_frame_end_time - get_frame_start_time) / 1000.0);
                }
            }
        };

        doGetFramesOverFromVideoListener = new DoGetFramesOverFromVideoListener() {
            @Override
            public void onComplete(Bitmap bitmap) {
                mArrayList.put(getFramesMaxCount, bitmap);
                getFramesMaxCount++;
                mGetFramesDialog.setMessage("영상에서 이미지를 추출하고 있습니다.. (" + getFramesMaxCount + "/" + thumbs + ")");

                if (getFramesMaxCount == thumbs) {
                    get_frame_end_time = System.currentTimeMillis();
                    //userVideoUploadButton.setText(getString(R.string.analysis_start));
                    leftButton.setText(getString(R.string.analysis_start));
                    mGetFramesDialog.dismiss();

                    Log.e(TAG, "GET FRAME OVER 145 TASK TIME: " + (get_frame_end_time - get_frame_start_time) / 1000.0);
                }

                if (getFramesMaxCount == (MAX_FRAME_COUNT * max_multiple_count)) {
                    // 수행 작업이 완료?
                    Log.e(TAG, "MIDDLE TASK COMPLETE");
                    if ((getFramesMaxCount + MAX_FRAME_COUNT) > thumbs) {
                        DoGetVideoFrameOverCount(doGetFramesFromVideoListener,
                                doGetFramesOverFromVideoListener,
                                getFramesMaxCount,
                                thumbs, interVal);
                    } else {
                        max_multiple_count++;
                        DoGetVideoFrameOverCount(doGetFramesFromVideoListener,
                                doGetFramesOverFromVideoListener,
                                getFramesMaxCount,
                                (MAX_FRAME_COUNT * max_multiple_count), interVal);
                    }
                }

            }
        };

        if (thumbs > MAX_FRAME_COUNT) {
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            for (int i = 0; i < MAX_FRAME_COUNT; i++) {
                Log.e(TAG, "i: " + i);
                new GetFramesFromVideo(getContext(), isThreadSleep, videoUri, "1", doGetFramesFromVideoListener, doGetFramesOverFromVideoListener, i, interVal, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else {
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            for (int i = 0; i < thumbs; i++) {
                Log.e(TAG, "i: " + i);
                new GetFramesFromVideo(getContext(), isThreadSleep, videoUri, "1", doGetFramesFromVideoListener, doGetFramesOverFromVideoListener, i, interVal, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }


    }

    private void DoGetVideoFrameOverCount(DoGetFramesFromVideoListener doGetFramesFromVideoListener,
                                          DoGetFramesOverFromVideoListener doGetFramesOverFromVideoListener,
                                          int i, long end, long interVal) {
        Log.e(TAG, "DO TASK: " + i + " , " + end);
        for (int start = i; start < end; start++) {
            new GetFramesFromVideo(getContext(), isThreadSleep, videoUri, "1", doGetFramesFromVideoListener, doGetFramesOverFromVideoListener, start, interVal, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO) {
                try {
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        videoUri = selectedUri;
                        filePath = Utils.getRealPathFromUri(getContext(), selectedUri);
                        Log.e(TAG, "path: " + filePath);
                        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                        //new AudioExtractor(getContext(), videoUri, filePath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        startUserVideoExtract();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void videoFromGallery() {
        Intent intent = new Intent();
        intent.setTypeAndNormalize("video/*"); // this setting requires api 16(jellybean)
        long maxVideoSize = 60 * 1024 * 1024; // 60MB
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize);
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO);
    }

    class Comparator implements java.util.Comparator<ResultModel> {
        @Override
        public int compare(ResultModel o1, ResultModel o2) {
            double firstValue = Double.parseDouble(o1.getProb());
            double secondValue = Double.parseDouble(o2.getProb());

            if (firstValue > secondValue) {
                return -1;
            } else if (firstValue < secondValue) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
