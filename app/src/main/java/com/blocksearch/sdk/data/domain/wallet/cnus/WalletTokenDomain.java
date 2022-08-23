package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.Serializable;

import lombok.Data;

/**
 * SDK on 2018. 12. 9..
 */

@Data
public class WalletTokenDomain implements Serializable {

    private String cryptoActiveYn;
    private long tokenId;
    private String contractAddress;
    private String tokenTypeDcd;
    private String tokenType;
    private String tokenSymbol;
    private String tokenNm;
    private int tokenDecimals;
    private String tokenImgPath;
    private String tokenBalance;
    private WalletCryptoMarketCapDomain marketCap;

}