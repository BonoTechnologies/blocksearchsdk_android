package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

/**
 * SDK on 2019. 2. 1..
 */

@Data
public class UserWalletVisitHistoryDomain {

    private long seq;
    private long historyDt;
    private long uid;
    private int coinId;
    private long wno;

}