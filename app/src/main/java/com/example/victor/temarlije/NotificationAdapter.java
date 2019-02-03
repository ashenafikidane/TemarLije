package com.example.victor.temarlije;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<Messages> messagesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, textMessage, textTime;
        public ImageView photoImageView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            textMessage = (TextView) view.findViewById(R.id.tv_message);
            photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
            textTime = (TextView) view.findViewById(R.id.tv_time);
        }
    }


    public NotificationAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Messages message = messagesList.get(position);

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            holder.textMessage.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
        } else {
            holder.textMessage.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.textMessage.setText(message.getTextMessage());
        }

        holder.name.setText(message.getMessageSender());
        holder.textTime.setText(message.getTextTime().toString());
    }
    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}