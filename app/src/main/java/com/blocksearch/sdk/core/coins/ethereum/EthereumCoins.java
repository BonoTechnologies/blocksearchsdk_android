package com.blocksearch.sdk.core.coins.ethereum;


import com.blocksearch.sdk.SearchConstants;
import com.blocksearch.sdk.core.coins.CoinType;

import org.web3j.crypto.Hash;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EthereumCoins extends CoinType {

    public String publicAddress;
    public String gasFeeWei = "21000"; // wei, gwei, ether // don't use, use estimate gas.

    public EthereumCoins() {
        coinId = SearchConstants.CNUS_COIN_ID_ETH;
        coinDcd = SearchConstants.CNUS_COIN_DCD_ETH;
        name = SearchConstants.CNUS_COIN_NM_ETH;
        symbol = SearchConstants.CNUS_COIN_SYMBOL_ETH;
        bip44Index = 60;
        fee = new BigInteger(gasFeeWei);
        minUnitNm = "wei";
    }

    @Override
    public String getMinCoinUnit() {
        return minUnitNm;
    }

    @Override
    public BigInteger getFee(int position) {
        return Convert.toWei(String.valueOf(position), Convert.Unit.GWEI).toBigInteger();
    }

    @Override
    public BigDecimal getTotalPrice(int position) {
        return new BigDecimal(fee).multiply(Convert.toWei(String.valueOf(position), Convert.Unit.GWEI));
    }

    @Override
    public String getCoinValue(BigDecimal totalPrice, String format) {
        return String.format(format, Convert.fromWei(totalPrice, Convert.Unit.ETHER).floatValue());
    }

    @Override
    public int getPositionWithFeeValue(String totalCoinFeeValue) {
        return Convert.fromWei(Convert.toWei(totalCoinFeeValue, Convert.Unit.ETHER).divide(new BigDecimal(getFee()), MathContext.DECIMAL128), Convert.Unit.GWEI).intValue();
    }

    @Override
    public BigDecimal getSendBalanceFormat(BigDecimal funds) {
        return Convert.toWei(funds, Convert.Unit.ETHER);
    }

    @Override
    public boolean checkAddressValidation(String address) {
        boolean result = false;
        do {
            if (Numeric.containsHexPrefix(address) && address.length() != 42) { //길이, length
                break;
            }

            Pattern pattern = Pattern.compile("^[0-9a-zA-Z]*$");
            Matcher matcher = pattern.matcher(address);
            if (!matcher.matches()) {
                break;
            }

            result = true;
        } while (false);

        return result;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    public static String getChecksumAddress(String address) {
        if (checksumAddress(address)) {
            return address;
        } else {
            return changeToCheckedAddress(address);
        }
    }

    public static String changeToCheckedAddress(final String address) {
        final String cleanAddress = Numeric.cleanHexPrefix(address).toLowerCase();
        char[] cs = cleanAddress.toLowerCase().toCharArray();

        StringBuilder o = new StringBuilder();
        String keccak = Hash.sha3String(cleanAddress);
        char[] checkChars = keccak.substring(2).toCharArray();

        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];

            c = (Character.digit(checkChars[i], 16) & 0xFF) > 7 ? Character.toUpperCase(c) : Character.toLowerCase(c);
            o.append(c);
        }

        return Numeric.prependHexPrefix(o.toString());
    }

    public static boolean checksumAddress(String address) {
        if (address.length() < 40) {
            return false;
        }

        String cleanAddress = address.replace("0x", "");
        System.out.println("replaced address :: " + cleanAddress);

        String hashedAddress = Hash.sha3String(cleanAddress.toLowerCase());
        System.out.println("hashedAddress :: " + hashedAddress);
        hashedAddress = hashedAddress.replace("0x", "");

        for (int i = 0; i < 40; i++) {
            if (Integer.parseInt(hashedAddress.substring(i, i+1), 16) > 7 && !cleanAddress.substring(i, i+1).toUpperCase().equals(cleanAddress.substring(i, i+1)) ||
                Integer.parseInt(hashedAddress.substring(i, i+1), 16) <= 7 && !cleanAddress.substring(i, i+1).toLowerCase().equals(cleanAddress.substring(i, i+1))) {
                return false;
            }
        }

        return true;
    }

    public static String getDisplayBalanceFromWei(BigInteger weiBalance, int decimal) throws Exception {
        return new BigDecimal(weiBalance).divide(BigDecimal.TEN.pow(decimal)).toPlainString();
    }

    public static BigInteger getDisplayBalanceToWei(String displayBalance, int decimal) throws Exception{
        return new BigDecimal(displayBalance).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger();
    }

}
