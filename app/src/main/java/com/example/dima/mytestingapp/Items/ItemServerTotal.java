package com.example.dima.mytestingapp.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dima on 09.09.2017.
 */

public class ItemServerTotal {

    @SerializedName("kol_total_get")
    @Expose
    private String kol;
    @SerializedName("ref_login_total_get")
    @Expose
    private String refLogin;

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

}
