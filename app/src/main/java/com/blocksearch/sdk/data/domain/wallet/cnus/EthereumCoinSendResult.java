package com.blocksearch.sdk.data.domain.wallet.cnus;

import com.theblockchain.coinus.wallet.data.domain.atomic.GearResult;

import lombok.Data;

@Data
public class EthereumCoinSendResult {

    private String txId;
    private GearResult result;


}
