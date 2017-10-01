package com.example.dima.mytestingapp.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dima on 13.09.2017.
 */

public class ItemServerReminder {

    @SerializedName("reminder_type")
    @Expose
    private String type;
    @SerializedName("reminder_name")
    @Expose
    private String name;
    @SerializedName("reminder_date")
    @Expose
    private String date;
    @SerializedName("reminder_time")
    @Expose
    private String time;
    @SerializedName("reminder_repeat")
    @Expose
    private String repeat;
    @SerializedName("reminder_img_marker")
    @Expose
    private String imgMarker;
    @SerializedName("reminder_marker_name")
    @Expose
    private String imgMarkerName;
    @SerializedName("reminder_sound")
    @Expose
    private String sound;
    @SerializedName("reminder_reflogin")
    @Expose
    private String refLogin;
    @SerializedName("local_id")
    @Expose
    private String localId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getImgMarker() {
        return imgMarker;
    }

    public void setImgMarker(String imgMarker) {
        this.imgMarker = imgMarker;
    }

    public String getImgMarkerName() {
        return imgMarkerName;
    }

    public void setImgMarkerName(String imgMarkerName) {
        this.imgMarkerName = imgMarkerName;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getRefLogin() {
        return refLogin;
    }

    public void setRefLogin(String refLogin) {
        this.refLogin = refLogin;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }
}
