package com.blocksearch.sdk.data.domain.wallet.tx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SDK on 2018. 1. 5..
 */

// EthPlorer Coin Address Transaction & Token Transaction Result Object.
//https://github.com/EverexIO/Ethplorer/wiki/Ethplorer-API#get-address-transactions
//https://api.ethplorer.io/getAddressHistory/0xd7b4ed15480087d3c8b31c42d624d96779e009fc?apiKey=freekey&token=0x2c79794a9682b7c3889245dafe03a8fdfa414751
public class CoinUsTransaction implements Serializable {

    private int seq;

    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("hash")
    @Expose
    private String hash;    // Only used for Address Info.
    @SerializedName("input")
    @Expose
    private String input;
    @SerializedName("success")
    @Expose
    private boolean success;

    private String transactionHash; // Only used for Token Info.
    private CoinUsTokenInfo tokenInfo; // Only used for Token Info.
    private String type; // transfer, approve, issuance, mint, burn, etc
    private String memo;
    private String txType = "normal"; // pending, normal (lowercase)
    private String coinNm;
    private String symbol;
    private String img; // drawable img name
    private long coinId;
    private int walletAddressIndex;
    private String contractAddress;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCoinNm() {
        return coinNm;
    }

    public void setCoinNm(String coinNm) {
        this.coinNm = coinNm;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public CoinUsTokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(CoinUsTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCoinId() {
        return coinId;
    }

    public void setCoinId(long coinId) {
        this.coinId = coinId;
    }

    public int getWalletAddressIndex() {
        return walletAddressIndex;
    }

    public void setWalletAddressIndex(int walletAddressIndex) {
        this.walletAddressIndex = walletAddressIndex;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public static boolean isSuccessReceiptStatus( long receiptStatus ){
        //  1 성공, 0 실패, -1 데이터 없음.
        return receiptStatus == 1;
    }
}
