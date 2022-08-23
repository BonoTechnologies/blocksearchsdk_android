package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiArgos {

    /**
     * Form 제출
     */
    @Headers({
            "x-api-key:7kFFOJBvOA9k0DT8yEKBk9mxkBIe7gxd9DMVUwJL",
            "Content-Type:application/x-www-form-urlencoded"
    })
    @POST("/v2/submissions")
    Call<JsonObject> requestArgosAuth(
            @Body RequestBody body
    );


    /**
     * Image Document 제출
     */

    @POST("/v2/submissions/{email}/{submission_id}/documents")
    Call<JsonObject> uploadArgosImage(
            @Header("x-api-key") String apiKey,
            @Path("email") String email,
            @Path("submission_id") String submission_id,
            @Body RequestBody body
    );

    /**
     * KYC Auth State 확인
     */
    @Headers({
            "x-api-key:7kFFOJBvOA9k0DT8yEKBk9mxkBIe7gxd9DMVUwJL"
    })
    @GET("/v2/submissions/{email}/{submission_id}")
    Call<JsonObject> getArgosAuthState(
            @Path("email") String email,
            @Path("submission_id") String submission_id
    );



}
