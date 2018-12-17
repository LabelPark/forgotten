package com.spresto.righttobeforgotten.interfaces;

import com.spresto.righttobeforgotten.model.GetWholeSiteData;

import java.util.ArrayList;

/**
 * Created by spresto on 2018-10-04.
 */

public interface WholePornDataTaskListener {
    void onComplete(ArrayList<GetWholeSiteData> lists);
}
