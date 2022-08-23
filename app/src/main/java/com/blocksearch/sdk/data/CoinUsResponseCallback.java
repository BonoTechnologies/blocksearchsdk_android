package com.blocksearch.sdk.data;

/**
 * SDK on 2017. 12. 21..
 */

public interface CoinUsResponseCallback<T> {

    void onResultFetched(T response);

    void onResultFailed(int code, String message);

}
