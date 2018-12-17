package com.spresto.righttobeforgotten.model;


/**
 * Created by spresto on 2018-09-18.
 */

public class SQLiteModel {
    private String id;
    private String prob, title, site;
    private String ranking;
    private String isDelete;

    public SQLiteModel(String prob, String title, String site, String ranking) {
        this.prob = prob;
        this.title = title;
        this.site = site;
        this.ranking = ranking;
    }


    public void setId(String id){
        this.id = id;
    }
    public String getProb() {
        return prob;
    }

    public String getTitle() {
        return title;
    }

    public String getSite() {
        return site;
    }

    public String getRanking() {
        return ranking;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public void setIsDelete(String check){this.isDelete = check;}

    public String getIsDelete(){return isDelete;}
}
