package com.blocksearch.sdk.core.exception.coin;

public class GasPriceNotLoadedException extends RuntimeException {
    public GasPriceNotLoadedException() {
        super("Gas Price is not initiated");
    }
}
