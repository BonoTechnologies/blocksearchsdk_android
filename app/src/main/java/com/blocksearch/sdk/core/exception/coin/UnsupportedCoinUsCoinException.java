package com.blocksearch.sdk.core.exception.coin;


import com.blocksearch.sdk.core.coins.CoinType;

public class UnsupportedCoinUsCoinException extends RuntimeException {
    public UnsupportedCoinUsCoinException(CoinType type) {
        super("UnSupportedCoinUsCoin : " + type);
    }
}
