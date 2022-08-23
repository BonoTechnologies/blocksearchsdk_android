package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

/**
 * SDK on 2018. 12. 11..
 */

@Data
public class EthTxTokenTransferDomain {

    private long tokenId;
    private String contractAddress;
    private String tokenName;
    private String tokenSymbol;
    private int tokenDecimals;
    private String transferFrom;
    private String transferTo;
    private String transferValue;
    private String transferDisplayValue;

}