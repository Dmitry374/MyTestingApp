package com.example.dima.mytestingapp.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Items.ItemRates;
import com.example.dima.mytestingapp.R;

import java.util.List;

/**
 * Created by Dima on 22.10.2017.
 */

public class RecyclerRatesAdapter extends RecyclerView.Adapter<RecyclerRatesAdapter.RatesViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ItemRates item);
    }

    private final OnItemClickListener listener;
    List<ItemRates> rates;

    public RecyclerRatesAdapter(List<ItemRates> rates, OnItemClickListener listener){
        this.rates = rates;
        this.listener = listener;
    }

    @Override
    public RatesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ex_rate, viewGroup, false);
        return new RatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RatesViewHolder personViewHolder, int i) {

        if (Double.toString(rates.get(i).getCourseRate()).equals("0.0")){
            personViewHolder.nameRate.setText(rates.get(i).getNameRate());
            personViewHolder.courseRate.setText("—");
        } else {
            personViewHolder.nameRate.setText(rates.get(i).getNameRate());
            personViewHolder.courseRate.setText(Double.toString(rates.get(i).getCourseRate()));
        }

        personViewHolder.bind(rates.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return rates.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }







    static class RatesViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nameRate;
        TextView courseRate;
        RatesViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nameRate = (TextView)itemView.findViewById(R.id.rateName);
            courseRate = (TextView)itemView.findViewById(R.id.ratePrice);
        }

        public void bind(final ItemRates rates, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(rates);
                }
            });
        }

    }

}
