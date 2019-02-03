package com.example.victor.temarlije;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private List<Students> studentsList;
    private ArrayList<Students> selectedStudents = new ArrayList<Students>();
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();


    private Context context;
    public int mSelectedItem = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ID;
        public CircleImageView profile_img;
        public CheckBox selection;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentname);
            ID = (TextView) view.findViewById(R.id.studentID);
            profile_img = (CircleImageView) view.findViewById(R.id.profile_image);
            selection = (CheckBox) view.findViewById(R.id.checkbox);

            selection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    if (isChecked){
                        selectedStudents.add(studentsList.get(adapterPosition));
                    }
                    else {
                        if (selectedStudents.size()>0){
                            selectedStudents.remove(studentsList.get(adapterPosition));
                        }
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    if (!itemStateArray.get(adapterPosition,false)){
                        selection.setChecked(true);
                        itemStateArray.put(adapterPosition,true);
                    }
                    else {
                        selection.setChecked(false);
                        itemStateArray.put(adapterPosition,false);
                    }
                }
            });
        }
    }

    public AttendanceAdapter(List<Students> studentsList) {
        this.studentsList = studentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_attendance_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Students student = studentsList.get(position);
        holder.name.setText(student.getStudentName());
        holder.ID.setText("TL/"+student.getID()+"/2018");

        if (!itemStateArray.get(position,false)){
            holder.selection.setChecked(false);
        }
        else {
            holder.selection.setChecked(true);
        }
    }
    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public ArrayList<Students> getSelectedItems(){
        return selectedStudents;
    }
}