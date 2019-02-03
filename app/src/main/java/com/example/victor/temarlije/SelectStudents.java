package com.example.victor.temarlije;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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
import java.util.List;

public class SelectStudents extends Fragment{

        private List<Students> studentList = new ArrayList<>();
        private RecyclerView recyclerView;
        private GroupAddAdapter mAdapter;

        private TextView addStudents;

        private String groupID;

    public static SelectStudents newInstance(String groupID) {
        SelectStudents fragment = new  SelectStudents();
        fragment.groupID = groupID;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_students, container, false);
        setHasOptionsMenu(true);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.select_students_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Add members");

        recyclerView = (RecyclerView) view.findViewById(R.id.rec_addGroup);
        addStudents = (TextView) view.findViewById(R.id.tv_addStudents);


        mAdapter = new GroupAddAdapter(studentList);
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
                Students student = studentList.get(position);

            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        StudentData();
        return view;
    }

    private void StudentData() {

        DatabaseHandler db = new DatabaseHandler(getContext());

        ArrayList<Students> students = db.getAllStudents();

        if (groupID == "all"){
            //Add item in listview
            for (int i = 0; i < students.size(); i++) {
                studentList.add(students.get(i));
            }
        }
        else {
            for (int i = 0; i < students.size(); i++) {
                if (db.getOtherStudents(""+students.get(i).getID(),groupID).size() > 0)
                    continue;
                else
                    studentList.add(students.get(i));
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        studentList.clear();
        StudentData();
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
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                else {
                    getActivity().finish();
                }
                return true;
            case R.id.check:
                ArrayList<Students> selectedStudents = mAdapter.getSelectedItems();

                if (groupID == "all"){
                    if (selectedStudents.size() == 0 ){
                        Toast.makeText(getContext(),"Please select Students", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        android.support.v4.app.Fragment fragment = GroupName.newInstance(selectedStudents);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutslectedstudents, fragment)
                                .addToBackStack("my fragment")
                                .commit();
                    }
                }
                else {
                    DatabaseHandler db = new DatabaseHandler(getContext());
                    for (int i=0; i < selectedStudents.size(); i++){
                        db.addStudentGroups(selectedStudents.get(i).getID(),Long.parseLong(groupID));
                    }
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
