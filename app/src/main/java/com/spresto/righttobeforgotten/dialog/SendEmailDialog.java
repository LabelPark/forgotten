package com.spresto.righttobeforgotten.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.model.ResultModel;
import com.spresto.righttobeforgotten.model.SQLiteModel;
import com.spresto.righttobeforgotten.remote.SendEmailTask;
import com.spresto.righttobeforgotten.sqlite.DBHelper;

/**
 * Created by spresto on 2018-11-06.
 */

public class SendEmailDialog extends Dialog {

    private static final String TAG = SendEmailDialog.class.getSimpleName();
    private Activity activity;
    private Context context;
    private TextView sender, receiver, emailFormat;
    private Button sendEmailButton;
    //private String senderUrl = "";
    private String modelTitle, receiverUrl, detailSiteUrl;

    private ResultModel model;
    private DBHelper dbHelper;
    private SendEmailTask sendEmailTask;

    public SendEmailDialog(@NonNull Context context, Activity activity, ResultModel model) {
        super(context);
        this.activity = activity;
        this.context = context;
        this.model = model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_send_email_dialog);
        sender = findViewById(R.id.sender);
        receiver = findViewById(R.id.receiver);
        emailFormat = findViewById(R.id.dialog_email);
        sendEmailButton = findViewById(R.id.dialog_send_email_button);
        dbHelper = new DBHelper(
                context,
                "RIGHT_TABLE",
                null, 1
        );
        dbHelper.testDB();

        if(model.getSite().contains("pornhub")){
            receiver.setText("받는 사람: www.pornhub.com");
            receiverUrl = "https://www.pornhub.com";
            emailFormat.setText("Request removal URL: "+model.getSite()+"\n\n"+context.getResources().getString(R.string.send_email_format_english));
        } else {
            receiver.setText("받는 사람: www.yazaral.com");
            receiverUrl = "https://www.yazaral.com";
            emailFormat.setText("삭제 요청 URL: "+model.getSite()+"\n\n"+context.getResources().getString(R.string.send_email_format));
        }


        modelTitle = model.getTitle(); detailSiteUrl = model.getSite();

        sendEmailTask = new SendEmailTask(context, modelTitle, detailSiteUrl,receiverUrl);
        //sendEmailTask = new SendEmailTask(context, modelTitle, "dev@spresto.net", "dev@spresto.net");

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. SQLite Insert
                if(dbHelper == null){
                    dbHelper = new DBHelper(context, "RIGHT_TABLE",null,1);
                }
                SQLiteModel sqLiteModel = new SQLiteModel(
                        model.getProb(),
                        model.getTitle(),
                        model.getSite(),
                        model.getRanking()
                );
                dbHelper.addModel(sqLiteModel);

                // 2. Server connect
                sendEmailTask.execute();
            }
        });

        sendEmailTask.setOnCompleteListener(new SendEmailTask.onCompleteListener() {
            @Override
            public void onComplete(String result) {
                if(result.equals("200")){
                    Toast.makeText(context, "이메일 전송 완료",Toast.LENGTH_SHORT).show();
                    SendEmailDialog.this.dismiss();
                } else {
                    Toast.makeText(context, "이메일 전송 실패, 재시도 부탁드립니다",Toast.LENGTH_SHORT).show();
                    SendEmailDialog.this.dismiss();
                }
            }
        });
    }


}
