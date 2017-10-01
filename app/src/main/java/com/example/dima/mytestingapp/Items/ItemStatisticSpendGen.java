package com.example.dima.mytestingapp.Items;

/**
 * Created by Dima on 30.07.2017.
 */

public class ItemStatisticSpendGen {

    private String date;
    private String onCount;
    private String getName;
    private String spendName;

    public ItemStatisticSpendGen(String _date, String _onCount, String _getName, String _spendName) {
        date = _date;
        onCount = _onCount;
        getName = _getName;
        spendName = _spendName;
    }

    public String getDate() {
        return date;
    }

    public String getOnCount() {
        return onCount;
    }

    public String getGetName() {
        return getName;
    }

    public String getSpendName() {
        return spendName;
    }

}
