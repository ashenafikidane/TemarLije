package com.example.victor.temarlije;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int db_version = 7;
    private static final String db_name = "TemarLije";

    //database for Students
    private static final String table = "students";
    private static final String key_ID = "_studentID";
    private static final String key_student_name = "_student_name";
    private static final String key_grade = "_grade";
    private static final String key_section = "_section";
    private static final String key_parent = "_parent";

    //database for Messages
    private static final String tablem = "messages";
    private static final String key_mID = "_messageID";
    private static final String key_sender_name = "_sender_name";
    private static final String key_text = "_text";
    private static final String key_time= "_time";
    private static final String key_photoUrl = "_photoUrl";
    private static final String key_group = "_groupID";

    //database for Groups
    private static final String tableg = "groups";
    private static final String key_gID = "_groupID";
    private static final String key_groupName = "_group_name";

    //database for StudentsGruops
    private static final String tables = "studentGroups";
    private static final String key_sgID = "_sgID";
    private static final String key_studentID = "_studentID";
    private static final String key_groupID= "_groupID";


    public DatabaseHandler(Context context) {
        super(context, db_name, null, db_version);
    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //for students
        String create_table = "create table " + table + "(" + key_ID + " integer primary key not null, " + key_student_name + " text, " +
                key_grade + " text, " + key_section+ " text, " + key_parent + " text " + ")";
        //for Groups
        String create_tableg = "create table " + tableg + "(" + key_gID + " integer primary key not null, " + key_groupName + " text " + ")";
        //for Messages
        String create_tablem = "create table " + tablem + "(" + key_mID + " integer primary key, " + key_sender_name + " text, " +
                key_text + " text, " + key_time+ " text, " + key_photoUrl + " text, " + key_group + " text, constraint fk_group " +
                " foreign key (_groupID) references groups(_groupID) on delete cascade " + ")";
        //for studentsGroups
        String create_tables = "create table " + tables + "(" + key_sgID + " integer, " + key_studentID + " integer, " + key_groupID + " integer, primary key " +
                "(_studentID,_groupID), constraint fk_student foreign key(_studentID) references students(_studentID) on delete cascade, constraint " +
                "fk_group foreign key(_groupID) references groups(_groupID) on delete cascade" + ")";

        String add_general_group = "insert into "+tableg+ " values("+1+" ,'General')";

        db.execSQL(create_table);
        db.execSQL(create_tablem);
        db.execSQL(create_tableg);
        db.execSQL(create_tables);
        db.execSQL(add_general_group);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()){
            //enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    //updating database

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newv) {
        //drop older table if existed
        db.execSQL("drop table if exists " + table);
        db.execSQL("drop table if exists " + tablem);
        db.execSQL("drop table if exists " + tableg);
        db.execSQL("drop table if exists " + tables);

        //create table again
        onCreate(db);

    }

    //code to add new Student
    void addStudent(Students student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_student_name, student.getStudentName());
        values.put(key_grade, student.getGrade());
        values.put(key_section, student.getSection());
        values.put(key_parent, student.getParent());

        //inserting Row
        db.insert(table, null, values);

        db.close();
    }

    //code to add new Message
    void addMessage(Messages message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_sender_name, message.getMessageSender());
        values.put(key_text, message.getTextMessage());
        values.put(key_time, message.getTextTime());
        values.put(key_photoUrl, message.getPhotoUrl());
        values.put(key_group, message.getGroup());

        //inserting Row
        db.insert(tablem, null, values);
        db.close();
    }

    //code to add new group
    long addGroup(Groups group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_groupName, group.getGroupName());

        //inserting Row
        long rowid = db.insert(tableg, null, values);
        db.close();
        return rowid;
    }

    int lastInsert(){
        int rowid ;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select last_insert_rowid()";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("cursor output",""+cursor);
        cursor.close();
        db.close();
        return 0;
    }

    void addStudentGroups(int studentID, long groupID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_studentID, studentID);
        values.put(key_groupID, groupID);

        //inserting Row
        db.insert(tables, null, values);
        db.close();
    }

    //code to get single student
    public Students getStudent(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from students where _studentID= '" + id.trim() + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Students student = new Students();
        if (cursor.moveToFirst()) {
            do {

                student = new Students();
                student.setID(Integer.parseInt(cursor.getString(0)));
                student.setStudentName(cursor.getString(1));
                student.setGrade(cursor.getString(2));
                student.setSection(cursor.getString(3));
                student.setParent(cursor.getString(4));

                cursor.moveToNext();
                //adding contact to list

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return student;
    }

    //code to get single Message
    public Messages getMessage(String textID) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from messages where _messageID= '" + textID.trim() + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Messages message = new Messages();
        if (cursor.moveToFirst()) {
            do {

                message = new Messages();
                message.setID(Integer.parseInt(cursor.getString(0)));
                message.setMessageSender(cursor.getString(1));
                message.setTextMessage(cursor.getString(2));
                message.setTextTime(cursor.getString(3));
                message.setPhotoUrl(cursor.getString(4));
                message.setGroup(cursor.getString(5));

                cursor.moveToNext();
                //adding contact to list

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return message;
    }

    //code to get single Group
    public Groups getGroup(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from groups where _groupID= '" + id.trim() + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        Groups group = new Groups();
        if (cursor.moveToFirst()) {
            do {

                group = new Groups();
                group.setID(Integer.parseInt(cursor.getString(0)));
                group.setGroupName(cursor.getString(1));

                cursor.moveToNext();
                //adding contact to list

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return group;
    }

    //code to get students of a group
    public ArrayList<Integer> getGroupStudents(String groupID) {
        ArrayList<Integer> studentList = new ArrayList<Integer>();
        String selectQuery = "select _studentID from studentGroups where _groupID= '" + groupID.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                studentList.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }

    //code to get students of a group
    public ArrayList<Integer> getOtherStudents(String studentID,String groupID) {
        ArrayList<Integer> studentList = new ArrayList<Integer>();
        String selectQuery = "select _studentID from studentGroups where _studentID= '" + studentID.trim() +"' and _groupID= '" + groupID.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                studentList.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }


    //code to get groups of a student
    public ArrayList<Integer> getStudentGroups(String studentID) {
        ArrayList<Integer> groupList = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select _groupID from studentGroups where _studentID= '" + studentID.trim() + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                groupList.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return groupList;
    }

    //code to get all students in a ArrayList
    public  ArrayList<Students> getAllStudents() {

        ArrayList<Students> studentList = new ArrayList<Students>();
        String selectQuery = "select * from " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Students student = new Students();
                student.setID(Integer.parseInt(cursor.getString(0)));
                student.setStudentName(cursor.getString(1));
                student.setGrade(cursor.getString(2));
                student.setSection(cursor.getString(3));
                student.setParent(cursor.getString(4));

                //adding student to list
                studentList.add(student);


            } while (cursor.moveToNext());
        }
        return studentList;
    }

    //code to get all messages in a ArrayList
    public  ArrayList<Messages> getAllMessages(String groupID) {

        ArrayList<Messages> messageList = new ArrayList<Messages>();
        String selectQuery = "select * from messages where _groupID= '" + groupID.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Messages message = new Messages();
                message.setID(Integer.parseInt(cursor.getString(0)));
                message.setMessageSender(cursor.getString(1));
                message.setTextMessage(cursor.getString(2));
                message.setTextTime(cursor.getString(3));
                message.setPhotoUrl(cursor.getString(4));
                message.setGroup(cursor.getString(5));

                //adding message to list
                messageList.add(message);


            } while (cursor.moveToNext());
        }
        return messageList;
    }

    //code to get all groups in a ArrayList
    public  ArrayList<String> getAllStudentGroups() {

        ArrayList<String> groupList = new ArrayList<String>();
        String selectQuery = "select _studentID from " + tables;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                //adding group to list
                groupList.add(cursor.getString(0));


            } while (cursor.moveToNext());
        }
        return groupList;
    }

    //code to get all groups in a ArrayList
    public  ArrayList<Groups> getAllGroups() {

        ArrayList<Groups> groupList = new ArrayList<Groups>();
        String selectQuery = "select * from " + tableg;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Groups group = new Groups();
                group.setID(Integer.parseInt(cursor.getString(0)));
                group.setGroupName(cursor.getString(1));

                //adding group to list
                groupList.add(group);


            } while (cursor.moveToNext());
        }
        return groupList;
    }

    //code to update single student
    public int updateStudent(Students student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_student_name, student.getStudentName());
        values.put(key_grade, student.getGrade());
        values.put(key_section, student.getSection());
        values.put(key_parent, student.getParent());

        //update row
        return db.update(table, values, key_ID + "=?", new String[]{""+student.getID()});
    }

    //code to update single message
    public int updateMessage(Messages message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_sender_name, message.getMessageSender());
        values.put(key_text, message.getTextMessage());
        values.put(key_time, message.getTextTime());
        values.put(key_photoUrl, message.getPhotoUrl());
        values.put(key_group, message.getGroup());

        //update row
        return db.update(tablem, values, key_mID + "=?", new String[]{""+message.getID()});

    }

    //code to update single Group
    public int updateGroup(Groups group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(key_groupName, group.getGroupName());
        //update row
        return db.update(tableg, values, key_gID + "=?", new String[]{""+group.getID()});

    }

    //deleting single student
    public void deleteStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, key_ID + "=?", new String[]{id});
        db.close();
    }

    //deleting single messsage
    public void deleteMessage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tablem,key_mID + "=?", new String[]{id});
        db.close();
    }

    //deleting single group
    public void deleteGroup(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableg,key_gID + "=?", new String[]{id});
        db.close();
    }

    //deleting student from group
    public void deleteGroupStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tables,key_studentID + "=?", new String[]{id});
        db.close();
    }

    //getting students count
    public int getStudentCount() {

        String countQuery = "select * from " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        return cursor.getCount();
    }

    //getting messages count
    public int getMessageCount() {

        String countQuery = "select * from " + tablem;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    //getting groups count
    public int getGroupsCount() {

        String countQuery = "select * from " + tableg;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
        return cursor.getCount();
    }

    public ArrayList<String> getAllStudentName() {
        ArrayList<String> studentNames = new ArrayList<String>();
        String selectQuery = "select _student_name from " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                studentNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return studentNames;
    }

    public void insertData(Context context) {

        try {
            BufferedReader student = new BufferedReader(new InputStreamReader(context.getAssets().open("student.txt")));

            BufferedReader grade = new BufferedReader(new InputStreamReader(context.getAssets().open("grade.txt")));
            BufferedReader section = new BufferedReader(new InputStreamReader(context.getAssets().open("section.txt")));
            BufferedReader parent = new BufferedReader(new InputStreamReader(context.getAssets().open("parent.txt")));
            String stud;
            while ((stud = student.readLine()) != null) {

                String gra = grade.readLine();
                String sec = section.readLine();
                String par = parent.readLine();

                addStudent(new Students(stud, gra, sec, par));
            }
            student.close();
            grade.close();
            section.close();
            parent.close();


        } catch (IOException e) {
            System.out.println("file not found here");
        }
    }

    public ArrayList<String> getParent(String input) {
        ArrayList<String> result = new ArrayList<String>();

        String selectQuery = "select distinct _parent from " + table + " where _student_name" + "='" + input + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return result;

    }

    //code to delate all from a given table
    public void deleteAll(String tablename) {

        String delete = "delete from " + tablename;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delete);
    }

}

