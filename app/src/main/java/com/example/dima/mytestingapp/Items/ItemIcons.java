package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 16.08.2017.
 */

public class ItemIcons {

    private String nameIcon;
    private int imgIcon;
    private int secondImgIcon;
    private int visibility;

    public ItemIcons(String nameIcon, int imgIcon, int secondImgIcon, int visibility) {
        this.nameIcon = nameIcon;
        this.imgIcon = imgIcon;
        this.secondImgIcon = secondImgIcon;
        this.visibility = visibility;
    }

    public String getNameIcon() {
        return nameIcon;
    }

    public int getImgIcon() {
        return imgIcon;
    }

    public int getSecondImgIcon() {
        return secondImgIcon;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
