package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;
import com.theblockchain.coinus.wallet.BuildConfig;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * SDK on 2018. 2. 12..
 */

public interface ApiBlockCypher {
    //  https://github.com/blockcypher/java-client

    @GET( BuildConfig.API_BITCOIN_PREFIX )
    Call<JsonObject> getMainInfo();


    @GET( BuildConfig.API_BITCOIN_PREFIX + "/addrs/{publicAddress}/balance" )
    Call<JsonObject> getBalance(
            @Path("publicAddress") String publicAddress
    );

    
    @GET( BuildConfig.API_BITCOIN_PREFIX + "/addrs/{publicAddress}/full?limit=50" )
    Call<JsonObject> getTxHistory(
            @Path("publicAddress") String publicAddress
    );

    @GET( BuildConfig.API_BITCOIN_PREFIX + "/txs/{txHash}?limit=20000" )
    Call<JsonObject> getTxInfo(
            @Path("txHash") String txHash
    );

    @Headers("Content-Type: application/json")
    @POST( BuildConfig.API_BITCOIN_PREFIX + "/txs/new" )
    Call<JsonObject> postNewSkeletonTransaction(
            @Body RequestBody body
    );

    @Headers("Content-Type: application/json")
    @POST( BuildConfig.API_BITCOIN_PREFIX + "/txs/send" )
    Call<JsonObject> postSendTransaction(
            @Body RequestBody body
    );
}
