package com.example.victor.temarlije;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {

    private List<Students> studentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ID, section;
        public CircleImageView profile_img;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentname);
            ID = (TextView) view.findViewById(R.id.studentID);
            section = (TextView) view.findViewById(R.id.section);
            profile_img = (CircleImageView) view.findViewById(R.id.profile_image);
        }
    }


    public StudentsAdapter(List<Students> studentsList) {
        this.studentsList = studentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Students student = studentsList.get(position);
        holder.name.setText(student.getStudentName());
        holder.ID.setText("TL/"+student.getID()+"/2018");
        holder.section.setText(student.getGrade() + student.getSection());
        //holder.profile_img.
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }
}