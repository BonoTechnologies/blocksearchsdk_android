package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SDK on 2018. 12. 18..
 */

@Data
public class WalletCurrencyPriceDomain {

    private String currency;
    private String currencyPrice;
    private List<WalletCryptoDomain> crypto = new ArrayList<>();

}