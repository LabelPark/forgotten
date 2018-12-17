package com.spresto.righttobeforgotten.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by spresto on 2018-11-23.
 */

public class AudioExtractor extends AsyncTask<String, Integer, String> {

    public interface onResultListener{
        void onResult(String upload, String download);
    }

    private Context context;
    //private String file_path;
    private String videoFilePath;
    private ArrayList<String> videoToAudioList = new ArrayList<>();
    private ArrayList<String> videoFileList = new ArrayList<>();
    private String upload_video_to_audio_filePath, download_video_to_audio_filePath;
    private String upload_video_filePath, download_video_filePath;

    private onResultListener onResultListener;

    public void setOnResultListener(onResultListener onResultListener){
        this.onResultListener = onResultListener;
    }

    public AudioExtractor(Context context, String upload_video_filePath, String download_video_filePath) {
        this.context = context;
        //this.file_path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/"+"selectVideo/"+"extractAudio/";
        this.upload_video_to_audio_filePath = Environment.getExternalStorageDirectory().getPath() + "/Pictures/"+"selectVideo/"+"extractAudio/"+"1.mp3";
        this.download_video_to_audio_filePath = Environment.getExternalStorageDirectory().getPath() + "/Pictures/"+"selectVideo/"+"extractAudio/"+"2.mp3";

        videoToAudioList.add(this.upload_video_to_audio_filePath); videoToAudioList.add(this.download_video_to_audio_filePath);

        this.upload_video_filePath = upload_video_filePath;
        this.download_video_filePath = download_video_filePath;

        videoFileList.add(this.upload_video_filePath); videoFileList.add(this.download_video_filePath);
    }

    public AudioExtractor(Context context, String videoFilePath) {
        this.context = context;
        //this.file_path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/" +"/viToAudio/" + System.currentTimeMillis() + ".mp3";
        this.videoFilePath = videoFilePath;
    }

    @Override
    protected String doInBackground(String... strings) {
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/" + "/viToAudio/");
        if (!file.exists()) file.mkdir();
        loadFFmpeg();

        for(int i = 0; i < videoToAudioList.size(); i++){
            //String[] cmd = {"-i", videoFilePath, "-vn", "-ar", "44100", "-ac", "2", "-ab", "192", "-f", "mp3", file_path};
            String[] cmd = {"-i", videoFileList.get(i), "-vn", "-ar", "44100", "-ac", "2", "-ab", "192", "-f", "mp3", videoToAudioList.get(i)};
            conversion(cmd, i);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(onResultListener != null){
            onResultListener.onResult(upload_video_to_audio_filePath, download_video_to_audio_filePath);
        }
    }

    private void loadFFmpeg() {
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    public void conversion(String[] cmd, final int index) {
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.e("progress", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e("fail", message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("success", message);

                    //renewalFile(file_path);
                    renewalFile(videoToAudioList.get(index));
                }

                @Override
                public void onFinish() {
                }
            });

            Log.e("Cmd", cmd.toString());
        } catch (FFmpegCommandAlreadyRunningException e) {

            e.printStackTrace();
        }
    }

    private void renewalFile(String path) {
        File file = new File(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(file);
            scanIntent.setData(contentUri);
            context.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file));
            context.sendBroadcast(intent);
        }
    }
}
