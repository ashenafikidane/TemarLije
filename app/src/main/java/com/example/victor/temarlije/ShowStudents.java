package com.example.victor.temarlije;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowStudents extends Fragment {

    private List<Students> studentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentsAdapter mAdapter;

    public static ShowStudents newInstance() {
        ShowStudents fragment = new ShowStudents();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_students, container, false);

        setHasOptionsMenu(true);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.student_list_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.rec_showstudents);

        mAdapter = new StudentsAdapter(studentList);
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
                android.support.v4.app.Fragment fragment = StudentProfile.newInstance(student);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutstudent, fragment)
                        .addToBackStack("show_fragment")
                        .commit(); // save the changes
            }

            @Override
            public void onLongClick(View view, final int position) {

                final DatabaseHandler db = new DatabaseHandler(getContext());
                final Students student = studentList.get(position);
                final String[] items = {"edit","delete"};

                //show up selected item in dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos];
                        if(selectedText == "edit"){
                            android.support.v4.app.Fragment fragment = AddStudent.newInstance("Edit", student);
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameLayoutstudent, fragment)
                                    .addToBackStack("show_fragment")
                                    .commit(); // save the changes

                        }
                        else if (selectedText == "delete"){

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                            dialogBuilder.setTitle("Are you sure you want to delete "+ student.getStudentName());
                            dialogBuilder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteStudent(""+student.getID());
                                    Toast.makeText(getContext(), student.getStudentName() + " deleted", Toast.LENGTH_SHORT).show();
                                    studentList.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                            dialogBuilder.setNegativeButton("cancell", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog alertDialogObject = dialogBuilder.create();
                            alertDialogObject.show();
                        }
                    }
                });

                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();
            }
        }));

        StudentData();
        return view;
    }

    private void StudentData() {

        DatabaseHandler db = new DatabaseHandler(getContext());

        ArrayList<Students> students = db.getAllStudents();
        //Add item in listview
        for (int i=0; i < students.size();i++ ){
            studentList.add(students.get(i));
        }

        mAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.student_menu, menu);
    }
}