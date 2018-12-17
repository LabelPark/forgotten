package com.spresto.righttobeforgotten.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LongSparseArray;

import com.spresto.righttobeforgotten.interfaces.WholeDoHistogramTaskListener;
import com.spresto.righttobeforgotten.model.GetWholeSiteData;
import com.spresto.righttobeforgotten.model.ResultModel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by spresto on 2018-10-04.
 */

public class DoWholeHistogramParallelTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = DoWholeHistogramParallelTask.class.getSimpleName();
    private Context context;
    private WholeDoHistogramTaskListener wholeDoHistogramTaskListener;
    private LongSparseArray<Bitmap> mArrayList = null;
    private GetWholeSiteData data;
    private ResultModel resultModel = null;

    public DoWholeHistogramParallelTask(Context context, WholeDoHistogramTaskListener wholeDoHistogramTaskListener, LongSparseArray<Bitmap> mArrayList, GetWholeSiteData data) {
        this.context = context;
        this.wholeDoHistogramTaskListener = wholeDoHistogramTaskListener;
        this.mArrayList = mArrayList;
        this.data = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        int dummyIndex = 0;
        double dummyResult = 0.0;
        double retValResult;

        // 1. File -> Bitmap
        Bitmap bitmap = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/ForgottenTemp/" + data.getFile_name());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 2. Do Histogram
        for(int i = 0; i < mArrayList.size(); i++){
            retValResult = Histogram(bitmap, i);
            if(dummyResult < retValResult){
                dummyResult = retValResult;
                dummyIndex = i;
            }
        }
        resultModel = new ResultModel(
                mArrayList.get(dummyIndex),
                bitmap,
                String.valueOf((1-dummyResult)*100.0),
                data.getTitle(),
                data.getSite()
        );

        // 3. return result
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
            wholeDoHistogramTaskListener.onCompleteHistogramTask(resultModel);
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
