package com.example.victor.temarlije;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StudentList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        android.support.v4.app.Fragment fragment = ShowStudents.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutstudent, fragment)
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    android.support.v4.app.Fragment fragment = ShowStudents.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayoutstudent, fragment)
                            .commit();
                }
                else {
                    finish();
                }
                return true;
            case R.id.add:
                android.support.v4.app.Fragment fragment = AddStudent.newInstance("Add");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutstudent, fragment)
                        .addToBackStack("my_fragment")
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            android.support.v4.app.Fragment fragment = ShowStudents.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutstudent, fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}
