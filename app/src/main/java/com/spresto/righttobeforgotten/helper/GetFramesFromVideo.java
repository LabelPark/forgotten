package com.spresto.righttobeforgotten.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LongSparseArray;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.interfaces.DoGetFramesFromVideoListener;
import com.spresto.righttobeforgotten.interfaces.DoGetFramesOverFromVideoListener;

/**
 * Created by spresto on 2018-09-04.
 */

public class GetFramesFromVideo extends AsyncTask<Void, Void, Bitmap>{
    public interface GetLists{
        void getLists(LongSparseArray<Bitmap> list);
    }
    private String TAG = GetFramesFromVideo.class.getSimpleName();
    private Uri mVideoUri;
    private Context mContext;
    int size;
    private LongSparseArray<Bitmap> mBitmapList = null;
    public GetLists getLists;
    private int selectedTime;
    private double selectedTimeDouble;
    private boolean isDouble = false;
    public DoGetFramesFromVideoListener doGetFramesFromVideoListener;
    public DoGetFramesOverFromVideoListener doGetFramesOverFromVideoListener;

    private int start;
    private long frameInterval;
    private boolean isOver, isThreadSleep;
    public void setGetLists(GetLists getLists){
        this.getLists = getLists;
    }

    public GetFramesFromVideo(Context context, boolean isThreadSleep,Uri mVideoUri, String selectedTime,
                              DoGetFramesFromVideoListener doGetFramesFromVideoListener,
                              DoGetFramesOverFromVideoListener doGetFramesOverFromVideoListener, int start, long interval, boolean isOver) {
        this.mVideoUri = mVideoUri;
        this.mContext = context;
        if(selectedTime.contains(".")){
            isDouble = true;
            this.selectedTimeDouble = Double.parseDouble(selectedTime) * 1000.0;
            //Log.e(TAG, "selectedTime: "+this.selectedTimeDouble);
        }else{
            isDouble = false;
            this.selectedTime = Integer.parseInt(selectedTime) * 1000;
            //Log.e(TAG, "selectedTime: "+this.selectedTime);
        }
        size = context.getResources().getDimensionPixelOffset(R.dimen.frames_video_height);
        this.doGetFramesFromVideoListener = doGetFramesFromVideoListener;
        this.doGetFramesOverFromVideoListener = doGetFramesOverFromVideoListener;
        this.start = start;
        this.frameInterval = interval;
        this.isOver = isOver;
        this.isThreadSleep = isThreadSleep;
    }

//    public void init(){
//        new GetBitmapTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }

    //        private ProgressDialog dialog = new ProgressDialog(mContext);
    @Override
    protected void onPreExecute() {
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setTitle("영상 추출");
//            dialog.setMessage("영상에서 이미지를 추출하고 있습니다..");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try{
            Log.e(TAG, "doInBackground");
            LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mContext, mVideoUri);

            //long videoLength = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000; // seconds
            //Log.e(TAG, "videoLength: "+videoLength);
            final int thumbWidth = size;
            final int thumbHeight = size;
            //Log.e(TAG, "thumbWidth: "+thumbWidth);

//                long numThumbs;
//                if(!isDouble){
//                    numThumbs = (videoLength/1000) / selectedTime;
//                }else{
//                    long dummyTime = (long)selectedTimeDouble;
//                    numThumbs = (videoLength/1000) / dummyTime;
//                }
//                long interval;
//                if(!isDouble){
//                    interval = (videoLength / (numThumbs*1000)) * 1000;
//                }else{
//                    double dummyVideoLength = (double) videoLength;
//                    double numThumbs_1000 = (double) (numThumbs * 1000);
//                    double dummyVideoLength_numThumbs_1000 = (dummyVideoLength / numThumbs_1000)*1000.0;
//                    interval = (long) dummyVideoLength_numThumbs_1000;
//                }
            //Log.e(TAG, "numThumbs: "+numThumbs);
            //Log.e(TAG, "interval: "+interval);

            Log.e(TAG, "start: "+start);
            Log.e(TAG, "frameInterVal: "+frameInterval);
            bitmap = mediaMetadataRetriever.getFrameAtTime(start*frameInterval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            try{
                bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
            } catch (Exception e){
                e.printStackTrace();
            }
            mediaMetadataRetriever.release();

            if(isThreadSleep){
                try{
                    Thread.sleep(500);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }


//                for(int i = 0; i < numThumbs; i++){
//                    Log.e(TAG, "i: "+i);
//                    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i*interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                    try{
//                        bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    thumbnailList.put(i, bitmap);
//                }
//                mediaMetadataRetriever.release();
//                mBitmapList = thumbnailList;
//                Log.e(TAG, "ListSize: "+thumbnailList.size());

        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
//            if(getLists != null){
//                getLists.getLists(mBitmapList);
//            }
        if(bitmap != null){
            if(isOver){
                doGetFramesOverFromVideoListener.onComplete(bitmap);
            } else {
                doGetFramesFromVideoListener.onComplete(bitmap);
            }

        }


//            dialog.dismiss();
    }


//    private class GetBitmapTask extends AsyncTask<Void, Void, Bitmap>{
////        private ProgressDialog dialog = new ProgressDialog(mContext);
//        @Override
//        protected void onPreExecute() {
////            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////            dialog.setTitle("영상 추출");
////            dialog.setMessage("영상에서 이미지를 추출하고 있습니다..");
////            dialog.setCanceledOnTouchOutside(false);
////            dialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... voids) {
//            Bitmap bitmap = null;
//            try{
//                Log.e(TAG, "doInBackground");
//                LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();
//
//                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//                mediaMetadataRetriever.setDataSource(mContext, mVideoUri);
//
//                //long videoLength = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000; // seconds
//                //Log.e(TAG, "videoLength: "+videoLength);
//                final int thumbWidth = size;
//                final int thumbHeight = size;
//                //Log.e(TAG, "thumbWidth: "+thumbWidth);
//
////                long numThumbs;
////                if(!isDouble){
////                    numThumbs = (videoLength/1000) / selectedTime;
////                }else{
////                    long dummyTime = (long)selectedTimeDouble;
////                    numThumbs = (videoLength/1000) / dummyTime;
////                }
////                long interval;
////                if(!isDouble){
////                    interval = (videoLength / (numThumbs*1000)) * 1000;
////                }else{
////                    double dummyVideoLength = (double) videoLength;
////                    double numThumbs_1000 = (double) (numThumbs * 1000);
////                    double dummyVideoLength_numThumbs_1000 = (dummyVideoLength / numThumbs_1000)*1000.0;
////                    interval = (long) dummyVideoLength_numThumbs_1000;
////                }
//                //Log.e(TAG, "numThumbs: "+numThumbs);
//                //Log.e(TAG, "interval: "+interval);
//
//                bitmap = mediaMetadataRetriever.getFrameAtTime(start*frameInterval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                try{
//                    bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//                mediaMetadataRetriever.release();
//
//
//
////                for(int i = 0; i < numThumbs; i++){
////                    Log.e(TAG, "i: "+i);
////                    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i*interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
////                    try{
////                        bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
////                    }catch (Exception e){
////                        e.printStackTrace();
////                    }
////                    thumbnailList.put(i, bitmap);
////                }
////                mediaMetadataRetriever.release();
////                mBitmapList = thumbnailList;
////                Log.e(TAG, "ListSize: "+thumbnailList.size());
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
////            if(getLists != null){
////                getLists.getLists(mBitmapList);
////            }
//            if(bitmap != null){
//                doGetFramesFromVideoListener.onComplete(bitmap);
//            }
//
//
////            dialog.dismiss();
//        }
//    }
}
