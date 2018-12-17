package com.spresto.righttobeforgotten.async.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by spresto on 2018-12-09.
 */

    // 1. Check 404
    // 2. Get HTML String using Jsoup
    // 3. Parse file url
    // 4. Request file and download byte

public class VideoDownloader extends AsyncTask<Void, String, String> {
    private static String TAG = VideoDownloader.class.getSimpleName();

    public interface onResult{
        void onResult(String filePath);
    }

    private onResult onResult;


    private Context context;
    private String site_url, site;
    private ProgressDialog dialog;
    private PowerManager.WakeLock mWakeLock;
    private String path;
    private File file;
    File dummyFile;

    public void setOnResult(onResult onResult){
        this.onResult = onResult;
    }

    public VideoDownloader(Context context, String site_url) {
        this.context = context;
        this.site_url = site_url;
        if(site_url.contains("pornhub"))
            this.site = "pornhub";
        else
            this.site = "yazaral";

        this.dialog = new ProgressDialog(context);
        this.dialog.setMessage("해당 영상 다운로드 중...");
        this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(true);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuffer fileUrlString = new StringBuffer();
        String result = null;
        String htmlString;
        Document document;
        long fileSize = -1;

        try{
            // 1.
            URL url = new URL(site_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode() == 404){
                return result;
            } else {
                // 2.
                if(site.equals("pornhub")){
                    document = Jsoup.connect(site_url).get();
                    htmlString = document.html().toString();
                    Log.e(TAG, "site_url: "+site_url);
                    //Log.e(TAG, "htmlString: "+htmlString);
                    int startIndex = htmlString.indexOf("videoUrl");
                    startIndex += 10;

                    // 3.
                    while(htmlString.charAt(++startIndex) != '\"'){
                        fileUrlString.append(htmlString.charAt(startIndex));
                    }
                    // \ delete
                    for(int i = 0; i < fileUrlString.length(); i++){
                        // \(92)
                        if(fileUrlString.charAt(i) == 92){
                            fileUrlString.deleteCharAt(i);
                        }
                    }
                    Log.e(TAG, "fileUrl: "+fileUrlString.toString());


                } else {
                    document = Jsoup.connect(site_url).get();
                    htmlString = document.html().toString();
                    Log.e(TAG, "site_url: "+site_url);
                    int startIndex = htmlString.indexOf("file: \"https");
                    startIndex += 6;

                    // 3.
                    while (htmlString.charAt(++startIndex) != '\"'){
                        fileUrlString.append(htmlString.charAt(startIndex));
                    }
                    Log.e(TAG, "fileUrl: "+fileUrlString.toString());
                }

                // 4.
                int count = 0;
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;

                try{
                    path = Environment.getExternalStorageDirectory().getPath()+"/Pictures/"+"selectVideo/";
                    file = new File(path);
                    file.mkdir();
                    URL videoFile_URL = new URL(fileUrlString.toString());
                    connection = (HttpURLConnection) videoFile_URL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    fileSize = connection.getContentLength();
                    input = connection.getInputStream();

                    dummyFile = new File(file, System.currentTimeMillis()+".mp4");
                    output = new FileOutputStream(dummyFile);
                    //output = new FileOutputStream(file);
                    byte data[] = new byte[(int) fileSize];
                    long downloadedSize = 0;
                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            input.close();
                            return "-1";
                        }
                        Log.e(TAG, "TASK4");
                        downloadedSize += count;
                        if (fileSize > 0) {
                            Log.e(TAG, "TASK5");
                            float per = ((float) downloadedSize / fileSize) * 100;
                            String str = "Downloaded " + downloadedSize + "KB / " + fileSize + "KB (" + (int) per + "%)";
                            publishProgress("" + (int) ((downloadedSize * 100) / fileSize), str);
                        }
                        Log.e(TAG, "TASK6");
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                    return "NotFile";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    mWakeLock.release();
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return String.valueOf(fileSize);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e(TAG, "result: "+result);
        dialog.dismiss();
        if(result == null || result.equals("-1") || result.equals("NotFile")){
            Toast.makeText(context, "다운로드 에러! 잠시후 이용해 주세요",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "다운로드 완료!",Toast.LENGTH_SHORT).show();
            if(onResult != null)
                onResult.onResult(dummyFile.getAbsolutePath());
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgress(Integer.parseInt(values[0]));
        dialog.setMessage(values[1]);
    }
}
