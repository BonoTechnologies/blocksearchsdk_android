package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ParaSwapV4 {

//    @GET("/v2/tokens/{network}")
//    Call<JsonObject> getAllTokenList(
//            @Path("network") int network
//    );
////
//    @GET("/v2/prices")
//    Call<JsonObject> getPrice(
//        @QueryMap Map<String, Object> params
//    );

    @GET("/v2/users/tokens/{network}/{address}/{contract}")
    Call<JsonObject> getTokenInfo(
            @Path("network") int network,
            @Path("address") String walletAddress,
            @Path("contract") String contractAddress
    );

    /**
     * check wallet approve
     *
     * @param network
     * @param wno
     * @param contractAddress
     */
    @GET("/v2/paraswap/users/tokens/{network}/{wno}/{contractAddress}")
    Call<JsonObject> checkWalletApprove(
            @Path("network") long network,
            @Path("wno") long wno,
            @Path("contractAddress") String contractAddress
    );

//    @POST("/v2/transactions/{network}")
//    Call<JsonObject> buildTransaction(
//            @Path("network") int network,
//            @Body Map<String, Object> params
//    );

    /**
     * get token list
     *
     * @param network
     * @param params { wno: int, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/paraswap/tokens/{network}")
    Call<JsonObject> getTokenList(
            @Path("network") int network,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * get popular token list
     *
     * @param network
     * @param params { wno: int, keyword: String }
     */
    @GET("/v2/paraswap/tokens/{network}/popular")
    Call<JsonObject> getPopularTokenList(
            @Path("network") int network,
            @QueryMap Map<String, Object> params
    );

    @GET("/v2/paraswap/prices")
    Call<JsonObject> getPrice(
            @QueryMap Map<String, Object> params
    );

    /**
     * get wallet approve list
     *
     * @param wno
     * @param params { walletAddress: String, txId: String, txStDcd: String, txNonce: int, swapTokenId: long, contractAddress: String, spendAddress: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/paraswap/wallets/{wno}/approves")
    Call<JsonObject> getWalletApproveList(
            @Path("wno") long wno,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * send approve transactions
     *
     * @param wno
     * @param params { hex: String, txHash: String, txNonce: long, from: String, to: String, contractAddress: String, txValue: long, txGasPrice: long, txGas: long, txInput: String, walletAddress: String, swapTokenId: long, spendAddress: String, allowanceAmt: String }
     */
    @POST("/v2/paraswap/wallets/{wno}/send-approve-transaction")
    Call<JsonObject> sendApproveTransactions(
            @Path("wno") long wno,
            @Body Map<String, Object> params
    );

    /**
     * build transaction
     *
     */
    @POST("/v2/paraswap/transactions/{network}")
    Call<JsonObject> buildTransaction(
            @Path("network") int network,
            @Body Map<String, Object> params
    );

    @POST("/v2/transactions/{network}")
    Call<JsonObject> buildTransactionFromParaSwap(
            @Path("network") int network,
            @Body Map<String, Object> params
    );

    @GET("/v2/prices")
    Call<JsonObject> getPriceFromParaSwap(
            @QueryMap Map<String, Object> params
    );


    /**
     * send swap transactions
     *
     * @param wno
     * @param params { hex: String, txHash: String, txNonce: long, from: String, to: String, contractAddress: String, txValue: long, txGasPrice: long, txGas: long, txInput: String, walletAddress: String, sourceSwapTokenId: long, sourceContractAddress: String, sourceAmt: String, sourceUsd: String, destSwapTokenId: long, destContractAddress: String, destAmt: String, destAmtFeeDeducted: String, destUsd: String, refId: String, feeRate: String, feeAmount: String }
     */
    @POST("/v2/paraswap/wallets/{wno}/send-swap-transaction")
    Call<JsonObject> sendSwapTransactions(
            @Path("wno") long wno,
            @Body Map<String, Object> params
    );

////
////    @GET("/v2/users/tokens/{network}/{address}/{contract}")
////    Call<JsonObject> getTokenInfo(
////            @Path("network") int network,
////            @Path("address") String walletAddress,
////            @Path("contract") String contractAddress
////    );
//
//    @POST("/paraswap/transactions/{network}")
//    Call<JsonObject> buildTransaction(
//            @Path("network") int network,
//            @Body Map<String, Object> params
//    );



}
