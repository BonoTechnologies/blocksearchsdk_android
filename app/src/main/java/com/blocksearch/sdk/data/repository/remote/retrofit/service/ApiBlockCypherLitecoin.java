package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;
import com.theblockchain.coinus.wallet.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * SDK on 2018. 2. 12..
 */

public interface ApiBlockCypherLitecoin {
    //  https://github.com/blockcypher/java-client

    @GET( BuildConfig.API_LITECOIN_PREFIX )
    Call<JsonObject> getMainInfo();


    @GET( BuildConfig.API_LITECOIN_PREFIX + "/addrs/{publicAddress}/balance" )
    Call<JsonObject> getBalance(
            @Path("publicAddress") String publicAddress
    );


    @GET( BuildConfig.API_LITECOIN_PREFIX + "/addrs/{publicAddress}/full?limit=50" )
    Call<JsonObject> getTxHistory(
            @Path("publicAddress") String publicAddress
    );


    @GET( BuildConfig.API_LITECOIN_PREFIX + "/txs/{txHash}" )
    Call<JsonObject> getTxInfo(
            @Path("txHash") String txHash
    );
}
