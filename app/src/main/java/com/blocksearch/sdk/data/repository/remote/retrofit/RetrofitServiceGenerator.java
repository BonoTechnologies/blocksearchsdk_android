package com.blocksearch.sdk.data.repository.remote.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theblockchain.coinus.wallet.common.CoinUsConstants;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * SDK on 2017. 12. 8..
 */

public class RetrofitServiceGenerator {

    private final String TAG = "RetrofitService";
    private Retrofit.Builder builder;

    public RetrofitServiceGenerator(OkHttpClient client) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        builder = new Retrofit.Builder()
                .baseUrl(CoinUsConstants.SERVER_DOMAIN)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)); //Json Parser 추가

    }

    public RetrofitServiceGenerator(OkHttpClient client, String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)); //Json Parser 추가

    }


    public RetrofitServiceGenerator(OkHttpClient client, String baseUrl, boolean isBackground) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client);

        if (isBackground) {
            builder.callbackExecutor(Executors.newSingleThreadExecutor());
        }
        builder.addConverterFactory(GsonConverterFactory.create(gson)); //Json Parser 추가
    }

    public <T> T createService(Class<T> serviceClass) {
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

}
