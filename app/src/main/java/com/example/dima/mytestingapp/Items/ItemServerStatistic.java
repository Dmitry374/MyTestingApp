package com.example.dima.mytestingapp.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dima on 09.09.2017.
 */

public class ItemServerStatistic {

    @SerializedName("type_statistic")
    @Expose
    private String type;
    @SerializedName("name_statistic_get")
    @Expose
    private String nameGet;
    @SerializedName("kol_statistic")
    @Expose
    private String kol;
    @SerializedName("name_statistic_spend")
    @Expose
    private String nameSpend;
    @SerializedName("date_statistic")
    @Expose
    private String date;
    @SerializedName("time_statistic")
    @Expose
    private String time;
    @SerializedName("reflogin_statistic")
    @Expose
    private String refLogin;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameGet() {
        return nameGet;
    }

    public void setNameGet(String nameGet) {
        this.nameGet = nameGet;
    }

    public String getKol() {
        return kol;
    }

    public void setKol(String kol) {
        this.kol = kol;
    }

    public String getNameSpend() {
        return nameSpend;
    }

    public void setNameSpend(String nameSpend) {
        this.nameSpend = nameSpend;
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

    public String getRefLogin() {
        return refLogin;
    }

    public void setRefLogin(String refLogin) {
        this.refLogin = refLogin;
    }

}
