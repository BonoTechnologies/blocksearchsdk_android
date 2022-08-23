package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.Serializable;

import lombok.Data;

/**
 * SDK on 2018. 12. 10..
 */

@Data
public class EthTxAddress implements Serializable {

     private String address;
     private String ethAccountDcd;
     private String ethAccount;
     private String tokenContractYn;


}
