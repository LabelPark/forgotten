package com.spresto.righttobeforgotten.interfaces;

import com.spresto.righttobeforgotten.model.SQLiteModel;

import java.util.ArrayList;

/**
 * Created by spresto on 2018-09-19.
 */

public interface HttpNotFoundListener {
    void onClear(ArrayList<SQLiteModel> data);
}
