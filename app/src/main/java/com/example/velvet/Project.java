package com.example.velvet;

import java.util.HashMap;
public class Project {
    private String dayCreated;// private String mediaCluster;
    private String name; private String timeStamp;
    private HashMap<String,String> labels;
    private HashMap<String,String> audio;

    public Project() {
        this.dayCreated = dayCreated;
        this.name = name;
        this.timeStamp = timeStamp;
        this.labels = labels;
        this.audio = audio;
    }
    public Project(String name, String dayCreated,String timeStamp){
        this.name = name;
        this.dayCreated = dayCreated;
        this.timeStamp = timeStamp;
    }

    public String getDayCreated() {
        return dayCreated;
    }

    public void setDayCreated(String dayCreated) {
        this.dayCreated = dayCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HashMap<String, String> getLabels() {
        return labels;
    }

    public void setLabels(HashMap<String, String> labels) {
        this.labels = labels;
    }

    public HashMap<String, String> getAudio() {
        return audio;
    }

    public void setAudio(HashMap<String, String> audio) {
        this.audio = audio;
    }
}
