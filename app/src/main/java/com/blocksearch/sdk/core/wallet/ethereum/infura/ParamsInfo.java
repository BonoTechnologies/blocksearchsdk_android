package com.blocksearch.sdk.core.wallet.ethereum.infura;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ParamsInfo {

    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("data")
    @Expose
    private String data;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
