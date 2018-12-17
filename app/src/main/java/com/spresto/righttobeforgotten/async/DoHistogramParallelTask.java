package com.spresto.righttobeforgotten.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;

import com.spresto.righttobeforgotten.interfaces.DoHistogramTaskListener;
import com.spresto.righttobeforgotten.model.GetSiteData;
import com.spresto.righttobeforgotten.model.ResultModel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;

/**
 * Created by spresto on 2018-09-19.
 */

public class DoHistogramParallelTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = DoHistogramParallelTask.class.getSimpleName();
    private Context context;
    private DoHistogramTaskListener doHistogramTaskListener;
    private LongSparseArray<Bitmap> mArrayList = null;
    private GetSiteData data;
    private ResultModel resultModel = null;

    public DoHistogramParallelTask(Context context, DoHistogramTaskListener doHistogramTaskListener, LongSparseArray<Bitmap> mArrayList, GetSiteData data) {
        this.context = context;
        this.doHistogramTaskListener = doHistogramTaskListener;
        this.mArrayList = mArrayList;
        this.data = data;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        long start_time = System.currentTimeMillis();
        int dummyIndex = 0;
        double dummyResult = 0.0;
        double retValResult;

        for(int i = 0; i < mArrayList.size(); i++){
            retValResult = Histogram(data.getBitmap(), i);
            if(dummyResult < retValResult){
                dummyResult = retValResult;
                dummyIndex = i;
            }
        }
        resultModel = new ResultModel(
                mArrayList.get(dummyIndex),
                data.getBitmap(),
                String.valueOf((1-dummyResult)*100.0),
                data.getTitle(),
                data.getSite()
        );
        long end_time = System.currentTimeMillis();
        Log.e(TAG, "doInBackground time: "+(end_time-start_time)/1000.0);
        if(resultModel != null){
            return "true";
        }else{
            return "false";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("true")){
            doHistogramTaskListener.onHistogramCompleteListener(resultModel);
        }
    }

    private double Histogram(Bitmap bitmap, int index){
        Mat srcBase = new Mat();
        Bitmap bitmap1 = bitmap;
        org.opencv.android.Utils.bitmapToMat(bitmap1, srcBase);

        Mat srcTest1 = new Mat();
        Bitmap bitmap2 = mArrayList.get(index);
        org.opencv.android.Utils.bitmapToMat(bitmap2, srcTest1);
        if (srcBase.empty() || srcTest1.empty()) {
            System.err.println("Cannot read the images");
            System.exit(0);
        }
        Mat hsvBase = new Mat(), hsvTest1 = new Mat();
        Imgproc.cvtColor( srcBase, hsvBase, Imgproc.COLOR_BGR2HSV );
        Imgproc.cvtColor( srcTest1, hsvTest1, Imgproc.COLOR_BGR2HSV );

        int hBins = 50, sBins = 60;
        int[] histSize = { hBins, sBins };
        // hue varies from 0 to 179, saturation from 0 to 255
        float[] ranges = { 0, 180, 0, 256 };
        // Use the 0-th and 1-st channels
        int[] channels = { 0, 1 };
        Mat histBase = new Mat(), histTest1 = new Mat();

        List<Mat> hsvBaseList = Arrays.asList(hsvBase);
        Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);

        List<Mat> hsvTest1List = Arrays.asList(hsvTest1);
        Imgproc.calcHist(hsvTest1List, new MatOfInt(channels), new Mat(), histTest1, new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histTest1, histTest1, 0, 1, Core.NORM_MINMAX);


        double baseTest2 = Imgproc.compareHist( histBase, histTest1, 3 );

        srcBase.release();
        srcTest1.release();
        hsvBase.release();
        histBase.release();
        hsvTest1.release();
        histTest1.release();


        return baseTest2;
    }
}
