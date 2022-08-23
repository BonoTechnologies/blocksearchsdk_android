package com.blocksearch.sdk.core.wallet.ethereum;

import com.blocksearch.sdk.CLog;
import com.blocksearch.sdk.SearchConstants;
import com.blocksearch.sdk.core.wallet.CoinUsServiceWallet;
import com.blocksearch.sdk.core.wallet.ethereum.web3j.CoinUsWeb3jManager;
import com.blocksearch.sdk.data.CoinUsResponseCallback;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import io.reactivex.Flowable;

// Ether Send, Balance Etc.. Actual Ether Operation.
public abstract class EtherWalletBase implements CoinUsServiceWallet {

    private CoinUsWeb3jManager web3jManager;
//    private CoinUsDataManager dataManager;

    public EtherWalletBase() {
        web3jManager = new CoinUsWeb3jManager(SearchConstants.INFURA_URL);
    }

    public EtherWalletBase(String infuraApiUrl) {
        web3jManager = new CoinUsWeb3jManager(infuraApiUrl);
    }

    public BigInteger getNonce(String address) {
        return web3jManager.getNonce(address);
    }

    public String sendWeb3jTransaction(Function function, Credentials credentials, String contractAddress, BigInteger gasPrice, int gasLimit, BigInteger nonce) {
        return web3jManager.requestWeb3jTransaction(credentials, function, contractAddress, gasPrice, gasLimit, nonce);
    }

    public String sendWeb3jTransaction(Function function, Credentials credentials, String contractAddress, BigInteger value, BigInteger gasPrice, int gasLimit, BigInteger nonce) {
        String encodedFunction = null;
        if (function != null) {
            encodedFunction = FunctionEncoder.encode(function);
        }
        return web3jManager.requestWeb3jTransaction(credentials, encodedFunction, contractAddress, value, gasPrice, gasLimit, nonce);
    }

    public String sendWeb3jTransaction(String function, Credentials credentials, String contractAddress, BigInteger value, BigInteger gasPrice, int gasLimit, BigInteger nonce) {
        return web3jManager.requestWeb3jTransaction(credentials, function, contractAddress, value, gasPrice, gasLimit, nonce);
    }

    public BigInteger getGasPrice() {
        return web3jManager.getGasPrice();
    }

    public List<org.web3j.abi.datatypes.Type> sendWeb3jCall(Function function, String fromAddress, String toAddress) {
        return web3jManager.requestWeb3jEthCall(function, fromAddress, toAddress);
    }

    public BigInteger getBalanceTest(String address) {
        return web3jManager.requestWeb3jGetBalanceTest(address);
    }

    public BigInteger getBalanceFromWeb3j(String address) {
        return web3jManager.requestWeb3jGetBalanceTest(address);
    }

    @Override
    public String getBalanceAsync(String pubAddress, CoinUsResponseCallback callback) {
        return null;
    }

    @Override
    public BigInteger getBalance(String pubAddress) {
        return getBalance_ByWEB3J( pubAddress );
    }

    private BigInteger getBalance_ByWEB3J(String pubAddress){
        if (web3jManager != null) {
            return web3jManager.requestWeb3jGetBalance(pubAddress);
        } else {
            CLog.d("web3jManager is null");
        }
        return BigInteger.ZERO;
    }
//endregion

