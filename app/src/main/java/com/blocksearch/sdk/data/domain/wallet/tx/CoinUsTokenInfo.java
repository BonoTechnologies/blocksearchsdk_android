package com.blocksearch.sdk.data.domain.wallet.tx;

import io.realm.RealmObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SDK on 2018. 4. 1..
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinUsTokenInfo extends RealmObject {

    private String address;
    private String name;
    private int decimals;
    private String symbol;
    private String totalSupply; // BigDecimal Type.
    private String owner;
    private int txsCount;
    private int transfersCount;
    private long lastUpdated;
    private int issuancesCount;
    private int holdersCount;
//    private String price;   // Price Object. Temporary change Object type as String. cause if price is null, it has boolean (false).
                            // https://github.com/EverexIO/Ethplorer/wiki/Ethplorer-API#get-address-transactions

}
