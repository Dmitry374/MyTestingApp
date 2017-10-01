package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 15.08.2017.
 */

public class ItemChildReminder {

    private String title;
    private String type;
    private String time;
    private String date;
    private String repeatPeriod;
    private int id;
    int imageReminderIcon;
    String nameReminderIcon;

    public ItemChildReminder(String title, String type, String time, String date, String repeatPeriod, int id,
                             int imageReminderIcon, String nameReminderIcon) {
        this.title = title;
        this.type = type;
        this.time = time;
        this.date = date;
        this.repeatPeriod = repeatPeriod;
        this.id = id;
        this.imageReminderIcon = imageReminderIcon;
        this.nameReminderIcon = nameReminderIcon;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getRepeatPeriod() {
        return repeatPeriod;
    }

    public int getId() {
        return id;
    }

    public int getImageReminderIcon() {
        return imageReminderIcon;
    }

    public String getNameReminderIcon() {
        return nameReminderIcon;
    }
}
