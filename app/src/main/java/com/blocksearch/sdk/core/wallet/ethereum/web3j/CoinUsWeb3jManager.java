package com.blocksearch.sdk.core.wallet.ethereum.web3j;

import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;


import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.StructuredDataEncoder;
import org.web3j.exceptions.MessageDecodingException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;


public class CoinUsWeb3jManager {

    private Web3j mWeb3;
    private final int THREAD_COUNT_LIMIT = 5;
    private ExecutorService mExecutor;

    public CoinUsWeb3jManager(String network) {
        final long TIME_OUT = 20;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout( TIME_OUT, TimeUnit.SECONDS )
                .readTimeout( TIME_OUT, TimeUnit.SECONDS )
                .writeTimeout( TIME_OUT, TimeUnit.SECONDS )
                .build();

        mWeb3 = Web3j.build( new HttpService( network, client, false ) );
        mExecutor = Executors.newFixedThreadPool(THREAD_COUNT_LIMIT);
    }

    public BigInteger getNonce(final String address) {
        Request<?, EthGetTransactionCount> request = mWeb3.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING);
//        Request<?, EthGetTransactionCount> request = mWeb3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST);

        try {
            EthGetTransactionCount transaction = request.send();
            if (transaction != null) {
                if (transaction.getError() != null) {
                    return null;
                }

                return transaction.getTransactionCount();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessageDecodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void requestWeb3jVersion(final CallbackResult callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Future<Web3ClientVersion> futureClientVersion = mWeb3.web3ClientVersion().sendAsync();
                try {
                    Web3ClientVersion web3ClientVersion = futureClientVersion.get();
                    if (callback != null) {
                        List<String> result = new ArrayList();
                        result.add(web3ClientVersion.getWeb3ClientVersion());
                        callback.resultFetched(result);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public BigInteger requestWeb3jGetBalance(final String address) {
        Request<?, EthGetBalance> balanceRequest = mWeb3.ethGetBalance(address, new DefaultBlockParameter() {
            @Override
            public String getValue() {
                return "latest";
            }
        });


        try {
            EthGetBalance ethBalance = balanceRequest.send();

            return ethBalance.getBalance();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessageDecodingException e) {
            e.printStackTrace();
        }

        return BigInteger.ZERO;
    }

    public BigInteger requestWeb3jGetBalanceTest(final String address) {
            Request<?, EthGetBalance> balanceRequest = mWeb3.ethGetBalance(address, new DefaultBlockParameter() {
//                Request<?, EthGetBalance> balanceRequest = mWeb3.ethGetBalance("0x3452932349Aac7D249640974C8D38948205ba728", new DefaultBlockParameter() {

                @Override
                public String getValue() {
                    return "latest";
                }
            });
            try {
                EthGetBalance ethBalance = balanceRequest.send();
                return ethBalance.getBalance();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessageDecodingException e) {
                e.printStackTrace();
            }

            return null;
    }

    public BigInteger requestWeb3jGetEthGasPrice() {
        try {
            Request<?, EthGasPrice> ethGasPriceRequest = mWeb3.ethGasPrice();
            EthGasPrice ethGasPrice = ethGasPriceRequest.send();
            BigInteger gas = ethGasPrice.getGasPrice();
            return Convert.fromWei(new BigDecimal(gas), Convert.Unit.GWEI).toBigInteger();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static final long POLLING_FREQUENCY = 15000;
    private static final int COUNT = 1;  // don't set too high if using a real Ethereum network

    public String requestWeb3jSendBalance(final String receiverAddress,
                                          final BigDecimal fundsOnWei,
                                          final BigInteger gasPrice,
                                          final Credentials credentials,
                                          final long gasLimit,
                                          final String data) {

        Map<String, Object> pendingTransactions = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                mWeb3, credentials,

                new QueuingTransactionReceiptProcessor(mWeb3, new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt != null) {
                        }
                        transactionReceipts.add(transactionReceipt);
                    }

                    @Override
                    public void exception(Exception exception) {

                    }
                }, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, POLLING_FREQUENCY));

        //Transfer transfer = new Transfer(mWeb3, transactionManager);

        try {
            EthSendTransaction transactionResponse = transactionManager.sendTransaction(gasPrice, new BigInteger(Long.toString(gasLimit)), receiverAddress, data != null ? data : "", fundsOnWei.toBigInteger());
            String transactionHash = transactionResponse.getTransactionHash();
            if (transactionHash == null) {
                Response.Error error = transactionResponse.getError();
                return null;
            } else {
                return transactionHash;
            }

//            TransactionReceipt transactionReceipt = createTransaction(transfer, fundsOnWei, receiverAddress, gasPrice, gasLimit).send();
//            pendingTransactions.put(transactionReceipt.getTransactionHash(), new Object());
//            CLog.d("transactionReceipt:: hash " + transactionReceipt.getTransactionHash());
//            return transactionReceipt.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getWeb3jSendBalanceRawTx(final String receiverAddress,
                                          final BigDecimal fundsOnWei,
                                          final BigInteger gasPrice,
                                          final Credentials credentials,
                                          final long gasLimit,
                                          final String data) {

        try {
            BigInteger nonce = getNonce(credentials.getAddress());

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    BigInteger.valueOf(gasLimit),
                    receiverAddress,
                    fundsOnWei.toBigInteger(),
                    data != null ? data : "");

            RawTransactionManager rawTransactionManager = new RawTransactionManager(mWeb3, credentials);
            return rawTransactionManager.sign(rawTransaction);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private RemoteCall<TransactionReceipt> createTransaction(Transfer transfer, final BigDecimal fundsOnWei, String receiveAddress, BigInteger gasPrice, long gasLimit) {
        return transfer.sendFunds(
                receiveAddress, fundsOnWei, Convert.Unit.WEI,
                gasPrice, new BigInteger(Long.toString(gasLimit)));
    }

    public String requestWeb3jERC20SendBalance(final String contractAddress, final String receiverAddress, final BigDecimal tokenValue, final int decimal, final BigInteger gasPrice, final Credentials credentials, final long gasLimit) {

        try {
            BigInteger tokenFund = tokenValue.toBigInteger();

            Function function = new Function("transfer", Arrays.<Type>asList(new Address(receiverAddress), new Uint256(tokenFund)), Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            BigInteger nonce = getNonce(credentials.getAddress());

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    BigInteger.valueOf(gasLimit),
                    contractAddress,
                    BigInteger.ZERO,
                    encodedFunction);

            RawTransactionManager rawTransactionManager = new RawTransactionManager(mWeb3, credentials);
            return rawTransactionManager.sign(rawTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // fixme (Giwung) Only for test.
    public String requestWeb3jERC20SendBalance(final String contractAddress, final String receiverAddress, final BigDecimal tokenValue, final int decimal, final BigInteger gasPrice, final Credentials credentials, final long gasLimit, BigInteger nonce) {

        Map<String, Object> pendingTransactions = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                mWeb3, credentials,

                new QueuingTransactionReceiptProcessor(mWeb3, new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt != null) {
                        }
                        transactionReceipts.add(transactionReceipt);
                    }

                    @Override
                    public void exception(Exception exception) {

                    }
                }, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, POLLING_FREQUENCY));

        try {
//            BigInteger tokenFund = getTokenUnit256(String.valueOf(tokenValue), decimal);
            BigInteger tokenFund = tokenValue.toBigInteger();

            Function function = new Function("transfer", Arrays.<Type>asList(new Address(receiverAddress), new Uint256(tokenFund)), Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    new BigInteger(Long.toString(gasLimit)),
                    contractAddress,
                    BigInteger.ZERO,
                    encodedFunction);

//            EthSendTransaction transactionResponse = transactionManager.sendTransaction(gasPrice, new BigInteger(Long.toString(gasLimit)), contractAddress, encodedFunction, BigInteger.ZERO);
            EthSendTransaction transactionResponse = transactionManager.signAndSend(rawTransaction);

            String transactionHash = transactionResponse.getTransactionHash();

            if (transactionHash == null) {
                Response.Error error = transactionResponse.getError();
                return null;
            } else {
                return transactionHash;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Bancor Method Start
     */
    // https://theethereum.wiki/w/index.php/ERC20_Token_Standard#Approve_And_TransferFrom_Token_Balance
    public boolean requestWeb3jERC20Approve(final String contractAddress, final String receiverAddress, final BigDecimal tokenValue, final int decimal, final BigInteger gasPrice, final Credentials credentials, final long gasLimit) {

        final ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                mWeb3, credentials,

                new QueuingTransactionReceiptProcessor(mWeb3, new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt != null) {
                        }
                        transactionReceipts.add(transactionReceipt);
                    }

                    @Override
                    public void exception(Exception exception) {

                    }
                }, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, POLLING_FREQUENCY));

        try {
            BigInteger tokenFund = tokenValue.toBigInteger();

            Function function = new Function("approve", Arrays.<Type>asList(new Address(receiverAddress), new Uint256(tokenFund)), Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            EthSendTransaction transactionResponse = transactionManager.sendTransaction(gasPrice, new BigInteger(Long.toString(gasLimit)), contractAddress, encodedFunction, BigInteger.ZERO);

            String transactionHash = transactionResponse.getTransactionHash();

            if (transactionHash == null) {
                Response.Error error = transactionResponse.getError();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String requestWeb3jTransaction(final Credentials credentials, Function function, final String contractAddress, final BigInteger gasPrice, final long gasLimit, final BigInteger nonce) {
        String encodedFunction = null;
        if (function != null) {
            encodedFunction = FunctionEncoder.encode(function);
        }
        return requestWeb3jTransaction(credentials, encodedFunction, contractAddress, BigInteger.ZERO, gasPrice, gasLimit, nonce);
    }

//    public String requestWeb3jTransaction(final Credentials credentials, Function function, final String contractAddress, BigInteger value, final BigInteger gasPrice, final long gasLimit, final BigInteger nonce) {
    public String requestWeb3jTransaction(final Credentials credentials, String function, final String contractAddress, BigInteger value, final BigInteger gasPrice, final long gasLimit, final BigInteger nonce) {
        String encodedFunction = function;

        final ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                mWeb3, credentials,

                new QueuingTransactionReceiptProcessor(mWeb3, new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt != null) {
                        }
                        transactionReceipts.add(transactionReceipt);
                    }

                    @Override
                    public void exception(Exception exception) {

                    }
                }, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, POLLING_FREQUENCY));

        try {

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    new BigInteger(Long.toString(gasLimit)),
                    contractAddress,
                    value,
                    encodedFunction == null ? "" : encodedFunction);

            EthSendTransaction transactionResponse = transactionManager.signAndSend(rawTransaction);

            String transactionHash = transactionResponse.getTransactionHash();

            if (transactionHash == null) {
                Response.Error error = transactionResponse.getError();
                return null;
            } else {
                return transactionHash;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String requestWeb3jTransactionRawTransaction(final Credentials credentials, String function, final String contractAddress, BigInteger value, final BigInteger gasPrice, final long gasLimit, final BigInteger nonce) {
        String encodedFunction = function;

        final ConcurrentLinkedQueue<TransactionReceipt> transactionReceipts =
                new ConcurrentLinkedQueue<>();

        FastRawTransactionManager transactionManager = new FastRawTransactionManager(
                mWeb3, credentials,

                new QueuingTransactionReceiptProcessor(mWeb3, new Callback() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) {
                        if (transactionReceipt != null) {
                        }
                        transactionReceipts.add(transactionReceipt);
                    }

                    @Override
                    public void exception(Exception exception) {

                    }
                }, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH, POLLING_FREQUENCY));

        try {

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    new BigInteger(Long.toString(gasLimit)),
                    contractAddress,
                    value,
                    encodedFunction == null ? "" : encodedFunction);


            RawTransactionManager rawTransactionManager = new RawTransactionManager(mWeb3, credentials);
            return rawTransactionManager.sign(rawTransaction);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Type> requestWeb3jEthCall(Function function, String fromAddress, String contractAddress) {
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            org.web3j.protocol.core.methods.response.EthCall ethCall = mWeb3.ethCall(
                    Transaction.createEthCallTransaction(
                            fromAddress, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST)
                    .send();

            String value = ethCall.getValue();

            Response.Error error = ethCall.getError();
            if (error != null) {
            }

            return FunctionReturnDecoder.decode(value, function.getOutputParameters());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigInteger getEtherContractEstimateGas(String function, String fromAddress, BigInteger value, String contractAddress) {

        try {
            Transaction contractTransaction = new Transaction(
                    fromAddress,
                    null,
                    null,
                    null,
                    contractAddress,
                    value,
                    function);

            Future<EthEstimateGas> ethEstimateGasResponse = mWeb3.ethEstimateGas(contractTransaction).sendAsync();
            EthEstimateGas ethEstimateGas = ethEstimateGasResponse.get();
            if (ethEstimateGas != null) {
                if (!ethEstimateGas.hasError()) {
                    return ethEstimateGas.getAmountUsed();
                } else {
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EthEstimateGas getEtherContractEstimateGasSync(String function, String fromAddress, BigInteger value, String contractAddress) {

        try {
            Transaction contractTransaction = new Transaction(
                    fromAddress,
                    null,
                    null,
                    null,
                    contractAddress,
                    value,
                    function);

            Future<EthEstimateGas> ethEstimateGasResponse = mWeb3.ethEstimateGas(contractTransaction).sendAsync();
            return ethEstimateGasResponse.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigInteger getGasPrice() {

        try {
            Future<EthGasPrice> ethEstimateGasResponse = mWeb3.ethGasPrice().sendAsync();
            EthGasPrice ethEstimateGas = ethEstimateGasResponse.get();
            if (ethEstimateGas != null) {
                if (!ethEstimateGas.hasError()) {
                    return ethEstimateGas.getGasPrice();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new BigInteger("11000000000");
    }

    private BigInteger getTokenUnit256(String tokenValue, int decimal) {
//        BigInteger value = new BigInteger(tokenValue);
//        BigInteger tokenDecimal = BigInteger.TEN.pow(decimal);
//        return value.multiply(tokenDecimal);
        BigDecimal value = new BigDecimal(tokenValue);
        BigDecimal tokenDecimal = BigDecimal.TEN.pow(decimal);
        return value.multiply(tokenDecimal).toBigInteger();
    }


    /**
     ******************* For Test. ******************
     * Start
     */
    // Error.  Invalid response received: okhttp3.internal.http.RealResponseBody
    // https://github.com/web3j/web3j/issues/366
    // infura not supporting
//    public void newBlockChain() {
//        Subscription subscription = mWeb3.blockObservable(false).subscribe(
//                new Action1<EthBlock>() {
//                    @Override
//                    public void call(EthBlock ethBlock) {
//                        EthBlock.Block block = ethBlock.getBlock();
//                        CLog.d("New Block Arrived Hash : " + block.getHash());
//                    }
//                }
//        );
//    }

    // Error.  Invalid response received: okhttp3.internal.http.RealResponseBody
    // https://github.com/web3j/web3j/issues/366
    // infura not supporting
//    public void allPendingTransaction() {
//        Subscription subscription = mWeb3.pendingTransactionObservable().subscribe(
//                new Action1<org.web3j.protocol.core.methods.response.Transaction>() {
//                    @Override
//                    public void call(org.web3j.protocol.core.methods.response.Transaction transaction) {
//                        CLog.d("new Pending Tx Arrived : to - " + transaction.getTo());
//                    }
//                }
//        );
//    }

    public void gasPriceTest() {
        // Pick gas price from geth console
        try {
            Request<?, EthGasPrice> gasPriceRequest = mWeb3.ethGasPrice();
            Future<EthGasPrice> ethGasPrice = gasPriceRequest.sendAsync();
            EthGasPrice gasPrice = ethGasPrice.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // ether 코인 Estimate Gase.

    /**
     *
     * @param fromAddress ether Address
     * @param toAddress ether Address
     * @param actualEther String Ether Unit.
     * @return
     */
    public BigInteger getEtherEstimateGas(String fromAddress, String toAddress, String actualEther) {
        try {
            final BigDecimal fundsOnWei = Convert.toWei(actualEther, Convert.Unit.ETHER);

            Transaction transaction = new Transaction(fromAddress, null, null, null, toAddress, fundsOnWei.toBigInteger(), null);
            Request<?, EthEstimateGas> ethEstimateGas = mWeb3.ethEstimateGas(transaction);
            Future<EthEstimateGas> estimateGas = ethEstimateGas.sendAsync();
            if (estimateGas != null) {
                EthEstimateGas eg = estimateGas.get();
                if (!eg.hasError()) {
                    return eg.getAmountUsed();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigInteger getEtherEstimateGas(String fromAddress, String toAddress, String actualEther, String data) {
        try {
            final BigDecimal fundsOnWei = Convert.toWei(actualEther, Convert.Unit.ETHER);

            Transaction transaction = new Transaction(fromAddress, null, null, null, toAddress, fundsOnWei.toBigInteger(), data);
            Request<?, EthEstimateGas> ethEstimateGas = mWeb3.ethEstimateGas(transaction);
            Future<EthEstimateGas> estimateGas = ethEstimateGas.sendAsync();
            if (estimateGas != null) {
                EthEstimateGas eg = estimateGas.get();
                if (!eg.hasError()) {
                    return eg.getAmountUsed();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigInteger getEtherEstimateGas(String fromAddress, String toAddress, BigInteger weiAmount, String data) {
        try {

            Transaction transaction = new Transaction(fromAddress, null, null, null, toAddress, weiAmount, data);
            Request<?, EthEstimateGas> ethEstimateGas = mWeb3.ethEstimateGas(transaction);
            Future<EthEstimateGas> estimateGas = ethEstimateGas.sendAsync();
            if (estimateGas != null) {
                EthEstimateGas eg = estimateGas.get();
                if (!eg.hasError()) {
                    return eg.getAmountUsed();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public EthEstimateGas getEtherEstimateGasSync(String fromAddress, String toAddress, String actualEther, String data) {
        try {
            final BigDecimal fundsOnWei = Convert.toWei(actualEther, Convert.Unit.ETHER);

            Transaction transaction = new Transaction(fromAddress, null, null, null, toAddress, fundsOnWei.toBigInteger(), data);
            Request<?, EthEstimateGas> ethEstimateGas = mWeb3.ethEstimateGas(transaction);
            Future<EthEstimateGas> estimateGas = ethEstimateGas.sendAsync();
            if (estimateGas != null) {
                return estimateGas.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }


    // CNUS 호출 됨.
    public BigInteger getEtherContractEstimateGas(String fromAddress, String toAddress, String contractAddress, String actualToken, int tokenDecimals) {

        BigInteger tokenFund = BigInteger.ONE;

        try {
            tokenFund = getTokenUnit256(String.valueOf(actualToken), tokenDecimals);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Function function = new Function("transfer", Arrays.<Type>asList(new Address(toAddress), new Uint256(tokenFund)), Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            Transaction contractTransaction = new Transaction(
                    fromAddress,
                    null,
                    null,
                    null,
                    contractAddress,
                    BigInteger.ZERO,
                    encodedFunction);

            Future<EthEstimateGas> ethEstimateGasResponse = mWeb3.ethEstimateGas(contractTransaction).sendAsync();
            EthEstimateGas ethEstimateGas = ethEstimateGasResponse.get();
            if (ethEstimateGas != null) {
                if (!ethEstimateGas.hasError()) {
                    return ethEstimateGas.getAmountUsed();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void checkCode(String apiEtherTestAddress) {
        Request<?, EthGetCode>  ethGetCodeRequest = mWeb3.ethGetCode(apiEtherTestAddress, new DefaultBlockParameter() {
            @Override
            public String getValue() {
                return null;
            }
        });
        Future<EthGetCode> dafsas = ethGetCodeRequest.sendAsync();
        try {
            EthGetCode ethGetCode = dafsas.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String signEip712StructuredData(ECKeyPair ecKeyPair, String message) throws IOException, RuntimeException {
        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(message);

        byte[] hashStructuredData = dataEncoder.hashStructuredData();
        byte[] sig = messageSign(ecKeyPair, hashStructuredData, false);
        return Numeric.toHexString(sig);
    }

    private byte[] messageSign(ECKeyPair ecKeyPair, byte[] label, boolean needToHash) {
        ByteBuffer buffer = ByteBuffer.allocate(label.length);
        buffer.put(label);
        byte[] array = buffer.array();
        Sign.SignatureData signature = Sign.signMessage(array, ecKeyPair, needToHash);

        return getSignatureDataToByte(signature);
    }

    private byte[] getSignatureDataToByte(Sign.SignatureData signature) {
        ByteBuffer sigBuffer = ByteBuffer.allocate(signature.getR().length + signature.getS().length + 1);
        sigBuffer.put(signature.getR());
        sigBuffer.put(signature.getS());
        sigBuffer.put(signature.getV());
        return sigBuffer.array();
    }

    /**
     ******************* For Test. ******************
     * End.
     */

    public Flowable<org.web3j.protocol.core.methods.response.Transaction> pendingTransactionFlowable() {
        return mWeb3.pendingTransactionFlowable();
    }

    public Flowable<String> ethPendingTransactionHashFlowable() {
        return mWeb3.ethPendingTransactionHashFlowable();
    }

    public interface CallbackResult<T> {
        void resultFetched(List<T> value);
    }
}
