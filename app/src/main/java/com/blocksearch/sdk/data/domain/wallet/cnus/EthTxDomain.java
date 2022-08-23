package com.blocksearch.sdk.data.domain.wallet.cnus;

import com.blocksearch.sdk.data.domain.wallet.address.UserAddressBookDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SDK on 2018. 12. 9..
 */

@Data
public class EthTxDomain implements Serializable {

    private String txHash;
    private int txReceiptStatus;
    private int txNonce;
    private String blockHash;
    private long blockNumber;
    private int txIndex;
    private EthAddressDomain from;
    private EthAddressDomain to;
    private String txValue;
    private String txDisplayValue;
    private BigDecimal txGasPrice;
    private BigDecimal txGasPriceBoosts;
    private long txGas;
    private String txInput;
    private long blockTimestamp;
    private long cumulativeGasUsed;
    private long txGasUsed;
    private BigDecimal txCost;
    private String contractAddress;
    private String txReceiptRoot;
    private List<EthTxTokenTransferDomain> tokenTransfers = new ArrayList<>();
    private String txStatus;
    private String inOutDcd;
    private String inOut;
    private List<UserAddressBookDomain> addressBooks = new ArrayList<>();
    private int txTotalTokenTransferCount;

}
