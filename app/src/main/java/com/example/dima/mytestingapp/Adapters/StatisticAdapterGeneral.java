package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.ItemStatisticGeneral;
import com.example.dima.mytestingapp.R;

import java.util.ArrayList;

/**
 * Created by Dima on 15.10.2017.
 */

public class StatisticAdapterGeneral extends BaseAdapter {

    Context context;
    private LayoutInflater lInflater;
    ArrayList<ItemStatisticGeneral> list;

    public StatisticAdapterGeneral(Context ctx, ArrayList<ItemStatisticGeneral> products) {
        context = ctx;
        list = products;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = lInflater.inflate(R.layout.item_statistic_general, parent, false);
        }

        TextView tvStatisticDate = (TextView) view.findViewById(R.id.tvStatDateGeneral);
        tvStatisticDate.setText(list.get(position).getDate());

//        ImageView imageView = (ImageView) view.findViewById(R.id.imgIconStatistic);
//        imageView.setImageResource(list.get(position).getIconImage());

        TextView tvStatisticCount = (TextView) view.findViewById(R.id.tvStatCountGeneral);
        tvStatisticCount.setText(list.get(position).getOnCount());
//        tvStatisticCount.setTextColor(list.get(position).getColor());

//        tvStatisticCount.setTextColor(Color.parseColor(String.valueOf(R.color.colorPrimary)));
        tvStatisticCount.setTextColor(context.getResources().getColor(list.get(position).getColor()));


        TextView tvStatisticGetName = (TextView) view.findViewById(R.id.tvStatOnGetGeneral);
        tvStatisticGetName.setText(list.get(position).getGetName());


        TextView tvStatisticSpendName = (TextView) view.findViewById(R.id.tvStatGeneral);
        tvStatisticSpendName.setText(list.get(position).getSpendName());


        return view;
    }
}
