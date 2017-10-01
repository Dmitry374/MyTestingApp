package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 04.08.2017.
 */

public class AdapterSpinnerStatistic extends BaseAdapter {
    Context ctx;
//    ArrayList<ItemSpinnerStatistic> list;
    ArrayList<String> list;
    LayoutInflater inflater;
    public AdapterSpinnerStatistic(Context context, ArrayList<String> list){
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //        Используем созданные, но неиспользуемые view
        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.item_spinner_statistic, parent, false);
        }

        TextView tvStatisticDate = (TextView) view.findViewById(R.id.tvItemSpinnerStatistic);
        tvStatisticDate.setText(list.get(position));

        return view;
    }
}
