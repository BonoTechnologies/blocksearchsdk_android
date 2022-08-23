package com.blocksearch.sdk.data.domain.wallet.address;

/**
 * Created by giwungeom on 2018. 4. 16..
 */


import lombok.Data;

@Data
public class UserAddressBookDomain {

    private long addressBookSeq;
    private long uid;
    private long coinId;
    private String coinDcd;
    private String coin;
//    private CoinDomain coinInfo;
    private String publicAddress;
    private long publicAddressUid;
    private String addressBookNm;
    private String addressBookMemo;

    private long createDt;
    private long updateDt;

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}