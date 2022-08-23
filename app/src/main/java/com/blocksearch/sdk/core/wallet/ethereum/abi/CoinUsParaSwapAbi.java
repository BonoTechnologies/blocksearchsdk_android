package com.blocksearch.sdk.core.wallet.ethereum.abi;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinUsParaSwapAbi {

    public static Function getSwapOnUniswapFork(String factory, byte[] initCode, BigInteger amountIn, BigInteger amountOutMin, List<String> path, BigInteger referrer) {
        List<Address> addresses = new ArrayList<>();
        if (path != null && path.size() > 0) {
            for (String address : path) {
                addresses.add(new Address(address));
            }
        }

        return new Function(
                "swapOnUniswapFork",
                Arrays.asList(
                        new Address(factory),
                        new Bytes32(initCode),
                        new Uint256(amountIn),
                        new Uint256(amountOutMin),
                        new DynamicArray<>(Address.class, addresses),
                        new Uint8(referrer)
                ),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {})
        );
    }

    public static Function getSwapOnUniswap(BigInteger amountIn, BigInteger amountOutMin, List<String> path, BigInteger referrerId) {
        List<Address> addresses = new ArrayList<>();
        if (path != null && path.size() > 0) {
            for (String address : path) {
                addresses.add(new Address(address));
            }
        }

        return new Function(
                "swapOnUniswap",
                Arrays.asList(
                        new Uint256(amountIn),
                        new Uint256(amountOutMin),
                        new DynamicArray<>(Address.class, addresses),
                        new Uint8(referrerId)
                ),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {})
        );
    }

    public static Function getSimpleSwap(
            String fromToken, String toToken, BigInteger fromAmount, BigInteger toAmount,
            BigInteger expectedAmount, List<String> callees, byte[] exchangeData, List<BigInteger> startIndexes,
            List<BigInteger> values, String beneficiary, String referrer, boolean useReduxToken) {

        List<Address> calleesParams = new ArrayList<>();
        if (callees != null && callees.size() > 0) {
            for (String address : callees) {
                calleesParams.add(new Address(address));
            }
        }

        List<Uint256> startIndexesParams = new ArrayList<>();
        if (startIndexes != null && startIndexes.size() > 0) {
            for (BigInteger startIndex : startIndexes) {
                startIndexesParams.add(new Uint256(startIndex));
            }
        }

        List<Uint256> valuesParams = new ArrayList<>();
        if (values != null && values.size() > 0) {
            for (BigInteger value : values) {
                valuesParams.add(new Uint256(value));
            }
        }

        return new Function(
                "simpleSwap",
                Arrays.asList(
                    new Address(fromToken),
                    new Address(toToken),
                    new Uint256(fromAmount),
                    new Uint256(toAmount),
                    new Uint256(expectedAmount),
                    new DynamicArray<>(Address.class, calleesParams),
                    new DynamicBytes(exchangeData),
                    new DynamicArray<>(Uint256.class, startIndexesParams),
                    new DynamicArray<>(Uint256.class, valuesParams),
                    new Address(beneficiary),
                    new Utf8String(referrer),
                    new Bool(useReduxToken)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {})
        );
    }

//multiSwap
//megaSwap




}
