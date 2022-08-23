package com.blocksearch.sdk.data.domain.wallet.tx;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SDK on 2018. 4. 2..
 */

@Data
public class CoinUsTransactionInfo {

    public String hash;
    public long timestamp;
    public String blockNumber;
    public String confirmations;
    public boolean success;
    public String from;
    public String to;
    public String value;
    public String input;
    public String gasLimit;
    public String gasUsed;
    public List<CoinUsTransactionLog> logs = new ArrayList<>();
    public List<CoinUsTransaction> operations = new ArrayList<>();


//    public static CoinUsTransactionInfo getFromBlockAPIJson( JsonObject jo, String txHash ){
//        JsonObject data = CoinUsUtils.Json.getJsonObject( jo,"data");
//        if( null == data )
//            return getInvalidInfo( txHash );
//
//        JsonArray items = CoinUsUtils.Json.getJsonArray( data, "items" );
//        if( null == items )
//            return getInvalidInfo( txHash );
//
//        if( items.size() < 1 )
//            return getInvalidInfo( txHash );
//
//        JsonElement je = items.get(0);
//        JsonObject transactionInfo = je.getAsJsonObject();
//
//        CoinUsTransactionInfo coinUsTransactionInfo = new CoinUsTransactionInfo();
//        coinUsTransactionInfo.hash = CoinUsUtils.Json.getString(transactionInfo, "txHash");
//        coinUsTransactionInfo.timestamp = CoinUsUtils.Json.getLong(transactionInfo, "blockTimestamp");
//        coinUsTransactionInfo.blockNumber = CoinUsUtils.Json.getString(transactionInfo, "blockNumber");
//
//        long receiptStatus = CoinUsUtils.Json.getLong(transactionInfo, "txReceiptStatus");
//        coinUsTransactionInfo.success = CoinUsTransaction.isSuccessReceiptStatus( receiptStatus );
//
//        JsonObject from = CoinUsUtils.Json.getJsonObject(transactionInfo, "from");
//        coinUsTransactionInfo.from = CoinUsUtils.Json.getString(from, "address");
//
//        JsonObject to = CoinUsUtils.Json.getJsonObject(transactionInfo, "to");
//        coinUsTransactionInfo.to = CoinUsUtils.Json.getString(to, "address");
//
//        BigDecimal valueWei = new BigDecimal( CoinUsUtils.Json.getString(transactionInfo, "txValue") );
//        BigDecimal valueEth = CoinUsUtils.WeiToEth( valueWei );
//        coinUsTransactionInfo.value = valueEth.toString();
//
//        BigDecimal costWei = new BigDecimal( CoinUsUtils.Json.getString(transactionInfo, "txCost") );
//        BigDecimal costEth = CoinUsUtils.WeiToEth( costWei );
//        coinUsTransactionInfo.gasUsed = costEth.stripTrailingZeros().toString();
//
//        return coinUsTransactionInfo;
//    }


//    private static CoinUsTransactionInfo getInvalidInfo( String txHash ){
//        // 인덱스 서버에서 아직 정보가 내려오지 않은 상황이다.
//        CoinUsTransactionInfo coinUsTransactionInfo = new CoinUsTransactionInfo();
//        String strInvalid = "Invalid";
//        long longInvalid = 0;
//
//        coinUsTransactionInfo.hash = strInvalid;
//        coinUsTransactionInfo.timestamp = longInvalid;
//        coinUsTransactionInfo.blockNumber = strInvalid;
//        coinUsTransactionInfo.success = false;
//        coinUsTransactionInfo.from = strInvalid;
//        coinUsTransactionInfo.to = strInvalid;
//        coinUsTransactionInfo.value = "0";
//        coinUsTransactionInfo.gasUsed = "0";
//
//        if( null == txHash )
//            return coinUsTransactionInfo;
//
//        // 해당 트랜젝션 해쉬의 거래 기록이 남아있으면 그것으로 채운다. 아직 인덱스 서버에서 정보를 얻기 전.
//        List<CoinUsTransaction> result = CoinUsDataManager.getInstance().getCoinUsTransactionByTxHash( txHash );
//        if( null == result || result.size() == 0 ){
//            result = CoinUsDataManager.getInstance().getCoinUsPendingTokenTransactionByTxHash( txHash );
//            if( null == result || result.size() == 0 ){
//                return coinUsTransactionInfo;
//            }
//        }
//
//        CoinUsTransaction transaction = result.get(0);
//
//        coinUsTransactionInfo.hash = transaction.getHash();
//        if( CoinUsUtils.isStringNullOrEmpty( coinUsTransactionInfo.hash ) ){
//            coinUsTransactionInfo.hash = transaction.getTransactionHash();
//        }
//
//        coinUsTransactionInfo.timestamp = transaction.getTimestamp();
//        coinUsTransactionInfo.blockNumber = "-";
//        coinUsTransactionInfo.success = false;
//        coinUsTransactionInfo.from = transaction.getFrom();
//        coinUsTransactionInfo.to = transaction.getTo();
//        coinUsTransactionInfo.value = transaction.getValue();
//        coinUsTransactionInfo.gasUsed = "-";
//
//        return coinUsTransactionInfo;
//    }
}
