package com.blocksearch.sdk.data.domain.wallet.cnus;

import lombok.Data;

/**
 * SDK on 2018. 4. 19..
 */

@Data
public class UserKycDomain {

    private long seq;
    private long uid;
    private long kycNo;
    private KycDomain kyc;

    private long createDt;

}