package com.example.victor.temarlije;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupName extends Fragment {

    private ArrayList<Students> selectedStudents;
    private RecyclerView recyclerView;
    private StudentsAdapter mAdapter;

    private EditText groupName;
    private TextView members;

    public static GroupName newInstance(ArrayList<Students> selectedStudents) {
        GroupName fragment = new GroupName();
        fragment.selectedStudents = selectedStudents;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_name, container, false);
        setHasOptionsMenu(true);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.group_name_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Add members");

        recyclerView = (RecyclerView) view.findViewById(R.id.rec_showstudents);
        members = (TextView) view.findViewById(R.id.number_of_members);

        mAdapter = new StudentsAdapter(selectedStudents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        // set the adapter
        recyclerView.setAdapter(mAdapter);
        recyclerView.setBackgroundColor(Color.GRAY);

        if(selectedStudents.size()==1){
            members.setText("1 member");
        }
        else {
            members.setText(""+selectedStudents.size() + "members");
        }

        groupName = (EditText) view.findViewById(R.id.et_groupName);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.student_add_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.check:

                if (groupName.getText().toString().isEmpty()){
                    groupName.setHintTextColor(Color.RED);
                    Toast.makeText(getContext(),"Please Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    final DatabaseHandler db = new DatabaseHandler(getContext());
                    Groups group = new Groups(groupName.getText().toString());
                    long newGroupID = db.addGroup(group);
                    Log.d("Group ID",""+newGroupID);
                    for (int i=0; i < selectedStudents.size(); i++){
                        db.addStudentGroups(selectedStudents.get(i).getID(),newGroupID);
                    }
                    Toast.makeText(getContext(),"Group " + group.getGroupName() + " Created" , Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                    }
                    Intent intent = new Intent(getContext(),ShowGroup.class);
                    intent.putExtra("groupID", ""+newGroupID);
                    startActivity(intent);
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
