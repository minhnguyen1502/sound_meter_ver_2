package com.example.exe01.ui.sound.sound_meter.model;

public class SoundItem {
    private int id;
    private long startTime;
    private String title;
    private long duration;
    private float min;
    private float max;
    private float avg;
    private String description;
    private byte[] image;
//    private byte[] video; // Added property for video


    public SoundItem(int id, long startTime, String title, long duration, float min, float max, float avg, String description, byte[] image) {
        this.id = id;
        this.startTime = startTime;
        this.title = title;
        this.duration = duration;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.description = description;
        this.image = image;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
