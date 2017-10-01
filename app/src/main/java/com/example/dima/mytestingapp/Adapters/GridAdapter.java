package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dima.mytestingapp.Items.Item;
import com.example.dima.mytestingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dima on 17.07.2017.
 *
 * Адаптер реализован с помощью дополнительной библиотеки Picasso
 * http://square.github.io/picasso/
 * https://futurestud.io/tutorials/picasso-adapter-use-for-listview-gridview-etc
 *
 * для отображения большого количества изображений в GridView
 */

public class GridAdapter extends ArrayAdapter<Item> {

    Context context;
    View view;

    ArrayList<Item> list;
    LayoutInflater layoutInflater;

    public GridAdapter(Context context, int idTvName, int idTvBtnImg, int idTvOnBtnCount, ArrayList<Item> list) {
        super(context, idTvName, idTvBtnImg, list);

        this.context = context;
        this.list = list;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        DecimalFormat decimal_formatter = new DecimalFormat("");
//        DecimalFormatSymbols custom = new DecimalFormatSymbols();
//        custom.setDecimalSeparator(' ');
//        decimal_formatter.setDecimalFormatSymbols(custom);
//        decimal_formatter.setGroupingSize(3);

        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.item, parent, false);
        }

        TextView tvBtnName = (TextView) convertView.findViewById(R.id.tvBtnName);
        TextView tvBtnCount = (TextView) convertView.findViewById(R.id.tvBtnOnCount);

//        String str = list.get(position).getOnBtnCount();
//        Double num = Double.parseDouble(str);
//        String test = new DecimalFormat("00,000.00").format(num);
//        Log.d("myLogsD", "test = " + test);


        tvBtnName.setText(list.get(position).getBtnName());
        tvBtnCount.setText(list.get(position).getOnBtnCount());

        Picasso
                .with(context)
                .load(list.get(position).getImageId())
                .fit() // will explain later
                .into((ImageView) convertView.findViewById(R.id.imgBtn));

        return convertView;
    }
}
