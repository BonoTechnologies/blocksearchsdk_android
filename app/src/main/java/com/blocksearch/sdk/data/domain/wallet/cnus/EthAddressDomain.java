package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SDK on 2018. 12. 11..
 */

@Data
public class EthAddressDomain {

    private String address;
    private String balance;
//    private BigInteger balance;
    private String displayBalance;
    private WalletCryptoMarketCapDomain marketCap;
    private String ethAccountDcd;
    private String ethAccount;
    private String tokenContractYn;
    private long indexedBlockNumber;
    private long addressTotalTxCount;
    private long addressOutTxCount;
    private long addressInTxCount;
    private long addressTotalTokenTransferCount;
    private long addressOutTokenTransferCount;
    private long addressInTokenTransferCount;
    private long addressTotalMinedBlockCount;
    private List<EthTokenDomain> tokens = new ArrayList<>();

}