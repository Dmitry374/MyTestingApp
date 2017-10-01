package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.ItemStatisticSpendGen;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 30.07.2017.
 */

public class StatisticAdapterSpendGen extends BaseAdapter {

    Context context;
    private LayoutInflater lInflater;
    ArrayList<ItemStatisticSpendGen> list;
    int color;

    public StatisticAdapterSpendGen(Context ctx, ArrayList<ItemStatisticSpendGen> products, int itemColor) {
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
            view = lInflater.inflate(R.layout.item_statistic_spend_general, parent, false);
        }

        TextView tvStatisticDate = (TextView) view.findViewById(R.id.tvStatDateSpendGen);
        tvStatisticDate.setText(list.get(position).getDate());

        TextView tvStatisticCount = (TextView) view.findViewById(R.id.tvStatCountSpendGen);
        tvStatisticCount.setText(list.get(position).getOnCount());
        tvStatisticCount.setTextColor(color);

        TextView tvStatisticGetName = (TextView) view.findViewById(R.id.tvStatSpendOnGetGeneral);
        tvStatisticGetName.setText(list.get(position).getGetName());


        TextView tvStatisticSpendName = (TextView) view.findViewById(R.id.tvStatSpendGeneral);
        tvStatisticSpendName.setText(list.get(position).getSpendName());


        return view;
    }

}
