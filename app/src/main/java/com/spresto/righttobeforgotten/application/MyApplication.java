package com.spresto.righttobeforgotten.application;

import android.app.Application;

import com.spresto.righttobeforgotten.model.ResultModel;

import java.util.ArrayList;

/**
 * Created by spresto on 2018-09-30.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private ArrayList<ResultModel> models;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        models = new ArrayList<>();
    }

    public void setLists(ArrayList<ResultModel> model){
        models = model;
    }

    public ArrayList<ResultModel> getLists(){
        return models;
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public void clearData(){
        if(models != null)
            models.clear();
    }

}
