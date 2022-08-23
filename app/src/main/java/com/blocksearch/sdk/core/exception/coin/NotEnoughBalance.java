package com.blocksearch.sdk.core.exception.coin;

public class NotEnoughBalance extends Throwable {
    private String cause;

    public NotEnoughBalance(String s) {
        cause = s;
    }

    public String getReason() {
        return cause;
    }
}
