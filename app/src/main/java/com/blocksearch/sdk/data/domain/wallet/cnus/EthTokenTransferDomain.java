package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.Serializable;

import lombok.Data;

/**
 * SDK on 2018. 12. 9..
 */

@Data
public class EthTokenTransferDomain implements Serializable {

    private String txHash;
    private long txIndex;
    private int txNonce;
    private long logIndex;
    private long tokenId;
    private String contractAddress;
    private String transferFrom;
    private String transferTo;
    private String transferValue;
    private String transferDisplayValue;
    private long blockTimestamp;
    private String blockHash;
    private long blockNumber;
    private String inOutDcd;
    private String inOut;
    private EthTokenDomain token;

}
