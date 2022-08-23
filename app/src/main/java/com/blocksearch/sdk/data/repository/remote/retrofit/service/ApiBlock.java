package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface ApiBlock {
    /**
     * 트랜잭션 정보 단 건 조회
     * //TODO(Giwung) : Pending 체크로 사용 할것.
     *
     * @param txHash
     */
    @GET("/v1/ethereum/txs/{txHash}")
    Call<JsonObject> getEthTx(
            @Path("txHash") String txHash
    );

    /**
     * 이더리움 계정의 트랜잭션 목록
     *
     * @param address
     * @param params { inOutDcd: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v1/ethereum/address/{address}/txs")
    Call<JsonObject> getEthTxByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 이더리움 계정의 특정 토큰 전송 목록
     *
     * @param address
     * @param contractAddress
     * @param params }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v1/ethereum/address/{address}/tokens/{contractAddress}/transfers")
    Call<JsonObject> getEthTokenTransferByAddressList(
            @Path("address") String address,
            @Path("contractAddress") String contractAddress,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 이더리움 계정 정보 단 건 조회 (ETH 계정 정보 및 Balance && Token Balance 조회)
     *
     * @param address
     * @param params { currency: String, includes: String }
     */
    @GET("/v1/ethereum/address/{address}")
    Call<JsonObject> getEthAddress(
            @Path("address") String address,
            @QueryMap Map<String, Object> params
    );
}
