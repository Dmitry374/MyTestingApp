package com.example.dima.mytestingapp.api;

import com.example.dima.mytestingapp.Activitys.CurrencyExchange;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Dima on 22.10.2017.
 */

public interface CurrencyExchangeService {
    @GET("latest")
//    @GET("latest?base=USD")
//    @GET("latest?symbols=USD,GBP")
    Call<CurrencyExchange> loadCurrencyExchange();

    @GET("")
    Call<CurrencyExchange> loadCurrencyRate(
            @Url String url
    );
}
