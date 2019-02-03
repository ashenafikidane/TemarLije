package com.example.victor.temarlije;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStudent extends Fragment {

    private DatabaseHandler db = new DatabaseHandler(getContext());
    private EditText studentName, grade, section, parentName;
    private String cmd, mstudentName, mgrade, msection, mparentName = "";
    private CircleImageView profile_img;

    private static final int RC_PHOTO_PICKER =  1;

    public static AddStudent newInstance(String cmd) {
        AddStudent fragment = new AddStudent();
        fragment.cmd = cmd;
        return fragment;
    }
    public static AddStudent newInstance(String cmd, Students student) {
        AddStudent fragment = new AddStudent();
        fragment.cmd = cmd;
        fragment.mstudentName = student.getStudentName();
        fragment.mgrade = student.getGrade();
        fragment.msection = student.getSection();
        fragment.mparentName = student.getParent();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);

        setHasOptionsMenu(true);

        studentName = (EditText) view.findViewById(R.id.et_full_name);
        parentName = (EditText) view.findViewById(R.id.et_parent);
        grade = (EditText) view.findViewById(R.id.et_grade);
        section = (EditText) view.findViewById(R.id.et_section);
        profile_img = (CircleImageView) view.findViewById(R.id.add_profile_img);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.add_student_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        actionBar.setTitle(this.cmd + " Student");
        actionBar.setHomeButtonEnabled(true);

        if (this.cmd == "Edit"){
            studentName.setText(this.mstudentName);
            grade.setText(this.mgrade);
            section.setText(this.msection);
            parentName.setText(this.mparentName);
        }

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PHOTO_PICKER);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            String image = "bbh"+selectedImageUri;

            profile_img.setImageURI(Uri.parse(image));
            Toast.makeText(getContext(), "Image" + selectedImageUri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.student_add_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.check:
                if (studentName.getText().toString().isEmpty() || grade.getText().toString().isEmpty()
                        || section.getText().toString().isEmpty() || parentName.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please fill all the required information", Toast.LENGTH_SHORT).show();
                }
                else {
                    final DatabaseHandler db = new DatabaseHandler(getContext());
                    if(this.cmd == "Add"){
                        db.addStudent(new Students(studentName.getText().toString(), grade.getText().toString(),
                                section.getText().toString(), parentName.getText().toString()));
                    }
                    else if(this.cmd == "Edit"){
                        db.updateStudent(new Students(studentName.getText().toString(), grade.getText().toString(),
                                section.getText().toString(), parentName.getText().toString()));
                    }

                    Toast.makeText(getContext(), "Student "+this.cmd+"ed successfully", Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();
                        android.support.v4.app.Fragment fragment = ShowStudents.newInstance();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutstudent, fragment)
                                .commit();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}