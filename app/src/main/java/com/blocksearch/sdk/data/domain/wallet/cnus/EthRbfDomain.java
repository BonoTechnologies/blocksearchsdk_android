package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

@Data
public class EthRbfDomain {

    private EthTxDomain ethTxDomain;
    private EthTokenTransferDomain ethTokenTransferDomain;

}
