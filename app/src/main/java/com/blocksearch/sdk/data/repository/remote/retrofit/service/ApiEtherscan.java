package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * SDK on 2017. 12. 21..
 */

public interface ApiEtherscan {

    @GET("/api?module=account&action=balance&tag=latest")
    Call<JsonObject> getEthBalance(
            @Query("address") String address,
            @Query("apikey") String apikey
    );


    @GET("/api?module=account&action=tokenbalance&tag=latest")
    Call<JsonObject> getTokenBalance(
            @Query("contractaddress") String contractaddress,
            @Query("address") String address,
            @Query("apiKey") String apiKey
    );

    @GET("/api?module=transaction&action=gettxreceiptstatus")
    Call<JsonObject> getPendingTransactionStatus(
            @Query("txhash") String txHash,
            @Query("apiKey") String apiKey
    );

}
