package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

@Data
public class EthEstimateFee {

    private int safeGasPrice;
    private int proposeGasPrice;
    private int fastGasPrice;
    
}
