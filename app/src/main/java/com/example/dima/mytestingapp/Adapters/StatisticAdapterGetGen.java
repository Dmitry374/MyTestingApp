package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.ItemStatisticGetGen;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 03.08.2017.
 */

public class StatisticAdapterGetGen extends BaseAdapter {

    Context context;
    private LayoutInflater lInflater;
    ArrayList<ItemStatisticGetGen> list;
    int color;

    public StatisticAdapterGetGen(Context ctx, ArrayList<ItemStatisticGetGen> products, int itemColor) {
        context = ctx;
        list = products;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        color = itemColor;
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
            view = lInflater.inflate(R.layout.item_statistic_get_general, parent, false);
        }

        TextView tvStatisticDate = (TextView) view.findViewById(R.id.tvStatDateGetGen);
        tvStatisticDate.setText(list.get(position).getDate());

        TextView tvStatisticCount = (TextView) view.findViewById(R.id.tvStatCountGetGen);
        tvStatisticCount.setText(list.get(position).getOnCount());
//        tvStatisticCount.setTextColor(color);
        tvStatisticCount.setTextColor(context.getResources().getColor(color));

        TextView tvStatisticGetName = (TextView) view.findViewById(R.id.tvStatOnGetGeneral);
        tvStatisticGetName.setText(list.get(position).getGetName());

        return view;
    }
}
