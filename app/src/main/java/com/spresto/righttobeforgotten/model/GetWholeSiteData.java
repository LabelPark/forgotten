package com.spresto.righttobeforgotten.model;

/**
 * Created by spresto on 2018-10-04.
 */

public class GetWholeSiteData {
    private String site, title, url, file_name;;

    public GetWholeSiteData(String site, String title, String url, String file_name) {
        this.site = site;
        this.title = title;
        this.url = url;
        this.file_name = file_name;
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

    public String getFile_name(){
        return file_name;
    }

}
