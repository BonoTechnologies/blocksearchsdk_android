package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

/**
 * SDK on 2018. 4. 14..
 */

@Data
public class WalletCryptoTokenDomain {

    private int displayOrd;
    private long tokenId;
    private String contractAddress;
    private String tokenTypeDcd;
    private String tokenType;
    private String tokenImgPath;
    private String tokenSymbol;
    private String tokenNm;
    private int tokenDecimals;
    private String activeYn;

}
