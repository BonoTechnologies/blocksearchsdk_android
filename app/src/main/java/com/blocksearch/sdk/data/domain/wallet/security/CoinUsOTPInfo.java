package com.blocksearch.sdk.data.domain.wallet.security;

import com.theblockchain.coinus.wallet.common.CoinUsConstants;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * SDK on 2018. 1. 22..
 */

public class CoinUsOTPInfo extends RealmObject {

    @PrimaryKey
    @Index
    private int seq;
    private int accountNo;
    /**
     * OTP_USAGE_NOT_USING - 필수사용 : 키를 반으로 쪼갬
     * OTP_USAGE_OPTIONAL - 옵션사용 : 키를 앱에서 가지고있고 승인버튼만 사용
     * OTP_USAGE_FORCE - 사용안함 : OTP 안씀
     */
    private int option = CoinUsConstants.OTP_USAGE_NOT_USING; // OTP_USAGE_OPTIONAL, OTP_USAGE_FORCE
    private byte[] secureRnd;   // used for otp key. (test).

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public byte[] getSecureRnd() {
        return secureRnd;
    }

    public void setSecureRnd(byte[] secureRnd) {
        this.secureRnd = secureRnd;
    }
}