    public String sendEthBalance(ECKeyPair ecKeyPair, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, String data) {
        if (web3jManager != null && ecKeyPair != null) {

            Credentials credentials = Credentials.create(ECKeyPair.create(ecKeyPair.getPrivateKey()));
            CLog.d("Web3j credentials - address : " + credentials.getAddress());

            CLog.d("funds : " + funds);
            CLog.d("gasPrice : " + gasPrice);

//            return web3jManager.requestWeb3jSendBalance(receiverAddress, funds, gasPrice, credentials, gasLimit, data);
            return web3jManager.getWeb3jSendBalanceRawTx(receiverAddress, funds, gasPrice, credentials, gasLimit, data);
        } else {
            CLog.w("Web3jManager, ecKeyPair are null");
            return null;
        }
    }

//    public String sendEthErc20Balance(ECKeyPair ecKeyPair, CnusWalletToken cnusWalletToken, String contractAddress, String receiverAddress, int funds, BigInteger gasPrice) {
    public String sendEthErc20Balance(ECKeyPair ecKeyPair, int decimal, String contractAddress, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit) {
        if (web3jManager != null && ecKeyPair != null) {

            Credentials credentials = Credentials.create(ECKeyPair.create(ecKeyPair.getPrivateKey()));
            CLog.d("Web3j credentials - address : " + credentials.getAddress());

            return web3jManager.requestWeb3jERC20SendBalance(contractAddress, receiverAddress, funds, decimal, gasPrice, credentials, gasLimit);
        } else {
            CLog.w("Web3jManager, ecKeyPair are null");
            return null;
        }
    }

    // fixme (Giwung) Only For test
    public String sendEthErc20Balance(ECKeyPair ecKeyPair, int decimal, String contractAddress, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, BigInteger nonce) {
        if (web3jManager != null && ecKeyPair != null) {

            Credentials credentials = Credentials.create(ECKeyPair.create(ecKeyPair.getPrivateKey()));
            CLog.d("Web3j credentials - address : " + credentials.getAddress());

            return web3jManager.requestWeb3jERC20SendBalance(contractAddress, receiverAddress, funds, decimal, gasPrice, credentials, gasLimit, nonce);
        } else {
            CLog.w("Web3jManager, ecKeyPair are null");
            return null;
        }
    }

    public BigInteger getEthGasPrice() {
        if (web3jManager != null) {
            return web3jManager.requestWeb3jGetEthGasPrice();
        } else {
            CLog.w("Web3jManager is null");
        }
        return null;
    }

    public BigInteger getEstimateGas(String fromAddress, String toAddress, BigInteger weiAmount, String data) {
        return web3jManager.getEtherEstimateGas(fromAddress, toAddress, weiAmount, data);
    }

    public EthEstimateGas getEstimateGasSync(String fromAddress, String toAddress, String actualEther, String data) {
        return web3jManager.getEtherEstimateGasSync(fromAddress, toAddress, actualEther, data);
    }

    public BigInteger getEstimateGas(String fromAddress, String toAddress, String actualEther) {
        return web3jManager.getEtherEstimateGas(fromAddress, toAddress, actualEther);
    }

    public BigInteger getErc20EstimateGas(String fromAddress, String toAddress, String contractAddress, String actualToken, int tokenDecimals) {
        return web3jManager.getEtherContractEstimateGas(fromAddress, toAddress, contractAddress, actualToken, tokenDecimals);
    }

    public Flowable<Transaction> pendingTransactionFlowable() {
        return web3jManager.pendingTransactionFlowable();
    }

    public Flowable<String> ethPendingTransactionHashFlowable() {
        return web3jManager.ethPendingTransactionHashFlowable();
    }

    public String signEip712StructuredData(ECKeyPair ecKeyPair, String data) throws IOException, RuntimeException {
        return web3jManager.signEip712StructuredData(ecKeyPair, data);
    }

    public BigInteger getEtherContractEstimateGas(String function, String fromAddress, BigInteger value, String contractAddress) {
        return web3jManager.getEtherContractEstimateGas(function, fromAddress, value, contractAddress);
    }

    public EthEstimateGas getEtherContractEstimateGasSync(String function, String fromAddress, BigInteger value, String contractAddress) {
        return web3jManager.getEtherContractEstimateGasSync(function, fromAddress, value, contractAddress);
    }

    public String requestWeb3jTransactionRawTransaction(final Credentials credentials, String function, final String contractAddress, BigInteger value, final BigInteger gasPrice, final long gasLimit, final BigInteger nonce) {
        return web3jManager.requestWeb3jTransactionRawTransaction(credentials, function, contractAddress, value, gasPrice, gasLimit, nonce);
    }
}

