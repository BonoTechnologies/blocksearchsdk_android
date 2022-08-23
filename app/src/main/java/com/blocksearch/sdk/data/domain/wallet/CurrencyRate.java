package com.blocksearch.sdk.data.domain.wallet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * SDK on 2017. 12. 21..
 */

public class CurrencyRate extends RealmObject {

    @PrimaryKey
    private int seq;
    private String name;
    private int rate;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
