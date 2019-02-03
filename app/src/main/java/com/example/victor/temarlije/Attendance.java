package com.example.victor.temarlije;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Attendance extends AppCompatActivity {

    private List<Students> studentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AttendanceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        //seeting ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<String> absentStudents = new ArrayList<>();
                ArrayList<Students> selectedStudents = mAdapter.getSelectedItems();
                for (int i= 0; i < selectedStudents.size(); i++){
                    absentStudents.add(selectedStudents.get(i).getStudentName());
                }

                final CharSequence[] items = absentStudents.toArray(new String[selectedStudents.size()]);

                //show up selected item in dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Attendance.this);
                dialogBuilder.setTitle("selected absent students list");
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos].toString();
                        Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"confirmed", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setNegativeButton("cancell", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.rec_attendance);

        mAdapter = new AttendanceAdapter(studentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        // set the adapter
        recyclerView.setAdapter(mAdapter);
        // row click listener
        recyclerView.addOnItemTouchListener(new StudentTouchListener(getApplicationContext(), recyclerView, new StudentTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Students student = studentList.get(position);

            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        StudentData();
    }

    private void StudentData() {

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        ArrayList<Students> students = db.getAllStudents();
        //Add item in listview
        for (int i=0; i < students.size();i++ ){
            studentList.add(students.get(i));
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeAsUp:
                onSupportNavigateUp();
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendance_menu, menu);
        return true;
    }
}
