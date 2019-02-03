package com.example.victor.temarlije;

import java.sql.Time;

public class Messages {
    private int ID;
    private String messageSender;
    private String textMessage;
    private String textTime;
    private String photoUrl;
    private String group;

    public Messages(){}
    public Messages(int ID, String messageSender, String textMessage, String textTime, String photoUrl, String group){
        this.ID = ID;
        this.textMessage = textMessage;
        this.messageSender = messageSender;
        this.textTime = textTime;
        this.photoUrl = photoUrl;
        this.group = group;
    }
    public Messages(String messageSender, String textMessage, String textTime, String photoUrl, String group){
        this.textMessage = textMessage;
        this.messageSender = messageSender;
        this.textTime = textTime;
        this.photoUrl = photoUrl;
        this.group = group;
    }

    public int getID(){ return this.ID; }
    public void setID(int id){
        this.ID=id;
    }
    public String getMessageSender() { return messageSender; }
    public void setMessageSender(String messageSender) { this.messageSender = messageSender;}
    public String getTextMessage() { return textMessage; }
    public void setTextMessage(String textMessage){this.textMessage = textMessage;}
    public String getTextTime() { return textTime; }
    public void setTextTime(String textTime) { this.textTime = textTime; }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group;}
}