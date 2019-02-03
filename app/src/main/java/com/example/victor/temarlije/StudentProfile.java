package com.example.victor.temarlije;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class StudentProfile extends Fragment {

    private TextView fullName, ID, gradeSection, studentParent;
    private String mfullName, mID, mgradeSection, mstudentParent;

    public static StudentProfile newInstance(Students student) {
        StudentProfile fragment = new StudentProfile();
        fragment.mfullName = student.getStudentName();
        fragment.mID = "TL/" + student.getID() + "/2018";
        fragment.mgradeSection = student.getGrade() + student.getSection();
        fragment.mstudentParent = student.getParent();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.student_detail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //seeting ActionBar
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        TextView title = view.findViewById(R.id.tv_name);
        ImageButton back = view.findViewById(R.id.back);
        title.setText(this.mfullName);

        fullName = (TextView) view.findViewById(R.id.tv_full_name);
        ID = (TextView) view.findViewById(R.id.tv_ID);
        gradeSection = (TextView) view.findViewById(R.id.tv_grade_section);
        studentParent = (TextView) view.findViewById(R.id.tv_parent);

        fullName.setText("Name: " + this.mfullName);
        ID.setText("ID: " + this.mID);
        gradeSection.setText("Grade and Section: " + this.mgradeSection);
        studentParent.setText("Parent: " + this.mstudentParent);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                else {
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}