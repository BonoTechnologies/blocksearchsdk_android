package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonArray;
import com.theblockchain.coinus.wallet.core.wallet.ethereum.infura.ERC20Data;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * SDK on 2017. 12. 27..
 */

public interface ApiInfura {

    @POST("/mew")
    Call<JsonArray> requestERC20(
            @HeaderMap Map<String, String> headers,
            @Body List<ERC20Data> params
    );

}
