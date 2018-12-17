package com.spresto.righttobeforgotten.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.spresto.righttobeforgotten.R;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by spresto on 2018-11-07.
 */

public class SendEmailTask extends AsyncTask<Void, Void, String> {
    public interface onCompleteListener{
        void onComplete(String result);
    }

    public void setOnCompleteListener(onCompleteListener onCompleteListener){
        this.onCompleteListener = onCompleteListener;
    }

    private static final String TAG = SendEmailTask.class.getSimpleName();
    private onCompleteListener onCompleteListener;
    private Context context;
    private String title, detailAddress, receiverAddress;

    public SendEmailTask(Context context, String title, String detailAddress, String receiverAddress) {
        this.context = context;
        this.title = title;
        this.detailAddress = detailAddress;
        this.receiverAddress = receiverAddress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String responseCode = null;
        try{
            String url = context.getString(R.string.send_email_url);
            URL urlObject = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("POST");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title",title);                  // 영상 제목
            jsonObject.put("url_info",detailAddress);       // detail site
            jsonObject.put("site_email",receiverAddress);   // 받는 사람

            String json = jsonObject.toString();

            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(json.getBytes());
            os.flush();

            responseCode = String.valueOf(connection.getResponseCode());
            Log.e(TAG, "response code: "+connection.getResponseCode());
        } catch (Exception e){
            e.printStackTrace();
        }


        return responseCode;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(onCompleteListener != null){
            onCompleteListener.onComplete(result);
        }
    }
}
