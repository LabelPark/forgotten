package com.spresto.righttobeforgotten.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.spresto.righttobeforgotten.interfaces.PornDataTaskListener;
import com.spresto.righttobeforgotten.model.GetSiteData;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by spresto on 2018-09-17.
 */

public class GetPornDataTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = GetPornDataTask.class.getSimpleName();
    private boolean isNotFound = false;
    private String default_url, detail_url;
    private PornDataTaskListener pornDataTaskListener;
    private Context context;
    private ArrayList<GetSiteData> siteArrayList = new ArrayList<>();


    public GetPornDataTask(Context context, PornDataTaskListener pornDataTaskListener, String default_url, String detail_url) {
        this.context = context;
        this.pornDataTaskListener = pornDataTaskListener;
        this.default_url = default_url;
        this.detail_url = detail_url;
        Log.e(TAG, "default_url: "+this.default_url);
        Log.e(TAG, "detail_url: "+this.detail_url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        Document document;
        Elements elements, extraElements;
        ArrayList<String> site_url = new ArrayList<>();
        ArrayList<String> img_url = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();

        // 404 Not Found check
        try{
            URL url = new URL(detail_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode() == 404){
                isNotFound = true;
                return "404";
            } else {
                isNotFound = false;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        if(!isNotFound){
            if(default_url.contains("mingkyaa")){
                /**
                 * mingkyaa
                 */
                try{
                    document = Jsoup.connect(detail_url).get();
                    elements = document.select("td.thum > a");
                    extraElements = document.select("td.subject > a");

                    for(Element link : elements) {
                        Log.e(TAG, "site url: "+default_url+link.attr("href"));
                        Log.e(TAG, "image url: "+link.select("img").attr("src"));
                        site_url.add(default_url+link.attr("href"));
                        img_url.add(link.select("img").attr("src"));
                    }

                    for(Element link : extraElements) {
                        Log.e(TAG, "title: "+link.text());
                        title.add(link.text());
                    }

                    for(int i = 0; i < title.size(); i++){
                        GetSiteData model = new GetSiteData(
                                site_url.get(i),
                                title.get(i),
                                img_url.get(i)
                        );

                        try{
                            try{
                                Thread.sleep(1000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            model.setBitmap(Bitmap.createScaledBitmap(
                                    Glide.with(context).asBitmap().load(model.getUrl()).submit().get(),
                                    262,
                                    262,
                                    false
                            ));
                            siteArrayList.add(model);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                } catch (HttpStatusException e) {
                    Log.e(TAG, "status: "+e.getStatusCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(default_url.contains("yazaral")){
                /**
                 * yazaral
                 */
                try {
                    document = Jsoup.connect(detail_url).get();
                    elements = document.select("div.img-item > a");
                    extraElements = document.select("h2.media-heading > a");
                    for (Element link : elements) {
                        Log.e(TAG, "image url: " + link.attr("href"));
                        img_url.add(link.attr("href"));
                    }
                    for (Element link : extraElements) {
                        Log.e(TAG, "site url: " + link.attr("href"));
                        Log.e(TAG, "title: " + link.text());
                        site_url.add(link.attr("href"));
                        title.add(link.text());
                    }

                    for(int i = 0; i < title.size(); i++){
                        GetSiteData model = new GetSiteData(
                                site_url.get(i),
                                title.get(i),
                                img_url.get(i)
                        );

                        try{
                            try{
                                Thread.sleep(1000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            model.setBitmap(Bitmap.createScaledBitmap(
                                    Glide.with(context).asBitmap().load(model.getUrl()).submit().get(),
                                    262,
                                    262,
                                    false
                            ));
                            siteArrayList.add(model);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch (HttpStatusException e){
                    Log.e(TAG, "status: "+e.getStatusCode());
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                /**
                 * PornHub
                 */
                try{
                    document = Jsoup.connect(detail_url).get();
                    elements = document.select("div.img.fade.fadeUp.videoPreviewBg > a");
                    for(Element link : elements){
                        Log.e(TAG, "Parse!!");
                        GetSiteData model = new GetSiteData(
                                default_url+link.attr("href"),
                                link.attr("title"),
                                link.select("img").attr("data-thumb_url")
                        );
                        try{
                            model.setBitmap(Bitmap.createScaledBitmap(
                                    Glide.with(context).asBitmap().load(model.getUrl()).submit().get(),
                                    262,
                                    262,
                                    false
                            ));
                            siteArrayList.add(model);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(siteArrayList.size() == 0){
            return "false";
        }else{
            return "true";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("true")) {
            Log.e(TAG, "siteArrayList size(): "+siteArrayList.size());
            pornDataTaskListener.onCompleteListener(siteArrayList);
        } else if(result.equals("404")) {
            pornDataTaskListener.onCompleteListener(null);
        } else {
            // comment...
        }
    }
}
