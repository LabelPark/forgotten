package com.spresto.righttobeforgotten.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spresto.righttobeforgotten.R;

/**
 * Created by spresto on 2018-11-07.
 */

public class GifProgressDialog {
    private Activity activity;
    private Dialog dialog;
    private TextView msgTextView;

    public GifProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog(){
        dialog = new Dialog(activity);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_gif_dialog);

        ImageView gifImageView = dialog.findViewById(R.id.gif_dialog_view);
        msgTextView = dialog.findViewById(R.id.dialog_text);

        Glide.with(activity).asGif().load(R.drawable.analysis_gif).into(gifImageView);
        msgTextView.setText("영상을 분석하고 있습니다...\n(모든 페이지 검사일 경우 최소 30분 이상이 소요)");
        dialog.show();

    }

    public void setStateMessage(String msg){
        msgTextView.setText(msg);
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
