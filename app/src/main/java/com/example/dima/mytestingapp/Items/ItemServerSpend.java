package com.example.dima.mytestingapp.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dima on 10.09.2017.
 */

public class ItemServerSpend {

    @SerializedName("spend_name")
    @Expose
    private String name;
    @SerializedName("spend_image")
    @Expose
    private String image;
    @SerializedName("kol_spend")
    @Expose
    private String kol;
    @SerializedName("ref_login_spend")
    @Expose
    private String refLogin;
    @SerializedName("local_id")
    @Expose
    private String localId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKol() {
        return kol;
    }

    public void setKol(String kol) {
        this.kol = kol;
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
