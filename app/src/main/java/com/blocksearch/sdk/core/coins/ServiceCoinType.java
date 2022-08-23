package com.blocksearch.sdk.core.coins;

import com.theblockchain.coinus.wallet.common.SearchConstants;

/**
 * Created by giwung on 2018-01-12.
 */

public enum ServiceCoinType {

    BITCOIN(SearchConstants.CNUS_COIN_ID_BTC),
    ETHER(SearchConstants.CNUS_COIN_ID_ETH),
    BITCOIN_CASH(SearchConstants.CNUS_COIN_ID_BCH),
    LITE_COIN(SearchConstants.CNUS_COIN_ID_LTC),
    QTUM(SearchConstants.CNUS_COIN_ID_QTUM),
    ;

    public final long mCoinId;

    ServiceCoinType(long coinId) {
        mCoinId = coinId;
    }

    public String toString() {
        return String.valueOf(mCoinId);
    }

    public long getCoinId() {
        return mCoinId;
    }
}
