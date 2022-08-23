package com.blocksearch.sdk.data;

import android.util.Pair;

import androidx.annotation.Nullable;


import com.blocksearch.sdk.data.domain.wallet.cnus.EthAddressDomain;
import com.blocksearch.sdk.data.domain.wallet.cnus.EthEstimateFee;
import com.blocksearch.sdk.data.domain.wallet.cnus.EthRbfDomain;
import com.blocksearch.sdk.data.domain.wallet.cnus.EthTxDomain;

import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CoinUsBoostsManager {

    private static CoinUsBoostsManager sInstance;

    synchronized public static CoinUsBoostsManager getInstance() {
        if (sInstance == null) {
            sInstance = new CoinUsBoostsManager();
        }
        return sInstance;
    }

    private CoinUsBoostsManager() {}


    public String getDisplayBalanceFromWei(BigInteger weiBalance, int decimal) throws Exception {
        return new BigDecimal(weiBalance).divide(BigDecimal.TEN.pow(decimal)).toPlainString();
    }

    public BigInteger getDisplayBalanceToWei(String displayBalance, int decimal) throws Exception{
        return new BigDecimal(displayBalance).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger();
    }

    public void getEthBoostsTransaction(final EthRbfDomain ethRbfDomain, final CoinUsResponseCallback<Pair<EthTxDomain, EthAddressDomain>> callback) {

        JobTask<Pair<EthTxDomain, EthAddressDomain>> jobTask = () -> {
//            1) 기존 수수료보다 30 % 높게 설정
//            2) 네트워크 수수료가 1)보다 클 경우 코인어스 네트워크 수수료  MID값 적용
            Pair<EthTxDomain, EthAddressDomain> pair = null;
            EthAddressDomain ethAddressDomain = null;
            BigDecimal networkGasPrice = BigDecimal.ZERO;

            EthTxDomain ethTxDomain = ethRbfDomain.getEthTxDomain();
            if (ethTxDomain != null && ethTxDomain.getTo() != null) {
                BigInteger gasLimit = BigInteger.valueOf(ethTxDomain.getTxGas());

                EthEstimateFee estimateFee = getEthEstimateFeeValue(CoinUsDataManager.getInstance().getEthEstimateGasPriceSync());
                CLog.d("gasLimit : " + gasLimit);
                CLog.d("estimateFee : " + (estimateFee != null ? estimateFee.getProposeGasPrice() : null));
                if (estimateFee != null) {
                    networkGasPrice = Convert.toWei(BigDecimal.valueOf(estimateFee.getProposeGasPrice()), Convert.Unit.GWEI);
                }

                if (estimateFee != null) {
                    BigDecimal gasPrice = ethTxDomain.getTxGasPrice();
                    CLog.d("gasPrice : " + gasPrice); // Gwei

                    ethTxDomain.setTxCost(gasPrice.multiply(new BigDecimal(ethTxDomain.getTxGas())));

                    BigDecimal boostsGasPrice = gasPrice.multiply(new BigDecimal("1.3"));

                    CLog.d("boostsGasPrice : " + boostsGasPrice + " estimateFee.getProposeGasPrice() : " + networkGasPrice);

                    if (boostsGasPrice.compareTo(networkGasPrice) > 0) {
                        ethTxDomain.setTxGasPriceBoosts(boostsGasPrice);

                    } else {
                        ethTxDomain.setTxGasPriceBoosts(networkGasPrice);
                    }
                } else {
                    return null;
                }

                Map<String, Object> params = new HashMap<>();
                String currency = CoinUsPrefManager.getCurCurency(CoinUsApplication.getInstance().getApplicationContext());
                params.put("currency", currency);

                ethAddressDomain = CoinUsDataManager.getInstance().getEthAddressSyncWithJson(ethTxDomain.getFrom().getAddress(), params);

                pair = new Pair<>(ethTxDomain, ethAddressDomain);
            }

            return pair;
        };

        BoostsThread<Pair<EthTxDomain, EthAddressDomain>> thread = new BoostsThread<>(jobTask, callback);
        thread.start();
    }

    private @Nullable EthEstimateFee getEthEstimateFeeValue(CoinUsResponse<EthEstimateFee> coinUsResponse) {
        if (coinUsResponse != null && coinUsResponse.getData() != null) {
            CoinUsDataParse dataParse = new CoinUsDataParse();
            List<EthEstimateFee> resultDomains = dataParse.getDataItems(coinUsResponse, EthEstimateFee.class);

            if (resultDomains != null && resultDomains.size() > 0) {
                EthEstimateFee ethEstimateFee = resultDomains.get(0);
                if (ethEstimateFee != null) {
                    CLog.d("ethEstimateFee safeGasPrice : " + ethEstimateFee.getSafeGasPrice());
                    CLog.d("ethEstimateFee.proposeGasPrice() : " + ethEstimateFee.getProposeGasPrice());
                    CLog.d("ethEstimateFee.fastGasPrice() : " + ethEstimateFee.getFastGasPrice());
                    return ethEstimateFee;
                }
            } else {
                CLog.w("coinUsResponse is null");
            }
        }
        return null;
    }

    public void sendTransaction(EthTxDomain ethTxDomain, int walletAddressIndex, final CoinUsResponseCallback<EthereumCoinSendResult> callback) {

        JobTask<EthereumCoinSendResult> jobTask = () -> {
//            1) 기존 수수료보다 30 % 높게 설정
//            2) 네트워크 수수료가 1)보다 클 경우 코인어스 네트워크 수수료  MID값 적용

            String encryptedSeed ;
            CoinUsWallet wallet = CoinUsWallet.getInstance();
            CoinUsAccount account = wallet.getCurrentAccount();

            if (CoinUsOTPManager.getInstance().getOTPUsageOption() == CoinUsConstants.OTP_USAGE_FORCE) {
                // get half
                encryptedSeed = wallet.getEncryptedSeedWithKeeperWithValidation(account);
                CoinUsApplication.getInstance().eraseRandomData();
                if(encryptedSeed == null) {
                    return null;
                }
            } else {
                encryptedSeed = account.getWalletSeed();
            }
            // get EncryptedSeed

            String balance;
            String paramBalance;
            String inputData = ethTxDomain.getTxInput();
            BigInteger nonce = BigInteger.valueOf(ethTxDomain.getTxNonce());
            String toAddress;
            String contractAddress;
            boolean isCoinType = false;
            String rawTransaction = null;

            CLog.w("inputData : " + inputData);

            WalletHDEtherPocket etherPocket = (WalletHDEtherPocket) CoinUsWallet.getInstance().getWalletPocket(new EthereumCoins());
            Credentials credentials = wallet.getCredentials(encryptedSeed, walletAddressIndex, new EthereumCoins());

            if (ethTxDomain.getTo().getTokenContractYn().equalsIgnoreCase("Y")) {
                if (ethTxDomain.getTokenTransfers() != null && ethTxDomain.getTokenTransfers().size() > 0) {
                    balance = "0";
                    toAddress = ethTxDomain.getTokenTransfers().get(0).getContractAddress();
                    contractAddress = ethTxDomain.getTokenTransfers().get(0).getContractAddress();
                    paramBalance = ethTxDomain.getTokenTransfers().get(0).getTransferValue();
                } else {
                    CLog.w("ethTxDomain.getTokenTransfers() is null");
                    return null;
                }
            } else {
                balance = ethTxDomain.getTxValue();
                paramBalance = balance;
                toAddress = ethTxDomain.getTo().getAddress();
                contractAddress = null;
                isCoinType = true;
            }

            try {
                // Raw Hex Value 가져옴.

                rawTransaction = etherPocket.requestWeb3jTransactionRawTransaction(credentials, inputData, toAddress, new BigInteger(balance), ethTxDomain.getTxGasPriceBoosts().toBigInteger(), (int) ethTxDomain.getTxGas(), nonce);

                CLog.i("transactionHash :: " + rawTransaction);

                // Raw Hex Value를 CoinUs에 전송함.
                Map<String, Object> params = new HashMap<>();
                params.put("hex", rawTransaction);
                params.put("txNonce", nonce);
                params.put("from", credentials.getAddress());
                params.put("to", isCoinType ? toAddress : ethTxDomain.getTokenTransfers().get(0).getTransferTo());
                params.put("contractAddress", contractAddress);
                params.put("txValue", paramBalance);
                params.put("txGasPrice", ethTxDomain.getTxGasPriceBoosts());
                params.put("txGas", ethTxDomain.getTxGas());
                params.put("txInput", ethTxDomain.getTxInput());
                params.put("walletConnectYn", "N");

                EthereumCoinSendResult coinSendResult = CoinUsDataManager.getInstance().sendEthAddressRawTransactions(credentials.getAddress(), params);
                if (coinSendResult != null) {
                    return coinSendResult;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        BoostsThread<EthereumCoinSendResult> thread = new BoostsThread<>(jobTask, callback);
        thread.start();
    }

    private static class BoostsThread<T> extends Thread {

        private final JobTask<T> jobTask;
        private final CoinUsResponseCallback<T> callback;

        public BoostsThread(JobTask<T> jobTask, CoinUsResponseCallback<T> callback) {
            this.jobTask = jobTask;
            this.callback = callback;
        }

        @Override
        public void run() {
            super.run();
            T result = jobTask.task();
            if (result != null) {
                callback.onResultFetched(result);
            } else {
                callback.onResultFailed(404, "");
            }
        }
    }

    private interface JobTask<T> {
        T task();
    }

}
