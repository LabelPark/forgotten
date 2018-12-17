package com.spresto.righttobeforgotten.model;

import android.graphics.Bitmap;

import com.spresto.righttobeforgotten.utils.Utils;

/**
 * Created by spresto on 2018-09-09.
 */

public class GetSiteData {

    private String site, title, url;
    private Bitmap bitmap;
    private Bitmap scaledBitmap;

    public GetSiteData(String site, String title, String url) {
        this.site = site;
        this.title = title;
        this.url = url;
    }

    public String getSite() {
        return site;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.scaledBitmap = Utils.cropCenterBitmap(this.bitmap, 100,100);
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public Bitmap getScaledBitmap(){
        return scaledBitmap;
    }
}
