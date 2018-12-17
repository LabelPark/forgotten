package com.spresto.righttobeforgotten.async;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.spresto.righttobeforgotten.interfaces.HttpNotFoundListener;
import com.spresto.righttobeforgotten.model.SQLiteModel;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.VISIBLE;

/**
 * Created by spresto on 2018-09-19.
 */

public class HttpNotFoundCheckTask extends AsyncTask<Void, Void, String> {

    private ArrayList<SQLiteModel> data;
    private ProgressBar progressBar;
    private HttpNotFoundListener httpNotFoundListener;

    public HttpNotFoundCheckTask(ArrayList<SQLiteModel> data, ProgressBar progressBar, HttpNotFoundListener httpNotFoundListener) {
        this.data = data;
        this.progressBar = progressBar;
        this.httpNotFoundListener = httpNotFoundListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    protected String doInBackground(Void... voids) {

        try{
            for(int i = 0; i < data.size(); i++){
//                try{
//                    Thread.sleep(1000);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                URL url = new URL(data.get(i).getSite());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode() == 404)
                    data.get(i).setIsDelete("Y");
                else
                    data.get(i).setIsDelete("N");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "true";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("true")){
            progressBar.setVisibility(View.GONE);
            httpNotFoundListener.onClear(data);
        }
    }
}
