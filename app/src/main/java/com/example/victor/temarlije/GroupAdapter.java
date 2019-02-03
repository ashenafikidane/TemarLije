package com.example.victor.temarlije;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private List<Groups> groupsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView group_time, name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_groupName);
            group_time = (TextView) view.findViewById(R.id.tv_groupTime);
        }
    }


    public GroupAdapter(List<Groups> groupsList) {
        this.groupsList = groupsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Groups group = groupsList.get(position);
        holder.name.setText(group.getGroupName());
        holder.group_time.setText("time");
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }
}