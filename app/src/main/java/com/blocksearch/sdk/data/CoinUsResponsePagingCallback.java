package com.blocksearch.sdk.data;

import com.theblockchain.coinus.wallet.data.domain.atomic.PagingForm;

/**
 * SDK on 2018. 12. 7..
 */

public interface CoinUsResponsePagingCallback<T> {

    void onResultFetched(T response, PagingForm pagingForm);

    void onResultFailed(int code, String message);

}
