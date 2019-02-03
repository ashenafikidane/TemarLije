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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class GroupDetail extends Fragment {

    private List<Students> studentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentsAdapter mAdapter;
    private android.support.v4.app.Fragment fragment2;
    private String groupID;

    private TextView title,members;

    public static GroupDetail newInstance(String groupID, android.support.v4.app.Fragment fragment2 ) {
        GroupDetail fragment = new GroupDetail();
        fragment.groupID = groupID;
        fragment.fragment2 = fragment2;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        setHasOptionsMenu(true);

        final DatabaseHandler db = new DatabaseHandler(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_group_students);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.group_detail_toolbar);
        title = view.findViewById(R.id.tv_title2);
        members = view.findViewById(R.id.tv_members2);
        ImageButton back = view.findViewById(R.id.back);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        title.setText(db.getGroup(groupID).getGroupName());
        setMembers();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    getFragmentManager().beginTransaction()
                            .show(fragment2)
                            .commit();
                }
                else {
                    getActivity().finish();
                }
            }
        });

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
                        .replace(R.id.frameLayoutgroup2, fragment)
                        .addToBackStack("show_fragment")
                        .commit(); // save the changes

            }

            @Override
            public void onLongClick(View view, final int position) {
                final DatabaseHandler db = new DatabaseHandler(getContext());
                final Students student = studentList.get(position);
                final String groupName = db.getGroup(groupID).getGroupName();
                final String[] items = {"Remove from group"};

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos];
                        if(selectedText == "Remove from group"){
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                            dialogBuilder.setTitle("Are you sure you want to remove "+ student.getStudentName() + " from " + groupName);
                            dialogBuilder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteGroupStudent(""+student.getID());
                                    Toast.makeText(getContext(),""+student.getStudentName()+" removed", Toast.LENGTH_SHORT).show();
                                    studentList.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    setMembers();
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

        GroupData();

        return view;
    }

    private void setMembers(){
        DatabaseHandler db = new DatabaseHandler(getContext());
        if(db.getGroupStudents(groupID).size()==1){
            members.setText("1 member");
        }
        else {
            members.setText(""+db.getGroupStudents(groupID).size()+" members");
        }
    }

    private void GroupData() {

        DatabaseHandler db = new DatabaseHandler(getContext());

        ArrayList<Integer> studentIDs = db.getGroupStudents(groupID);

        //Add item in listview
        for (int i = 0; i < studentIDs.size(); i++) {

            studentList.add(db.getStudent(""+studentIDs.get(i)));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_detail_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        studentList.clear();
        GroupData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DatabaseHandler db = new DatabaseHandler(getContext());
        switch (item.getItemId()) {
            case R.id.delete_group:
                final String groupName = db.getGroup(groupID).getGroupName();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Are you sure you want to delete "+groupName);
                dialogBuilder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteGroup(groupID);
                        Toast.makeText(getContext(),""+groupName+" deleted", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
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
                return true;

            case R.id.add_member:
                android.support.v4.app.Fragment fragment = SelectStudents.newInstance(groupID);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutgroup2, fragment)
                        .addToBackStack("my fragment")
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
