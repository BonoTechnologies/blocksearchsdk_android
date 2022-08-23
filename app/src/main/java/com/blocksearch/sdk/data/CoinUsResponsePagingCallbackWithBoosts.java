package com.blocksearch.sdk.data;

import com.theblockchain.coinus.wallet.data.domain.atomic.PagingForm;

public interface CoinUsResponsePagingCallbackWithBoosts<T, U> {

    void onResultFetched(T response, U boosts, PagingForm pagingForm);

    void onResultFailed(int code, String message);

}
