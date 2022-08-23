package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;


@Data
public class EthTokenDomain {

    private long tokenId;
    private String contractAddress;
    private String tokenName;
    private String tokenSymbol;
    private String tokenOwnerAddress;
    private String tokenTotalSupply;
    private int tokenDecimals;
    private String tokenBalance;
    private String tokenDisplayBalance;
    private long indexedBlockNumber;
    private long tokenHolderCount;
    private WalletCryptoMarketCapDomain marketCap;
    private long tokenTransferCount;

}