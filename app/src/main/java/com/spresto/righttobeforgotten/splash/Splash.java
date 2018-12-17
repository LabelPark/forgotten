package com.spresto.righttobeforgotten.splash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.activity.TabStartActivity;
import com.spresto.righttobeforgotten.utils.Utils;

/**
 * Created by spresto on 2018-09-07.
 */

public class Splash extends AppCompatActivity {

    private static final String TAG = Splash.class.getSimpleName();
    private final int DISPLAY_TIME = 1500;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        networkInfo = Utils.getNetworkInfo(Splash.this);

        runTask();
    }

    private void runTask(){
        if(networkInfo != null && networkInfo.isConnected()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, TabStartActivity.class));
                    finish();
                }
            },DISPLAY_TIME);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.network_check_title));
            builder.setMessage(getString(R.string.network_check_message));

            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    recreate();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
