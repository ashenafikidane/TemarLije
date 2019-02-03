package com.example.victor.temarlije;

public class Students {
    private int ID;
    private String studentName;
    private String grade;
    private String section;
    private String parent;

    public Students(){}
    public Students(int ID,String studentName,String grade,String section,String parent){
        this.ID=ID;
        this.studentName=studentName;
        this.grade=grade;
        this.section=section;
        this.parent=parent;

    }
    public Students(String studentName,String grade,String section,String parent){
        this.studentName=studentName;
        this.grade=grade;
        this.section=section;
        this.parent=parent;

    }
    public int getID(){ return this.ID; }
    public void setID(int id){
        this.ID=id;
    }
    public String getStudentName(){
        return this.studentName;
    }
    public void setStudentName(String studentName){
        this.studentName=studentName;
    }
    public String getGrade(){
        return this.grade;
    }
    public void setGrade(String grade){
        this.grade=grade;
    }
    public String getSection(){
        return this.section;
    }
    public void setSection(String section){
        this.section =section;
    }
    public String getParent(){
        return this.parent;
    }
    public void setParent(String parent){
        this.parent=parent;
    }
}

