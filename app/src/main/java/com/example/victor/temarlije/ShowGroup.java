package com.example.victor.temarlije;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowGroup extends AppCompatActivity {

    private String groupID;
    private android.support.v4.app.Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);

        final DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        groupID = "General";

        TextView title = findViewById(R.id.tv_title);
        TextView members = findViewById(R.id.tv_members);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            groupID = extras.getString("groupID");
        //setting Toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        title.setText(db.getGroup(groupID).getGroupName());
        if(db.getGroupStudents(groupID).size()==1){
            members.setText("1 member");
        }
        else {
            members.setText(""+db.getGroupStudents(groupID).size()+" members");
        }

        fragment = Notification.newInstance(groupID);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutgroup, fragment)
                .commit();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.Fragment fragment2 = GroupDetail.newInstance(groupID,fragment);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutgroup2, fragment2)
                        .addToBackStack("my_fragment3")
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .hide(fragment)
                        .commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                }
                else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            finish();
        }
    }
}
