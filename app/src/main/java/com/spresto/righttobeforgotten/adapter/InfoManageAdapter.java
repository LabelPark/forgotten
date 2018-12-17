package com.spresto.righttobeforgotten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.model.SQLiteModel;

import java.util.ArrayList;

/**
 * Created by spresto on 2018-09-19.
 */

public class InfoManageAdapter extends BaseAdapter {
    public interface onReSendEmailListener{
        void onResendEmail(int position);
    }

    private static final String TAG = InfoManageAdapter.class.getSimpleName();
    private Context context;
    private int layoutResource;
    private LayoutInflater layoutInflater;
    private ArrayList<SQLiteModel> data;

    private onReSendEmailListener onReSendEmailListener;
    static class ViewHolder{
        ImageView reSendEmail;
        TextView  info_percent, info_url, info_title, info_email;
    }

    public InfoManageAdapter(Context context, int layoutResource, ArrayList<SQLiteModel> data) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.data = data;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnResendEmail(onReSendEmailListener onResendEmail){
        this.onReSendEmailListener = onResendEmail;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.info_percent = convertView.findViewById(R.id.info_mathpercent);
            holder.info_url = convertView.findViewById(R.id.info_url);
            holder.info_title = convertView.findViewById(R.id.info_title);
            holder.info_email = convertView.findViewById(R.id.info_email);
            holder.reSendEmail = convertView.findViewById(R.id.resend);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.info_percent.setText(String.format("%.0f",Double.parseDouble(data.get(position).getProb()))+"%");
        holder.info_url.setText(data.get(position).getSite());
        holder.info_title.setText(data.get(position).getTitle());
        holder.info_email.setText(data.get(position).getIsDelete());
        holder.reSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onReSendEmailListener != null){
                    onReSendEmailListener.onResendEmail(position);
                }
            }
        });
        return convertView;
    }
}
