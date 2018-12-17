package com.spresto.righttobeforgotten.async;

import android.content.Context;
import android.os.AsyncTask;

import com.spresto.righttobeforgotten.interfaces.DoNextPageTaskListener;

/**
 * Created by spresto on 2018-10-28.
 */

public class DoNextPageTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = DoNextPageTask.class.getSimpleName();

    private Context context;
    private DoNextPageTaskListener doNextPageTask;
    private String currentSite;

    public DoNextPageTask(Context context, DoNextPageTaskListener doNextPageTask, String currentSite) {
        this.context = context;
        this.doNextPageTask = doNextPageTask;
        this.currentSite = currentSite;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
