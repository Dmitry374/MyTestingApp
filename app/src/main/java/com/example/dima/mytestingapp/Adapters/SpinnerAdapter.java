package com.example.dima.mytestingapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.dima.mytestingapp.Items.ItemData;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 07.07.2017.
 */

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    int groupid;
    Activity context;
    ArrayList<ItemData> list;
    LayoutInflater inflater;
    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData> list){
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View contentView, ViewGroup parent){

        View itemView=inflater.inflate(groupid, parent, false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.img);
        imageView.setImageResource(list.get(position).getImageId());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);

    }
}
