package com.example.dima.mytestingapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Activitys.CurrencyExchange;
import com.example.dima.mytestingapp.Activitys.RatesCalcActivity;
import com.example.dima.mytestingapp.Adapters.RecyclerRatesAdapter;
import com.example.dima.mytestingapp.Items.ItemRates;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.CurrencyExchangeService;
import com.pepperonas.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentExchangeRates.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentExchangeRates#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentExchangeRates extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    Retrofit retrofit;
    CurrencyExchangeService service;

    private RecyclerView listRecycleRates;
    private RecyclerRatesAdapter recyclerRatesAdapter;
    private ArrayList<ItemRates> arrayListRates;
    TextView rateType;

    SharedPreferences sPrefSelection;

    String[] rates = {"USD", "EUR", "RUB", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK",
            "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD",
            "PHP", "PLN", "RON", "SEK", "SGD", "THB", "TRY", "ZAR"
    };


    String selection = "EUR";
    int numSelection;

    RelativeLayout relLayoutRateType;


    private OnFragmentInteractionListener mListener;

    public FragmentExchangeRates() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentExchangeRates.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentExchangeRates newInstance(String text) {
        FragmentExchangeRates fragment = new FragmentExchangeRates();
        Bundle mBundle = new Bundle();
        mBundle.putString("FragmentExchangeRates", text);
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Имя Toolbar
        Activity activity = this.getActivity();
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setTitle("Курсы валют");
        }

        view = inflater.inflate(R.layout.ftagment_exchange_rates, container, false);

        rateType = (TextView) view.findViewById(R.id.rateType);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CurrencyExchangeService.class);


        listRecycleRates = (RecyclerView) view.findViewById(R.id.listRates);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listRecycleRates.setLayoutManager(llm);

        relLayoutRateType = (RelativeLayout) view.findViewById(R.id.relLayoutRateType);

        relLayoutRateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Rates", Toast.LENGTH_SHORT).show();

                showSingleChoice();

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCurrencyExchangeData();
    }

    public void loadCurrencyExchangeData(){

        rateType.setText("Стоимость " + selection);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //ratesList = new ArrayList<ItemRates>(Arrays.asList(rates), );

        Call<CurrencyExchange> call = service.loadCurrencyRate("latest?base=" + selection);
        call.enqueue(new Callback<CurrencyExchange>() {
            @Override
            public void onResponse(Call<CurrencyExchange> call, Response<CurrencyExchange> response) {
                CurrencyExchange currencyExchange = response.body();
//                listRecycleRates.setAdapter(new RecyclerRatesAdapter(getContext(), currencyExchange.getRatesList(), getContext()));

                listRecycleRates.setAdapter(new RecyclerRatesAdapter(currencyExchange.getRatesList(), new RecyclerRatesAdapter.OnItemClickListener() {
                    @Override public void onItemClick(ItemRates item) {
//                        Toast.makeText(getContext(), "Item Clicked " + item.getNameRate(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), RatesCalcActivity.class);
                        intent.putExtra("rate_name_from", selection);
                        intent.putExtra("rate_name_to", item.getNameRate());
                        intent.putExtra("rate_course", item.getCourseRate());

                        startActivity(intent);
                    }
                }));


            }

            @Override
            public void onFailure(Call<CurrencyExchange> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

//        service = retrofit.create(CurrencyExchangeService.class);
//        Call<CurrencyExchange> call = service.loadCurrencyExchange();
//        call.enqueue(new Callback<CurrencyExchange>() {
//            @Override
//            public void onResponse(Call<CurrencyExchange> call, Response<CurrencyExchange> response) {
//                CurrencyExchange currencyExchange = response.body();
//                listRecycleRates.setAdapter(new RecyclerRatesAdapter(getContext(), currencyExchange.getRatesList(), getContext()));
//            }
//
//            @Override
//            public void onFailure(Call<CurrencyExchange> call, Throwable t) {
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    public void showSingleChoice() {

        sPrefSelection = getActivity().getSharedPreferences("SharedPrefSelection", MODE_PRIVATE);
        numSelection = sPrefSelection.getInt("save_selection", 1);

        new MaterialDialog.Builder(getActivity())
                .title("Валюты")
                .message(null)
                .positiveText("OK")
                .negativeText("CANCEL")
                .positiveColor(R.color.green_500)
                .negativeColor(R.color.pink_400)
                .listItemsSingleSelection(false, rates)
                .selection(numSelection)
                .itemClickListener(new MaterialDialog.ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, long id) {
                        super.onClick(v, position, id);
//                        Toast.makeText(getActivity(), rates[position], Toast.LENGTH_SHORT).show();
                        selection = rates[position];

                        sPrefSelection = getActivity().getSharedPreferences("SharedPrefSelection",MODE_PRIVATE);
                        SharedPreferences.Editor edPrefSelection = sPrefSelection.edit();
                        edPrefSelection.putInt("save_selection", position);
                        edPrefSelection.commit();

                        loadCurrencyExchangeData();
                    }
                })
                .itemLongClickListener(new MaterialDialog.ItemLongClickListener() {
                    @Override
                    public void onLongClick(View view, int position, long id) {
                        super.onLongClick(view, position, id);
                    }
                })
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(AlertDialog dialog) {
                        super.onShow(dialog);
                    }
                })
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                    }


                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
