package com.spresto.righttobeforgotten.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spresto.righttobeforgotten.R;
import com.spresto.righttobeforgotten.model.ResultModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by spresto on 2018-09-30.
 */

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.ViewHolder> {
    private static final String TAG = CompareAdapter.class.getSimpleName();

    private ViewHolder _holder;

    public interface onClickListener{
        interface onSendEmailListener {
            void onSendEmail(int position);
        }
        interface onAudioAnalysisListener {
            void onAnalysis(int position);
        }
        interface onTensorFlowListener {
            void onTensor(int position);
        }
    }

    public void setOnSendEmailListener(onClickListener.onSendEmailListener onSendEmailListener) {
        this.onSendEmailListener = onSendEmailListener;
    }

    public void setOnAudioAnalysisListener(onClickListener.onAudioAnalysisListener onAudioAnalysisListener) {
        this.onAudioAnalysisListener = onAudioAnalysisListener;
    }

    public void setOnTensorFlowListener(onClickListener.onTensorFlowListener onTensorFlowListener) {
        this.onTensorFlowListener = onTensorFlowListener;
    }

    private Context context;
    private ArrayList<ResultModel> data = null;
    private HashMap<Integer, String> isSendMap = new HashMap<>();
    private onClickListener.onSendEmailListener onSendEmailListener;
    private onClickListener.onAudioAnalysisListener onAudioAnalysisListener;
    private onClickListener.onTensorFlowListener onTensorFlowListener;
    private Button sendButton;

    public CompareAdapter(Context context, ArrayList<ResultModel> data, Button sendButton) {
        this.context = context;
        this.data = data;
        this.sendButton = sendButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.compare_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        _holder = holder;
        String percent = String.format("%.0f", Double.parseDouble(data.get(position).getProb()));
        Double percent_double = Double.parseDouble(percent);
        if (percent_double >= 0.0 && percent_double < 30.0) {
            holder.circlePercent.setBackground(context.getResources().getDrawable(R.drawable.dot_circular_yellow));
            holder.circlePercent.setText(percent + "%");
        } else if (percent_double >= 30.0 && percent_double < 50.0) {
            holder.circlePercent.setBackground(context.getResources().getDrawable(R.drawable.dot_circular_green));
            holder.circlePercent.setText(percent + "%");
        } else {
            holder.circlePercent.setBackground(context.getResources().getDrawable(R.drawable.dot_circular_red));
            holder.circlePercent.setText(percent + "%");
        }

        holder.site.setImageBitmap(data.get(position).getPornBitmap());
        holder.title.setText("영상 제목: " + data.get(position).getTitle());
        holder.url.setText("사이트 URL: " + data.get(position).getSite());

        holder.sendEmailButton.setOnClickListener((View v) -> {
            if (onSendEmailListener != null) {
                onSendEmailListener.onSendEmail(position);
            }
        });

        if (isSendMap.containsKey(position)) {
            Log.e(TAG, "VISIBLE");
            holder.mConfirmLayout.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "GONE");
            holder.mConfirmLayout.setVisibility(View.GONE);
        }

        holder.audioAnalysisButton.setOnClickListener((View v) -> {
            if (onAudioAnalysisListener != null) {
                onAudioAnalysisListener.onAnalysis(position);
            }
        });

        holder.tensorButton.setOnClickListener((View v) -> {
            if (onTensorFlowListener != null) {
                onTensorFlowListener.onTensor(position);
            }
        });


        sendButton.setOnClickListener((View v) -> {

            isSendMap.put(position, data.get(position).getProb());

            _holder.mConfirmLayout.setVisibility(View.VISIBLE);

            if (onSendEmailListener != null) {
                onSendEmailListener.onSendEmail(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.site_image)
        ImageView site;
        @BindView(R.id.com_title)
        TextView title;
        @BindView(R.id.com_url)
        TextView url;
        @BindView(R.id.circle_percent)
        TextView circlePercent;
        @BindView(R.id.confirmLayout)
        RelativeLayout mConfirmLayout;
        @BindView(R.id.sendEmail)
        Button sendEmailButton;
        @BindView(R.id.audioAnalysisButton)
        Button audioAnalysisButton;
        @BindView(R.id.tensorButton)
        Button tensorButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
