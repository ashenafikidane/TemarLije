package com.example.victor.temarlije;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class GroupList extends Fragment {

    private List<Groups> groupsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GroupAdapter mAdapter;

    public static GroupList newInstance() {
        GroupList fragment = new GroupList();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_groups);

        mAdapter = new GroupAdapter(groupsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // set the adapter
        recyclerView.setAdapter(mAdapter);
        // row click listener
        recyclerView.addOnItemTouchListener(new StudentTouchListener(getContext(), recyclerView, new StudentTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Groups group = groupsList.get(position);

                Intent intent = new Intent(getContext(),ShowGroup.class);
                intent.putExtra("groupID", ""+group.getID());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        GroupData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupsList.clear();
        GroupData();
    }

    private void GroupData() {

        DatabaseHandler db = new DatabaseHandler(getContext());

        ArrayList<Groups> groups = db.getAllGroups();
        //Add item in listview
        for (int i = 1; i < groups.size(); i++) {
            groupsList.add(groups.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}