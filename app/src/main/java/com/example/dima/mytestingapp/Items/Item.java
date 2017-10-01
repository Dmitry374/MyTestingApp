package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 17.07.2017.
 */

/** Класс для создания объекта динамически создаваемых кнопок*/

public class Item {
    String btnName;
    Integer imageId;
    String onBtnCount;

    public Item(String btnNane , Integer imageId, String onBtnCount){
        this.btnName = btnNane;
        this.imageId = imageId;
        this.onBtnCount = onBtnCount;
    }

    public String getBtnName() {
        return btnName;
    }

    public Integer getImageId(){
        return imageId;
    }

    public String getOnBtnCount() {
        return onBtnCount;
    }
}
