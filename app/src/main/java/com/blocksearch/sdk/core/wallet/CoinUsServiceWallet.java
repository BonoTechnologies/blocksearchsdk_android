package com.blocksearch.sdk.core.wallet;

import com.blocksearch.sdk.core.coins.CoinType;
import com.blocksearch.sdk.data.CoinUsResponseCallback;
import com.blocksearch.sdk.data.domain.wallet.cnus.WalletCryptoDomain;
import com.blocksearch.sdk.data.domain.wallet.cnus.WalletDomain;

import org.bitcoinj.crypto.DeterministicKey;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface CoinUsServiceWallet {

    String createCnusWalletAddress(DeterministicKey parentKey, int accountNo, int addressIndex);

    String getBalanceAsync(String pubAddress, CoinUsResponseCallback callback);

    BigInteger getBalance(String pubAddress);

    CoinType getCoinType();

    BigInteger getFee(String coinNm, String Symbol, int position);

    BigInteger getFeeOnNet(boolean coinYn, WalletCryptoDomain targetObject, String toAddress, String actualFund);

    BigDecimal getTotalPrice(String coinNm, String symbol, int position, BigInteger fee);

    String getCoinValue(String coinNm, String Symbol, BigDecimal totalPrice, String format);

    BigDecimal getSendBalanceFormat(String coinNm, String symbol, BigDecimal funds);

    boolean checkValidAddress(String coinNm, String symbol, String address);

    void checkBalanceHistory(DeterministicKey parentKey, final CoinUsResponseCallback<Boolean> callback);

    String sendBalance(DeterministicKey parentKey, Object targetObject, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, String data);

    String sendBalance(DeterministicKey parentKey, WalletDomain walletDomain, WalletCryptoDomain walletCryptoDomain, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, String data);

    String getCnusWalletAddressEachCoin(DeterministicKey parentKey, int accountNo, int addressIndex);
}
