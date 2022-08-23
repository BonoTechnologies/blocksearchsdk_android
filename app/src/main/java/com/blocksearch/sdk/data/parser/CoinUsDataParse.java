package com.blocksearch.sdk.data.parser;

import com.blocksearch.sdk.data.CoinUsResponseCallback;
import com.blocksearch.sdk.data.CoinUsResponsePagingCallback;
import com.blocksearch.sdk.data.domain.CoinUsResponse;
import com.blocksearch.sdk.data.domain.atomic.GearResult;
import com.blocksearch.sdk.data.repository.remote.CoinUsResponseCode;

import java.util.List;

/**
 * SDK on 2019. 2. 1..
 */

public class CoinUsDataParse {

    public <T> CoinUsResponseCallback<CoinUsResponse<T>> createDataParser(final Class<T> resultDomain, final CoinUsResponseCallback callback) {

        return new CoinUsResponseCallback<CoinUsResponse<T>>() {

            @Override
            public void onResultFetched(CoinUsResponse<T> response) {
                if (response != null) {
                    if (response.getData().getResult() != null) {
                        GearResult result = response.getData().getResult();
                        if (result.isSuccess()) {
                            callback.onResultFetched(response.getData().getItems(resultDomain));
                        } else {
                            callback.onResultFailed(
                                    result.getNumber(),
                                    result.getMessage()
                            );
                        }
                    } else {
                        callback.onResultFetched(response.getData().getItems(resultDomain));
                    }
                } else {
                    callback.onResultFailed(
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                }
            }

            @Override
            public void onResultFailed(int code, String message) {
                callback.onResultFailed(code, message);
            }
        };
    }

    public <T> List<T> getDataItems(CoinUsResponse<T> response, final Class<T> resultDomain) {
        if (response != null) {
            if (response.getData() != null) {
                return response.getData().getItems(resultDomain);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public <T> CoinUsResponseCallback<CoinUsResponse<T>> createDataParser(final Class<T> resultDomain, final CoinUsResponsePagingCallback callback) {

        return new CoinUsResponseCallback<CoinUsResponse<T>>() {

            @Override
            public void onResultFetched(CoinUsResponse<T> response) {
                if (response != null) {
                    if (response.getData().getResult() != null) {
                        GearResult result = response.getData().getResult();
                        if (result.isSuccess()) {
                            callback.onResultFetched(response.getData().getItems(resultDomain), response.getData().getPaging());
                        } else {
                            callback.onResultFailed(
                                    result.getNumber(),
                                    result.getMessage()
                            );
                        }
                    } else {
                        callback.onResultFetched(response.getData().getItems(resultDomain), response.getData().getPaging());
                    }
                } else {
                    callback.onResultFailed(
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                }
            }

            @Override
            public void onResultFailed(int code, String message) {
                callback.onResultFailed(code, message);
            }
        };
    }

    public CoinUsResponseCallback<CoinUsResponse> createDataParser(final CoinUsResponseCallback callback) {

        return new CoinUsResponseCallback<CoinUsResponse>() {
            @Override
            public void onResultFetched(CoinUsResponse response) {
                if (response != null) {
                    if (response.getData().getResult() != null) {
                        GearResult result = response.getData().getResult();
                        if (result != null) {
                            if (result.isSuccess()) {
                                callback.onResultFetched(true);
                            } else {
                                callback.onResultFailed(
                                        result.getNumber(),
                                        result.getMessage()
                                );
                            }
                        }
                    } else {
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                } else {
                    callback.onResultFailed(
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                            CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                }
            }

            @Override
            public void onResultFailed(int code, String message) {
                callback.onResultFailed(code, message);
            }
        };
    }

}
