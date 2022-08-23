package com.blocksearch.sdk.data.domain.wallet.tx;

import java.util.Date;

/**
 * SDK on 2018. 4. 1..
 */

public class Price {

    private String rate;
    private String currency;
    private String diff;
    private Date ts;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
