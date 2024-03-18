package com.example.ramadan1;

public class soundsModel {
    private int id;
    private String soundName;

    public soundsModel(int id, String soundName) {
        this.id = id;
        this.soundName = soundName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }
}
