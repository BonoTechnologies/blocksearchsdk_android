package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;
import com.theblockchain.coinus.wallet.data.domain.wallet.tx.CoinUsTransaction;
import com.theblockchain.coinus.wallet.data.domain.wallet.tx.CoinUsTransactionInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * SDK on 2017. 12. 21..
 */

public interface ApiEthplorer {

    @GET("/getAddressInfo/{publicAddress}")
    Call<JsonObject> getAddressInfo(
            @Path("publicAddress") String publicAddress,
            @Query("apiKey") String apiKey
    );


    @GET("/getAddressTransactions/{publicAddress}?type=transfer&limit=50")
    Call<List<CoinUsTransaction>> getCoinTransactionHistory(
            @Path("publicAddress") String publicAddress,
            @Query("apiKey") String apiKey
    );

    ///getAddressHistory/0x1f5006dff7e123d550abc8a4c46792518401fcaf?apiKey=freekey&token=0xc66ea802717bfb9833400264dd12c2bceaa34a6d&type=transfer
    @GET("/getAddressHistory/{publicAddress}?type=transfer&limit=30")
    Call<JsonObject> getTokenTransactionHistory(
            @Path("publicAddress") String publicAddress,
            @Query("token") String contractAddress,
            @Query("apiKey") String apiKey
    );

    @GET("/getTxInfo/{publicAddress}")
    Call<CoinUsTransactionInfo> getTransactionInfo(
            @Path("publicAddress") String publicAddress,
            @Query("apiKey") String apiKey
    );

}
