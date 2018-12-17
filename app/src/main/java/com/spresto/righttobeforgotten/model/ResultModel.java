package com.spresto.righttobeforgotten.model;

import android.graphics.Bitmap;


/**
 * Created by spresto on 2018-09-08.
 */

public class ResultModel{
    private Bitmap originalBitmap, pornBitmap;
    private String prob, title, site;
    private String ranking;

    public ResultModel(Bitmap originalBitmap, Bitmap pornBitmap, String prob, String title, String site) {
        this.originalBitmap = originalBitmap;
        this.pornBitmap = pornBitmap;
        this.prob = prob;
        this.title = title;
        this.site = site;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public Bitmap getPornBitmap() {
        return pornBitmap;
    }

    public String getProb(){return prob;}

    public String getTitle() {
        return title;
    }

    public String getSite() {
        return site;
    }

    public void setRanking(String rank){
        this.ranking = rank;
    }

    public String getRanking(){
        return ranking;
    }
}
