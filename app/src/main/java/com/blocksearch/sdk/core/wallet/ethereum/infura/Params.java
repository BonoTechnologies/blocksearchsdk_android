package com.blocksearch.sdk.core.wallet.ethereum.infura;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Params {

//    @SerializedName("paramsInfo")
//    @Expose
//    private ParamsInfo paramsInfo;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("pending")
    @Expose
    private String pending;

//    public ParamsInfo getParamsInfo() {
//        return paramsInfo;
//    }
//
//    public void setParamsInfo(ParamsInfo paramsInfo) {
//        this.paramsInfo = paramsInfo;
//    }

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

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }
}
