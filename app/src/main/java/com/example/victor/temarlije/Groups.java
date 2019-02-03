package com.example.victor.temarlije;

public class Groups {
    private int ID;
    private String groupName;

    public Groups(){}
    public Groups(int ID,String groupName){
        this.ID=ID;
        this.groupName=groupName;
    }
    public Groups(String groupName){
        this.groupName=groupName;
    }
    public int getID(){ return this.ID; }
    public void setID(int id){
        this.ID=id;
    }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
