package com.blocksearch.sdk.data.repository.remote.retrofit;

import android.content.Context;

import com.google.gson.JsonObject;
import com.theblockchain.coinus.wallet.CoinUsApplication;
import com.theblockchain.coinus.wallet.common.CLog;
import com.theblockchain.coinus.wallet.data.repository.remote.client.HttpClient;

import retrofit2.Response;

public abstract class RemoteBase {

    public abstract String getApiUrl();

    /**
     *
     * @param backgroundThread Result Parsing on Bg Thread
     * @param classType Retrofit Interface.
     * @return Service Class
     */
    public <T> T createRetrofitService(boolean backgroundThread, Class<T> classType) {
        return createRetrofitService(CoinUsApplication.getInstance().getApplicationContext(), backgroundThread, classType);
    }

    public <T> T createRetrofitService(Context context, boolean backgroundThread, Class<T> classType) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(context).getClient(),
                getApiUrl(),
                backgroundThread);

        return retrofit.createService(classType);
    }

    protected void printLogAPIErrorResponse(Response<JsonObject> response, String methodName) {
        if (response != null) {
            String message = "";
            if (response.message() != null) {
                message = response.message();
            }

            CLog.e(methodName + " Error - Code : " + response.code() + " Message : " + message);
        }
    }

}
