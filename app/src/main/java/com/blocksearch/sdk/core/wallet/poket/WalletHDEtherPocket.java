package com.blocksearch.sdk.core.wallet.poket;

import com.blocksearch.sdk.CLog;
import com.blocksearch.sdk.DomainCodes;
import com.blocksearch.sdk.core.bip39.Mnemonic;
import com.blocksearch.sdk.core.coins.CoinType;
import com.blocksearch.sdk.core.coins.ethereum.EthereumCoins;
import com.blocksearch.sdk.core.wallet.SimpleHDKeyChain;
import com.blocksearch.sdk.core.wallet.ethereum.EtherWalletBase;
import com.blocksearch.sdk.data.CoinUsResponseCallback;
import com.blocksearch.sdk.data.domain.wallet.cnus.WalletCryptoDomain;
import com.blocksearch.sdk.data.domain.wallet.cnus.WalletDomain;

import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WalletHDEtherPocket extends EtherWalletBase {

    private CoinType coinType;

    public WalletHDEtherPocket(CoinType coinType) {
        super();
        this.coinType = coinType;
        CLog.d("********************************* Pocket Wallet Generate ***********************************************");
    }

    public WalletHDEtherPocket(CoinType coinType, String infuraApiUrl) {
        super(infuraApiUrl);
        this.coinType = coinType;
//        CLog.d("********************************* Pocket Wallet Generate ***********************************************");
    }

    @Override
    public String createCnusWalletAddress(DeterministicKey parentKey, int accountNo, int addressIndex) {
        CLog.e("Called");
        SimpleHDKeyChain keys = new SimpleHDKeyChain(parentKey, addressIndex);
        Credentials credentials = Credentials.create(ECKeyPair.create(keys.getECKeyPair().getPrivateKey()));
        return credentials.getAddress();
    }

    @Override
    public String getCnusWalletAddressEachCoin(DeterministicKey rootKey, int accountNo, int addressIndex) {
        CLog.e("Called");
        SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, addressIndex);
        Credentials credentials = Credentials.create(ECKeyPair.create(keys.getECKeyPair().getPrivateKey()));

        return credentials.getAddress();
    }

    /**
     * @return Nonce.
     */
    public BigInteger requestApprove(Credentials ownerCredentials, String toAddress, Function function, BigInteger gasPrice, BigInteger gasLimit) {

        BigInteger nonce = getNonce(ownerCredentials.getAddress());

        String result = sendWeb3jTransaction(
                function,
                ownerCredentials,
                toAddress,
                gasPrice,
//                gasLimit.add(BigInteger.valueOf(10000)).intValue(),
                gasLimit.intValue(),
                nonce
        );

        if (result != null) {
            return nonce;
        } else {
            return null;
        }
    }

    @Override
    public String sendBalance(DeterministicKey parentKey, WalletDomain walletDomain, WalletCryptoDomain walletCryptoDomain, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, String data) {
        int addressIndex = walletDomain.getWalletAddressIndex();
        CLog.d("TargetAddress Index : " + addressIndex);
        if (addressIndex < 0) {
            return null;
        }

        DeterministicHierarchy hierarchy = new DeterministicHierarchy(parentKey);
        DeterministicKey rootKey = hierarchy.get(coinType.getBip44Path(0), false, true);
        SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, addressIndex);

        if (walletCryptoDomain.getCryptoTypeDcd().equals(DomainCodes.CRYPTO_TYPE_COIN.getDcd())) {
            return sendEthBalance(keys.getECKeyPair(), receiverAddress, funds, gasPrice, gasLimit, data);
        } else {
            return sendEthErc20Balance(keys.getECKeyPair(), walletCryptoDomain.getTokenDecimals(), walletCryptoDomain.getContractAddress(), receiverAddress, funds, gasPrice, gasLimit);
        }
    }

    @Override
    public String sendBalance(DeterministicKey parentKey, Object targetObject, String receiverAddress, BigDecimal funds, BigInteger gasPrice, long gasLimit, String data) {
        return null;
    }

    @Override
    public void checkBalanceHistory(DeterministicKey parentKey, final CoinUsResponseCallback<Boolean> callback) {
        // getAddress, with AddressIndex.
//        BigInteger balance;
        int addressIndex = 0;
        CoinUsDataManager dataManager = CoinUsDataManager.getInstance();
        // get AddressIndex;
        CLog.i("--------- Balance History Check Start ---------");
        do {
            CLog.i("Checking History...");
            SimpleHDKeyChain keys = new SimpleHDKeyChain(parentKey, addressIndex);
            Credentials credentials = Credentials.create(ECKeyPair.create(keys.getECKeyPair().getPrivateKey()));
            String walletAddress = credentials.getAddress();
            CLog.i("finding .. AddressIndex : " + addressIndex + " address : " + walletAddress);
//            balance = super.getBalance(walletAddress);

            if (CoinUsDataManager.getInstance().checkEthAddressHistorySync(walletAddress)) {
                Map<String, Object> insertWalletParams = new HashMap<>();
                insertWalletParams.put("coinId", coinType.getCoinId());
                insertWalletParams.put("walletAddress", walletAddress);
                insertWalletParams.put("walletAddressIndex", addressIndex);
                insertWalletParams.put("walletAddressNm", "");
                CLog.i("sending.. AddressIndex : " + addressIndex + " address : " + credentials.getAddress());
                dataManager.insertWallet(insertWalletParams);
            } else {
                break;
            }

            addressIndex++;

        } while (true);
        CLog.i("--------- Balance History Check Finished ---------");
        callback.onResultFetched(true);
    }

    @Override
    public CoinType getCoinType() {
        return coinType;
    }

    @Override
    public BigInteger getFeeOnNet(boolean coinYn, WalletCryptoDomain walletCryptoDomain, String toAddress, String actualFund) {
        if (coinYn) {
            //String fromAddress, String toAddress, String actualEther
            return super.getEstimateGas(walletCryptoDomain.getWalletAddress(), toAddress, actualFund);
        } else {
            return super.getErc20EstimateGas(
                    walletCryptoDomain.getWalletAddress(),
                    toAddress,
                    walletCryptoDomain.getContractAddress(),
                    actualFund,
                    walletCryptoDomain.getTokenDecimals()
            );
        }
    }

    @Override
    public BigInteger getFee(String coinNm, String symbol, int position) {
        return Convert.toWei(String.valueOf(position), Convert.Unit.GWEI).toBigInteger();
    }

    @Override
    public BigDecimal getTotalPrice(String coinNm, String symbol, int position, BigInteger fee) {
        BigDecimal returnVal = new BigDecimal(fee).multiply(Convert.toWei(String.valueOf(position), Convert.Unit.GWEI));
        return returnVal.setScale(12, RoundingMode.FLOOR);
    }

    @Override
    public String getCoinValue(String coinNm, String symbol, BigDecimal totalPrice, String format) {
        return String.format(format, Convert.fromWei(totalPrice, Convert.Unit.ETHER).doubleValue());

    }

    @Override
    public BigDecimal getSendBalanceFormat(String coinNm, String symbol, BigDecimal funds) {
        return Convert.toWei(funds, Convert.Unit.ETHER);

    }

    @Override
    public boolean checkValidAddress(String coinNm, String coinSymbol, String address) {
        boolean result = false;
        do {
            if ((false == Numeric.containsHexPrefix(address)) || address.length() != 42) { //길이, length
                break;
            }

            Pattern pattern = Pattern.compile("^[0-9a-zA-Z]*$");
            Matcher matcher = pattern.matcher(address);
            if (!matcher.matches()) {
                CLog.w("Address has irreverent character. : " + address);
                break;
            }

            result = true;
        } while (false);

        return result;

    }

    public Credentials getCredential(String decryptedSeed, int addressIndex) {
        DeterministicKey parentKey = HDKeyDerivation.createMasterPrivateKey(Mnemonic.getEntropyByteSeeds(decryptedSeed));
        // Get KeyChain
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(parentKey);
        DeterministicKey rootKey = hierarchy.get(new EthereumCoins().getBip44Path(0), false, true);
        SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, addressIndex);

        // Get ECKeyPair
        ECKeyPair keyPair = keys.getECKeyPair();

        // Create Credential for Address.
        Credentials credentials = Credentials.create(keyPair);
        return credentials;
    }
}