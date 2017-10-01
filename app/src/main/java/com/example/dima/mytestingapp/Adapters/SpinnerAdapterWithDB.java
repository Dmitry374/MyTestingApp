package com.example.dima.mytestingapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.ItemDataWithDB;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 22.07.2017.
 */

public class SpinnerAdapterWithDB extends ArrayAdapter<ItemDataWithDB> {
    int groupid;
    Activity context;
    ArrayList<ItemDataWithDB> list;
    LayoutInflater inflater;

    int pos;
    public SpinnerAdapterWithDB(Activity context, int groupid, int groupid2, int id, ArrayList<ItemDataWithDB> list) {
        super(context, id, list);

        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View contentView, ViewGroup parent){

        View itemView=inflater.inflate(groupid, parent, false);
        TextView tvGetName = (TextView) itemView.findViewById(R.id.tvGetName);
        TextView tvHowMach = (TextView) itemView.findViewById(R.id.tvHowMach);

        tvGetName.setText(list.get(position).getGetName());
        tvHowMach.setText(list.get(position).getHowMach());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);

    }
}
