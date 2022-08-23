package com.blocksearch.sdk.data.domain.wallet.eth;

import java.math.BigInteger;

import lombok.Data;

@Data
public class EthTokenDomain {
    private long tokenId;
    private String contractAddress;
    private String tokenName;
    private String tokenSymbol;
    private String tokenOwnerAddress;
    private String tokenTotalSupply;
    private int  tokenDecimals;
    private String symbolImgUrl;
    private BigInteger tokenBalance = BigInteger.ZERO;
    private String tokenDisplayBalance = "0";
    private long indexedBlockNumber;
    private long blockTimestamp;
    private long tokenHolderCount;
    private long tokenTransferCount;
}