package com.blocksearch.sdk.data.domain.atomic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.EthRbfDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.EthTokenTransferDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.EthTxDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.rvn.RvnRpcResultDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SDK on 2018. 4. 10..
 */

public class GearData<T> extends HashMap<String,Object> {

    public <T> List<T> getItems(Class<T> toValueType){
        List<T> result = new ArrayList<>();
        List items = (List)this.get("items");
        if (items!=null){
            Gson gson = new Gson();
            for (int i = 0; i < items.size(); i++) {
                result.add(gson.fromJson(gson.toJsonTree(items.get(i)), toValueType));
            }
            return result;
        } else { // 2017.01.12 /Giwung /Added Else Snippet code for a single data without "items"
            Gson gson = new Gson();
            result.add(gson.fromJson(gson.toJsonTree(this), toValueType));
        }
        return result;
    }

    public <T> T getItemsFindFirst(Class<T> toValueType){
        List<T> items = getItems(toValueType);
        if (items!=null && !items.isEmpty()){
            return items.get(0);
        }
        return null;
    }

    public PagingForm getPaging() {
        if (this.get("paging")!=null){
            Gson gson = new Gson();
            return gson.fromJson(gson.toJsonTree(this.get("paging")), PagingForm.class);
        }
        return null;
    }

    public GearResult getResult() {
        if (this.get("result") != null) {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJsonTree(this.get("result")), GearResult.class);
        }
        return null;
    }

    public EthRbfDomain getRbfEthereum() {
        EthRbfDomain ethRbfDomain = new EthRbfDomain();
        if (this.get("rbfAvailable") != null) {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(this.get("rbfAvailable"));
            if (jsonElement != null && !jsonElement.isJsonNull()) {

                JsonElement jsonEthereum = jsonElement.getAsJsonObject().get("ethereum");
                if (jsonEthereum != null && !jsonEthereum.isJsonNull()) {
                    JsonElement jsonEthTxDomain = jsonEthereum.getAsJsonObject().get("ethTxDomain");
                    if (jsonEthTxDomain != null && !jsonEthTxDomain.isJsonNull()) {
                        ethRbfDomain.setEthTxDomain(gson.fromJson(jsonEthTxDomain, EthTxDomain.class));
                    }

                    JsonElement jsonEthTokenTransferDomain = jsonEthereum.getAsJsonObject().get("ethTokenTransferDomain");
                    if (jsonEthTokenTransferDomain != null && !jsonEthTokenTransferDomain.isJsonNull()) {
                        ethRbfDomain.setEthTokenTransferDomain(gson.fromJson(jsonEthTokenTransferDomain, EthTokenTransferDomain.class));

    //                    JsonElement jsonToken = jsonEthTokenTransferDomain.getAsJsonObject().get("token");
    //                    if (jsonToken != null && !jsonToken.isJsonNull()) {
    //                        ethRbfDomain.getEthTokenTransferDomain().setToken(gson.fromJson(jsonToken, EthTokenDomain.class));
    //                    }
                    }

                    return ethRbfDomain;
                }
            }
        }
        return null;
    }

    public String getResultType() {
        if (this.get("resultType") != null) {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJsonTree(this.get("resultType")), String.class);
        }
        return null;
    }

    public RvnRpcResultDomain getRvnRpcResultDomain() {
        if (this.get("rvnRpcResultDomain") != null) {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJsonTree(this.get("rvnRpcResultDomain")), RvnRpcResultDomain.class);
        }
        return null;
    }
}
