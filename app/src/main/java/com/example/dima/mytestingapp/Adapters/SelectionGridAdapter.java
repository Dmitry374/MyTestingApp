package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.dima.mytestingapp.Items.ItemIcons;
import com.example.dima.mytestingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dima on 16.08.2017.
 */

public class SelectionGridAdapter extends ArrayAdapter<ItemIcons> {

    Context context;
    View view;

    ArrayList<ItemIcons> list;
    LayoutInflater layoutInflater;
    int idTvBtnImg;

    public SelectionGridAdapter(Context context, int idTvBtnImg, ArrayList<ItemIcons> list) {
        super(context, idTvBtnImg, list);

        this.context = context;
        this.list = list;
        this.idTvBtnImg = idTvBtnImg;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ItemIcons getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.item_grid_select_icon, parent, false);
        }

        ImageView imgSecond = (ImageView) convertView.findViewById(R.id.secondImgIcon);
        imgSecond.setImageResource(list.get(position).getSecondImgIcon());

        if (list.get(position).getVisibility() == View.VISIBLE){
            imgSecond.setVisibility(View.VISIBLE);
        } else {
            imgSecond.setVisibility(View.INVISIBLE);
        }

        Picasso
                .with(context)
                .load(list.get(position).getImgIcon())
                .fit() // will explain later
                .into((ImageView) convertView.findViewById(R.id.imgIcon));

//        Picasso
//                .with(context)
//                .load(list.get(position).getSecondImgIcon())
//                .fit() // will explain later
//                .into((ImageView) convertView.findViewById(R.id.secondImgIcon));

        return convertView;
    }





}
