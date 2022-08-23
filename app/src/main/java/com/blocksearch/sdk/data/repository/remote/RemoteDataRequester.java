package com.blocksearch.sdk.data.repository.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.theblockchain.coinus.wallet.CoinUsApplication;
import com.theblockchain.coinus.wallet.R;
import com.theblockchain.coinus.wallet.common.CLog;
import com.theblockchain.coinus.wallet.common.CoinUsConstants;
import com.theblockchain.coinus.wallet.common.CoinUsPrefManager;
import com.theblockchain.coinus.wallet.common.CoinUsUtils;
import com.theblockchain.coinus.wallet.core.wallet.CoinUsWallet;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.EoscDataManager;
import com.theblockchain.coinus.wallet.data.CoinUsDataManager;
import com.theblockchain.coinus.wallet.data.CoinUsResponseCallback;
import com.theblockchain.coinus.wallet.data.domain.CoinUsResponse;
import com.theblockchain.coinus.wallet.data.domain.atomic.GearResult;
import com.theblockchain.coinus.wallet.data.domain.atomic.GearStatus;
import com.theblockchain.coinus.wallet.data.domain.atomic.PagingForm;
import com.theblockchain.coinus.wallet.data.domain.wallet.address.UserAddressBookDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.AnnouncementNoticeDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.CnusBoot;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.CnusToken;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.CnusWallet;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.CoinDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.CurrencyDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.ExchangeRateDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoInvestmentDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoPurchaseDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoPurchaseWalletDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoReferralDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.IcoSaleDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.InvestmentTotalAmountDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.KycDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.KycRequestDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.LanguageDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.PreorderKycDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.UserDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.UserKycDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.WalletCryptoDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.WalletDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.kyc.IcoKycUserStateDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.kyc.KYCReqExInfoDomain;
import com.theblockchain.coinus.wallet.data.mock.CoinUsMockDataManager;
import com.theblockchain.coinus.wallet.data.repository.CoinUsRequester;
import com.theblockchain.coinus.wallet.data.repository.local.LocalDataRequester;
import com.theblockchain.coinus.wallet.data.repository.remote.client.HttpClient;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.RetrofitServiceGenerator;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.service.ApiArgos;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.service.ApiBlock;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.service.ApiEtherscan;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.service.ApiEthplorer;
import com.theblockchain.coinus.wallet.data.repository.remote.retrofit.service.CoinUsService;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterApiData.RequestIdData.PayloadData.TransactionData;
import com.theblockchain.coinus.wallet.main.asset.eos.bp.EOSBPInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SDK on 2017. 12. 8..
 */

// CoinUs Requester to CoinUs Server.
public class RemoteDataRequester implements CoinUsRequester {

    private static RemoteDataRequester sInstance;
    private LocalDataRequester mLocalRequester;

    private Map<String, Erc20RequestDummyClass> mERC20ResponseMapper;

    synchronized public static RemoteDataRequester getInstance(LocalDataRequester localRequester) {
        if (sInstance == null) {
            sInstance = new RemoteDataRequester(localRequester);
        }

        return sInstance;
    }

    private RemoteDataRequester(LocalDataRequester localRequester) {
        mLocalRequester = localRequester;
    }

    @Override
    public void onStop() {

    }

    /**
     *
     * @param backgroundThread Result Parsing on Bg Thread
     * @param classType Retrofit Interface.
     * @return Service Class
     */
    public <T> T createRetrofitService(boolean backgroundThread, Class<T> classType) {
        return createRetrofitService(CoinUsApplication.getInstance().getApplicationContext(), backgroundThread, classType);
    }

    public <T> T createRetrofitService(Context context, boolean backgroundThread, Class<T> classType) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(context).getClient(),
                CoinUsConstants.API_COIN_US,
                backgroundThread);

        return retrofit.createService(classType);
    }

    /**
     *
     * @param call      Retrofit Caller
     * @param callback  Result Callback
     * @param callee    Method Nm for Log
     */
    public void requestRemoteData(Call<JsonObject> call, final CoinUsResponseCallback callback, final String callee) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null ) {
                    if (response.body() != null) {
                        CLog.d("callee : " + callee);
                        CLog.d("response body : " + response.body().toString());
                        CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                        if (responseData != null && responseData.getStatus() != null) {
                            int statusCode = responseData.getStatus().getCode();
                            if (statusCode == CoinUsResponseCode.OK.getCode()) {
                                callback.onResultFetched(responseData);
                            } else {
                                printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), callee);
                                CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                                callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                            }
                        } else {
                            printLogAPIErrorResponse(response, callee);
                            callback.onResultFailed(
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, callee);
                        callback.onResultFailed(
                                response.raw().code(),
                                EoscDataManager.convertStreamToString(response.errorBody().byteStream()));
                    }
                } else {
                    printLogAPIErrorResponse(response, callee);
                    callback.onResultFailed(
                            CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                            CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void requestRemoteDataWithoutNetCheck(Call<JsonObject> call, final CoinUsResponseCallback callback, final String callee) {
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null ) {
                    if (response.body() != null) {
                        CLog.d("callee : " + callee);
                        CLog.d("response body : " + response.body().toString());
                        CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                        if (responseData != null && responseData.getStatus() != null) {
                            int statusCode = responseData.getStatus().getCode();
                            if (statusCode == CoinUsResponseCode.OK.getCode()) {
                                callback.onResultFetched(responseData);
                            } else {
                                printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), callee);
                                CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                                callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                            }
                        } else {
                            printLogAPIErrorResponse(response, callee);
                            callback.onResultFailed(
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, callee);
                        callback.onResultFailed(
                                response.raw().code(),
                                EoscDataManager.convertStreamToString(response.errorBody().byteStream()));
                    }
                } else {
                    printLogAPIErrorResponse(response, callee);
                    callback.onResultFailed(
                            CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                            CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }


    /**
     *
     * @param call      Retrofit Caller
     * @param callee    Method Nm for Log
     */
    public CoinUsResponse requestRemoteDataSync(Call<JsonObject> call, final String callee) {
        CoinUsResponse coinUsResponse = new CoinUsResponse();
        GearStatus status = new GearStatus();

        if (!CoinUsUtils.isInternetAvailable()) {
            status.put("code", CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode());
            status.put("message", (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            coinUsResponse.setStatus(status);
            return coinUsResponse;
        }

        try {
            Response<JsonObject> response = call.execute();

            if (response != null ) {
                if (response.body() != null) {
                    CLog.d(callee + " response result : " + response.body().toString());
                    return new Gson().fromJson(response.body(), CoinUsResponse.class);

                } else {
                    printLogAPIErrorResponse(response, callee);
                    status.put("code", CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode());
                    status.put("message", CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    coinUsResponse.setStatus(status);
                    return coinUsResponse;
                }
            } else {
                printLogAPIErrorResponse(response, callee);
                status.put("code", CoinUsResponseCode.CODE_UNKNOWN.getCode());
                status.put("message", CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                coinUsResponse.setStatus(status);
                return coinUsResponse;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JsonObject requestRemoteDataSyncWithJson(Call<JsonObject> call, final String callee) {
        CoinUsResponse coinUsResponse = new CoinUsResponse();
        GearStatus status = new GearStatus();

        if (!CoinUsUtils.isInternetAvailable()) {
            status.put("code", CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode());
            status.put("message", (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            coinUsResponse.setStatus(status);
            return null;
        }

        try {
            Response<JsonObject> response = call.execute();

            if (response != null ) {
                if (response.body() != null) {
                    CLog.d(callee + " response result : " + response.body().toString());
                    return response.body();

                } else {
                    printLogAPIErrorResponse(response, callee);
                    status.put("code", CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode());
                    status.put("message", CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    coinUsResponse.setStatus(status);
                    return null;
                }
            } else {
                printLogAPIErrorResponse(response, callee);
                status.put("code", CoinUsResponseCode.CODE_UNKNOWN.getCode());
                status.put("message", CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                coinUsResponse.setStatus(status);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CoinUsResponse getBootingCoinUsApp(String deviceToken) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Map<String, Object> param = new HashMap<>();
        if (deviceToken != null) {
            param.put("deviceToken", deviceToken);
        }

        try {
            Call<JsonObject> call = coinUsService.getBootingCoinUsApp(param);

            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse<CnusBoot> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                return coinUsResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            /**
             * 2020-08-04 GiwungEom Gson 에서 JsonSyntaxException 발생 체크.
             */
            e.printStackTrace();
        }
        return null;
    }

    public void sendRegistrationServer(String deviceToken) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Map<String, Object> param = new HashMap<>();
        if (deviceToken != null) {
            param.put("deviceToken", deviceToken);
        }

        Call<JsonObject> call = coinUsService.insertBootingMobileDevicePushToken(param);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                CLog.d("result : " + response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("onFailure : " + t.getMessage());
                }
            }
        });
//        try {
//            Response<JsonObject> response = call.execute();
//            if (response != null && response.body() != null) {
//                CLog.d("result : " + response.body());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void getCoinList(Map<String, Object> params, PagingForm paging, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getCoinList(params, paging.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse<CoinDomain> responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getCoinList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getCoinList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getWalletList(Map<String, Object> params, PagingForm paging, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getWalletList(params, paging.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse<WalletDomain> responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getWalletList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getWalletList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getWalletCryptoCurrencyPrice(long wno, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getWalletCryptoCurrencyPrice(wno);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse<WalletDomain> responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getWalletCryptoCurrencyPrice");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getWalletCryptoCurrencyPrice");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getWalletCryptoList(long wno, Map<String, Object> params, PagingForm paging, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getWalletCryptoList(wno, params, paging.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse<WalletCryptoDomain> responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getWalletCryptoList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getWalletCryptoList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void deleteWallet(long wno, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.deleteWallet(wno);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "deleteWallet");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "deleteWallet");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public CoinUsResponse getEthTokenByAddressContract(String contractAddress, String walletAddress) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Map<String, Object> params = new HashMap<>();
        Call<JsonObject> call = coinUsService.getEthTokenByAddressContract(walletAddress, contractAddress, params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                if (responseData != null && responseData.getStatus() != null) {
                    int statusCode = responseData.getStatus().getCode();
                    if (statusCode == CoinUsResponseCode.OK.getCode()) {
                        return responseData;
                    } else {
                        printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthTokenByAddressContract");
                        return null;
                    }
                } else {
                    printLogAPIErrorResponse(response, "getEthTokenByAddressContract");
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CoinUsResponse getBtcAddress(String walletAddress) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getBtcAddress(walletAddress);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                if (responseData != null && responseData.getStatus() != null) {
                    int statusCode = responseData.getStatus().getCode();
                    if (statusCode == CoinUsResponseCode.OK.getCode()) {
                        return responseData;
                    } else {
                        printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getBtcAddress");
                        return null;
                    }
                } else {
                    printLogAPIErrorResponse(response, "getBtcAddress");
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void getWalletTokenList(long wno, Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getWalletTokenList(wno, params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getWalletTokenList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getWalletTokenList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void insertDappWhitelist(Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.insertDappWhitelist(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(response.body().get("data").getAsJsonObject().get("seq").getAsLong());
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "insertDappWhitelist");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "insertDappWhitelist");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void updateDappWhitelist(Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateDappWhitelist(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(response.body().get("data").getAsJsonObject().get("seq").getAsLong());
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "updateDappWhitelist");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "updateDappWhitelist");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getDappWhiltelist(Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getDappWhiltelist(params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getWalletTokenList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getWalletTokenList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEthAddress(String address, Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthAddress(address, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);


                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthAddress");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthAddress");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public CoinUsResponse getEthAddressSync(String address, Map<String, Object> params) {
        if (!CoinUsUtils.isInternetAvailable()) {
            return null;
        }
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthAddress(address, params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                if (responseData != null && responseData.getStatus() != null) {
                    int statusCode = responseData.getStatus().getCode();
                    if (statusCode == CoinUsResponseCode.OK.getCode()) {
                        return responseData;
                    } else {
                        printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthAddress");
                        CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                        return null;
                    }
                } else {
                    printLogAPIErrorResponse(response, "getEthAddress");
                    return null;
                }
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void getEosAccountBalance(String account, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEosAccountBalance(account);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEosAccountBalance");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEosAccountBalance");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEosToken(String account, String code, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEosToken(account, code);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEosToken");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEosToken");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getBtcAddress(String address, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getBtcAddress(address);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthAddress");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthAddress");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEthTokenByAddressContract(String address, String contractAddress, Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthTokenByAddressContract(address, contractAddress, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthAddress");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthAddress");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEthTxByAddressList(String address, Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US, true);


        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthTxByAddressList(address, params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthTxByAddressList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthTxByAddressList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEthTokenTransferByAddressList(String address, String contractAddress, Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthTokenTransferByAddressList(address, contractAddress, params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthTokenTransferByAddressList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthTokenTransferByAddressList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEosAccountTxList(String account, Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEosAccountTxList(account, params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEosAccountTxList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEosAccountTxList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getBtcTxByAddressList(String account, Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getBtcTxByAddressList(account, params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getBtcTxByAddressList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getBtcTxByAddressList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getUserAddressBookList(Map<String, Object> params, PagingForm pagingForm, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getUserAddressBookList(params, pagingForm.getParams());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getUserAddressBookList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getUserAddressBookList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }
//
//    public CoinUsResponse getEosAccountBalance(String walletAddressNm) {
//        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
//                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
//                CoinUsConstants.API_COIN_US);
//
//        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
//
//        Call<JsonObject> call = coinUsService.getEosAccountBalance(walletAddressNm);
//
//        try {
//            Response<JsonObject> response = call.execute();
//            if (response != null && response.body() != null) {
//                CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);
//                if (responseData != null) {
//                    return responseData;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public void getMyAccount(final CoinUsResponseCallback<CoinUsResponse<UserDomain>> callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getMyAccount();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getMyAccount");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getMyAccount");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEthTxByAddress(String address, String txHash, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEthTxByAddress(address, txHash);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEthTxByAddress");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEthTxByAddress");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getBtcTxByAddressList(String address, String txHash, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getBtcTxByAddressList(address, txHash);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getBtcTxByAddressList");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getBtcTxByAddressList");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getEosTx(String address, String txHash, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getEosTx(address, txHash);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getEosTx");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getEosTx");
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getUserAddressBook(long addressBookSeq, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getUserAddressBook(addressBookSeq);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getUserAddressBook");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getUserAddressBook");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void getUserAssetCacheData(Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US,
                true);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getUserAssetCacheData(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "getUserAssetCacheData");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "getUserAssetCacheData");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }


    public void insertUserAddressBook(Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.insertUserAddressBook(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "insertUserAddressBook");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "insertUserAddressBook");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void updateUserAddressBook(long addressBookSeq, Map<String, Object> params, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateUserAddressBook(addressBookSeq, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "updateUserAddressBook");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "updateUserAddressBook");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }


    public void deleteUserAddressBook(long addressBookSeq, final CoinUsResponseCallback callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.deleteUserAddressBook(addressBookSeq);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "deleteUserAddressBook");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "deleteUserAddressBook");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }

    public void updateWalletTokenActiveYn(long wno, long tokenId, Map<String, Object> params, final CoinUsResponseCallback<CoinUsResponse> callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateWalletTokenActiveYn(wno, tokenId, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CoinUsResponse responseData = new Gson().fromJson(response.body(), CoinUsResponse.class);

                    if (responseData != null && responseData.getStatus() != null) {
                        int statusCode = responseData.getStatus().getCode();
                        if (statusCode == CoinUsResponseCode.OK.getCode()) {
                            callback.onResultFetched(responseData);
                        } else {
                            printLogAPIErrorFromCoinUsServer(responseData.getStatus().getCode(), responseData.getStatus().getMessage(), "updateWalletTokenActiveYn");
                            CoinUsResponseCode responseCode = getCoinUsResponseCode(statusCode);
                            callback.onResultFailed(responseCode.getCode(), responseCode.getDesc());
                        }
                    } else {
                        printLogAPIErrorResponse(response, "updateWalletTokenActiveYn");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_UNKNOWN.getCode(),
                        CoinUsResponseCode.CODE_UNKNOWN.getDesc());
                CLog.w("Error : " + t.getMessage());
            }
        });
    }


    public void getAnnouncementNoticeList(Map<String, Object> params, Map<String, Object> paging, final CoinUsResponseCallback<List<AnnouncementNoticeDomain>> callback) {
        if (!CoinUsUtils.isInternetAvailable()) {
            callback.onResultFailed(CoinUsResponseCode.NETWORK_UN_CONNECTED.getCode(),
                    (CoinUsApplication.getInstance()).getString(R.string.network_connection_check));
            return;
        }

        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getAnnouncementNoticeList(params, paging);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    CoinUsResponse<AnnouncementNoticeDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse != null && coinUsResponse.getData() != null) {
                        if (callback != null) {
                            callback.onResultFetched(coinUsResponse.getData().getItems(AnnouncementNoticeDomain.class));
                        } else {
                            printLogAPIErrorFromCoinUsServer(coinUsResponse.getStatus().getCode(), coinUsResponse.getStatus().getMessage(), "getAnnouncementNoticeList");
                            callback.onResultFailed(
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                            CLog.w("callback is null");
                        }
                    } else {
                        CLog.w("coinUsResponse is null or data is null");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.w("t :: " + t.getMessage());
                }
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                        CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
            }
        });

//        try {
//            Response<JsonObject> response = call.execute();
//            if (response != null && response.body() != null) {
//                CLog.d("result : " + response.body());
//                CoinUsResponse<AnnouncementNoticeDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
//                if (coinUsResponse != null && coinUsResponse.getData() != null) {
//                    return coinUsResponse.getData().getItems(AnnouncementNoticeDomain.class);
//                } else {
//                    CLog.w("coinUsResponse is null or data is null");
//                }
//
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public void getAnnouncementNotice(long anncSeq, final CoinUsResponseCallback<AnnouncementNoticeDomain> callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getAnnouncementNotice(anncSeq);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    CoinUsResponse<AnnouncementNoticeDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse != null && coinUsResponse.getData() != null) {
                        if (callback != null) {
                            callback.onResultFetched(coinUsResponse.getData().getItemsFindFirst(AnnouncementNoticeDomain.class));
                        } else {
                            printLogAPIErrorFromCoinUsServer(coinUsResponse.getStatus().getCode(), coinUsResponse.getStatus().getMessage(), "getAnnouncementNotice");
                            callback.onResultFailed(
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                    CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                            CLog.w("callback is null");
                        }
                    } else {
                        CLog.w("coinUsResponse is null or data is null");
                        callback.onResultFailed(
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                                CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                CLog.w("t :: " + t.getMessage());
                callback.onResultFailed(
                        CoinUsResponseCode.CODE_PARSING_NO_DATA.getCode(),
                        CoinUsResponseCode.CODE_PARSING_NO_DATA.getDesc());
            }
        });
//
//        try {
//            Response<JsonObject> response = call.execute();
//            if (response != null && response.body() != null) {
//                CLog.d("result : " + response.body());
//                CoinUsResponse<AnnouncementNoticeDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
//                if (coinUsResponse != null && coinUsResponse.getData() != null) {
//                    return coinUsResponse.getData().getItemsFindFirst(AnnouncementNoticeDomain.class);
//                } else {
//                    CLog.w("coinUsResponse is null or data is null");
//                }
//
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


    public CoinUsResponse getTokenList(Map<String, Object> params, Map<String, Object> paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getTokenList(params, paging);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                synchronized (CoinUsDataManager.synchronizationLock) {
                    CoinUsResponse<CnusToken> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    return coinUsResponse;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // sync
    public boolean insertUser(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.insertUser(params);
        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                JsonObject jsonObject = response.body();
                if (jsonObject != null && !jsonObject.isJsonNull() && jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
                    String authToken = dataObject.get("authToken").getAsString();
                    if (authToken != null) {
                        CoinUsPrefManager.setCoinUsAuthToken(CoinUsApplication.getInstance(), authToken);
                        CLog.d("authToken : " + authToken);
                        return true;
                    } else {
                        return false;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public void insertUserAsync(Map<String, Object> params) {
//        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
//                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
//                CoinUsConstants.API_COIN_US);
//
//        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
//
//        Call<JsonObject> call = coinUsService.insertUser(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response != null && response.body() != null) {
//                    CLog.d("result : " + response.body());
//                    JsonObject jsonObject = response.body();
//                    if (jsonObject != null && !jsonObject.isJsonNull() && jsonObject.has("data")) {
//                        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
//                        String authToken = dataObject.get("authToken").getAsString();
//                        CoinUsPrefManager.setCoinUsAuthToken(CoinUsApplication.getInstance(), authToken);
//                        CLog.d("authToken : " + authToken);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
//    }

//    public String getUserId() {
//        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
//                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
//                CoinUsConstants.API_COIN_US);
//
//        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
//
//        Call<JsonObject> call = coinUsService.getMyAccount();
//
//        try {
//            Response<JsonObject> response = call.execute();
//            if (response != null && response.body() != null) {
//                CLog.d("result : " + response.body());
//                CoinUsResponse<MyAccount> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
//                return Long.toString(coinUsResponse.getData().getItems(MyAccount.class).get(0).getUid());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    // Async
    public void insertUserRestoreLog() {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.insertUserRestoreLog();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("error : " + t.getMessage());
                }
            }
        });
    }

    public void insertUserBackupLog() {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.insertUserBackupLog();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    CoinUsWallet.getInstance().setIsWalletBackup(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("error : " + t.getMessage());
                }
            }
        });
    }

    public boolean reqRefund( long icoId, String referralCode  ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = null;
        if( referralCode == null ) {
            call = coinUsService.reqPublicRefund( icoId );
        }else {
            call = coinUsService.reqPrivateRefund( icoId, referralCode );
        }

        try {
            Response<JsonObject> response = call.execute();
            if (response.body() == null){ return false; }

            JsonObject jsonObject = response.body();
            if (jsonObject.get("data") == null || jsonObject.get("data").isJsonNull() ){ return false; }

            JsonObject data = jsonObject.get("data").getAsJsonObject();
            if (data == null || data.isJsonNull() ){ return false; }

            if (data.has("result") == false ){return false;}
            JsonObject result = data.get("result").getAsJsonObject();

            if( result == null || result.isJsonNull()){ return false; }

            if( result.has("isSuccess")){
                CLog.d("isSuccess :: " + result.get("isSuccess").getAsBoolean());
                return result.get("isSuccess").getAsBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public List<IcoPurchaseWalletDomain> getIcoPurchaseWalletList( long icoId, String referralCode  ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = null;
        if( referralCode == null ) {
            call = coinUsService.getPublicIcoPurchaseWalletList(icoId);
        }else {
            call = coinUsService.getPrivateIcoPurchaseWalletList(icoId, referralCode);
        }

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<IcoPurchaseWalletDomain>) coinUsResponse.getData().getItems(IcoPurchaseWalletDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IcoPurchaseDomain> getIcoPurchaseList(){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoPurchaseList();

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<IcoPurchaseDomain>) coinUsResponse.getData().getItems(IcoPurchaseDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveKycInfo( long uid, Map<String, Object> params ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.saveKycInfo( uid, params );
        try {
            Response<JsonObject> response = call.execute();
            if (response.body() == null){ return false; }

            JsonObject jsonObject = response.body();
            if (jsonObject.get("data") == null || jsonObject.get("data").isJsonNull() ){ return false; }

            JsonObject data = jsonObject.get("data").getAsJsonObject();
            if (data == null || data.isJsonNull() ){ return false; }

            if (data.has("result") == false ){return false;}
            JsonObject result = data.get("result").getAsJsonObject();

            if( result == null || result.isJsonNull()){ return false; }

            if( result.has("isSuccess")){
                CLog.d("isSuccess :: " + result.get("isSuccess").getAsBoolean());
                return result.get("isSuccess").getAsBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public long insertWallet(Map<String, Object> params) {
        return insertWallet(params, false);
    }

    public long insertWallet(Map<String, Object> params, boolean mockTest) {

        if (mockTest) {
            return CoinUsMockDataManager.getInstance().insertWallet(params);

        } else {
            RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                    HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                    CoinUsConstants.API_COIN_US);

            CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

            Call<JsonObject> call = coinUsService.insertWallet(params);
            try {
                Response<JsonObject> response = call.execute();
                if (response.body() != null) {
                    CLog.d("response : " + response.body());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        if (data != null && !data.isJsonNull()) {
                            if (data.has("wno")) {
                                CLog.d("wno :: " + data.get("wno").getAsLong());
                                return data.get("wno").getAsLong();
                            } else {
                                CLog.w("data doesn't have wno");
                            }
                        } else {
                            CLog.w("data is null");
                        }
                    } else {
                        CLog.w("data is null");
                    }
                } else {
                    CLog.w("Result is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public void updateWallet(long wno, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateWalletNm(wno, params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("error : " + t.getMessage());
                }
            }
        });
    }

    public boolean updateWalletNm(long wno, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateWalletNm(wno, params);
        try {
            Response<JsonObject> response = call.execute();
            if (response.body() != null) {
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    GearResult result = coinUsResponse.getData().getResult();
                    if (result != null) {
                        return result.isSuccess();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void updateUserEmailConfirm(Map<String, Object> params, final CoinUsResponseCallback callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateUserEmailConfirm(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (callback != null) {
                        callback.onResultFetched(coinUsResponse);
                    } else {
                        CLog.w("callback is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("error : " + t.getMessage());
                }
            }
        });
    }

    public void updateUserEmail(Map<String, Object> params, final CoinUsResponseCallback callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.updateUserEmail(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (callback != null) {
                        callback.onResultFetched(coinUsResponse);
                    } else {
                        CLog.w("callback is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.d("error : " + t.getMessage());
                }
            }
        });

    }

    public boolean deleteWalletToServer(long wno) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.deleteWallet(wno);
        try {
            Response<JsonObject> response = call.execute();
            if (response.body() != null) {
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    GearResult result = coinUsResponse.getData().getResult();
                    if (result != null) {
                        return result.isSuccess();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserKycDomain> getIcoKycList(long icoId, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getIcoKycList(icoId, params);
        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("response : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return coinUsResponse.getData().getItems(UserKycDomain.class);
                } else {
                    CLog.w("getData is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<UserKycDomain> getUserKycList(Map<String, Object> params, Map<String, Object> paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getUserKycList(params, paging);
        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());

                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse != null && coinUsResponse.getData() != null) {
                    return coinUsResponse.getData().getItems(UserKycDomain.class);
                } else {
                    CLog.w("coinUsResponse is null or getData() is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public KycDomain getKyc(long kycNo) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        Call<JsonObject> call = coinUsService.getKyc(kycNo);
        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());

                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse != null && coinUsResponse.getData() != null) {
                    return (KycDomain) coinUsResponse.getData().getItems(KycDomain.class).get(0);
                } else {
                    CLog.w("coinUsResponse is null or getData() is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<KycRequestDomain> getKycRequestList(Map<String, Object> params, PagingForm paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);

        List<KycRequestDomain> returnList = new ArrayList<>();
        List<KycRequestDomain> tmpList = null;
        Call<JsonObject> call = coinUsService.getKycRequestList(params, paging.getParams());

        Response<JsonObject> response;
        CoinUsResponse coinUsResponse;
        do {
            try {
                response = call.execute();
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse != null && coinUsResponse.getData() != null) {
                        tmpList = coinUsResponse.getData().getItems(KycRequestDomain.class);
                        paging.setPageNo(paging.getPageNo() + 1);
                        call = coinUsService.getKycRequestList(params, paging.getParams());
                        returnList.addAll(tmpList);
                    } else {
                        CLog.w("coinUsResponse is null or getData() is null");
                        break;
                    }
                } else {
                    CLog.w("Response is null or body() is null");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while(coinUsResponse != null && coinUsResponse.getData() != null && coinUsResponse.getData().getPaging() != null &&
                coinUsResponse.getData().getPaging().isHasMore());

        return returnList.isEmpty() ? null : returnList;
    }


    public String getUserAuthToken(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getUserAuthToken(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                JsonObject jsonObject = response.body();
                if (jsonObject != null && !jsonObject.isJsonNull()) {
                    JsonObject data = jsonObject.get("data").getAsJsonObject();
                    if (data != null && !data.isJsonNull()) {
                        if (data.has("authToken") && !data.get("authToken").isJsonNull()) {
                            CLog.d("authToken get : " + data.get("authToken"));
                            return data.get("authToken").getAsString();
                        } else {
                            CLog.d("data doesn't have authToken key");
                        }
                    } else {
                        CLog.d("data is null");
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CoinUsResponse insertErrWalletInfo(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertErrWalletInfo(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return new Gson().fromJson(response.body(), CoinUsResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return -1;
    }

    public List<CnusWallet> getAllWalletList(Map<String, Object> params, PagingForm paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getWalletList(params, paging.getParams());
        Response<JsonObject> response;
        CoinUsResponse<CnusWallet> coinUsResponse;

        List<CnusWallet> returnList = new ArrayList<>();
        List<CnusWallet> tmpList;
        do {
            try {
                response = call.execute();
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse.getData() != null) {
                        tmpList = coinUsResponse.getData().getItems(CnusWallet.class);
                        returnList.addAll(tmpList);
                        paging.setPageNo(paging.getPageNo() + 1);
                        call = coinUsService.getWalletList(params, paging.getParams());
                    } else {
                        CLog.w("coinUsResponse.getData() is null");
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while(coinUsResponse != null && coinUsResponse.getData() != null && coinUsResponse.getData().getPaging() != null &&
                coinUsResponse.getData().getPaging().isHasMore());

        return returnList.isEmpty() ? null : returnList;
    }

    public void insertTokenAutoDiscovery(long wno, int coinId, Map<String, Object> params, final CoinUsResponseCallback<Long> callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertTokenAutoDiscovery(wno, coinId, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        CLog.d("tokenId :: " + data.get("tokenId").getAsLong());

                        if (callback != null) {
                            callback.onResultFetched(data.get("tokenId").getAsLong());

                        } else {
                            CLog.w("Callback is null");
                        }
                    } else {
                        CLog.w("data is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.w("t message :: " + t.getMessage());
                }
            }
        });
    }

    public String uploadKycIdPicture(MultipartBody.Part body) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.updateMyProfilePicture(body);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                JsonObject jsonObject = response.body();

                if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();

                    if (dataJsonObject.has("idPicturePath") && dataJsonObject.get("idPicturePath") != null && !dataJsonObject.get("idPicturePath").isJsonNull()) {
                        return dataJsonObject.get("idPicturePath").getAsString();
                    } else {
                        CLog.w("idPicturePath is null");
                    }
                } else {
                    CLog.w("Data is null");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserAddressBookDomain> getUserAddressBook(Map<String, Object> params, Map<String, Object> paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getUserAddressBookList(params, paging);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                synchronized (CoinUsDataManager.synchronizationLock) {
                    CoinUsResponse<UserAddressBookDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse.getData() != null) {
                        return coinUsResponse.getData().getItems(UserAddressBookDomain.class);
                    } else {
                        CLog.w("coinUsResponse.getData() is null");
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public CoinUsResponse insertUserAddressBook(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertUserAddressBook(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return new Gson().fromJson(response.body(), CoinUsResponse.class);
//                JsonObject jsonObject = response.body();
//                if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
//                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();
//
//                    if (dataJsonObject.get("addressBookSeq") != null && !dataJsonObject.get("addressBookSeq").isJsonNull()) {
//                        return dataJsonObject.get("addressBookSeq").getAsLong();
//                    } else {
//                        CLog.w("addressBookSeq is null");
//                    }
//                } else {
//                    CLog.w("Data is null");
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return -1;
    }

    public void deleteUserAddressBook(long addressSeq) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.deleteUserAddressBook(addressSeq);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());


                JsonObject jsonObject = response.body();
                if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();

                    if (dataJsonObject.has("result")) {
                        JsonObject result = dataJsonObject.get("result").getAsJsonObject();
                        if (result != null && !result.isJsonNull()) {
                            if (result.has("isSuccess") && !result.get("isSuccess").isJsonNull()) {
                                boolean isSuccess = result.get("isSuccess").getAsBoolean();
                                CLog.d("isSuccess : " + isSuccess);
                            } else {
                                CLog.w("isSuccess json Object is null");
                            }
                        } else {
                            CLog.w("result is null");
                        }
                    } else {
                        CLog.w("data doesn't have result object");
                    }
                } else {
                    CLog.w("Data is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ExchangeRateDomain getCryptoCurrencyExchange(String symbol) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getCryptoCurrencyExchange(symbol);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (ExchangeRateDomain) coinUsResponse.getData().getItemsFindFirst(ExchangeRateDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getCryptoCurrencyExchange(String symbol, final CoinUsResponseCallback<ExchangeRateDomain> callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getCryptoCurrencyExchange(symbol);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    if (callback != null) {
                        synchronized (CoinUsDataManager.synchronizationLock) {
                            CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                            callback.onResultFetched((ExchangeRateDomain) coinUsResponse.getData().getItemsFindFirst(ExchangeRateDomain.class));
                        }
                    } else {
                        CLog.d("callback is null");
                    }
                } else {
                    CLog.w("response is null");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.w("t message :: " + t.getMessage());
                } else {
                    CLog.w("throwable is null");
                }
            }
        });
    }


    public List<KYCReqExInfoDomain> getKycReqExList( String icoIDs ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getKycRequestExList( icoIDs );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<KYCReqExInfoDomain>) coinUsResponse.getData().getItems(KYCReqExInfoDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IcoKycUserStateDomain getIcoKycUserState (String icoId) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoKycUserState( icoId );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (IcoKycUserStateDomain) coinUsResponse.getData().getItemsFindFirst(IcoKycUserStateDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertIcoKycUserState (String icoId, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertIcoKycUserState(icoId, params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                JsonObject jsonObject = response.body();

                if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();

                    if (dataJsonObject.has("result")) {
                        JsonObject result = dataJsonObject.get("result").getAsJsonObject();

                        if (result != null && !result.isJsonNull()) {
                            if (result.has("isSuccess") && !result.get("isSuccess").isJsonNull()) {
                                boolean isSuccess = result.get("isSuccess").getAsBoolean();
                                CLog.d("isSuccess : " + isSuccess);
                                return isSuccess;
                            } else {
                                CLog.w("isSuccess json Object is null");
                            }
                        } else {
                            CLog.w("result is null");
                        }
                    } else {
                        CLog.w("data doesn't have result object");
                    }
                } else {
                    CLog.w("Data is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JsonObject requestArgosAuth(RequestBody body) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ARGOS);

        ApiArgos apiArgos = retrofit.createService(ApiArgos.class);
        Call<JsonObject> call = apiArgos.requestArgosAuth(body);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject uploadArgosImage(String email, String submission_id, String type, RequestBody body) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        ApiArgos apiArgos = retrofit.createService(ApiArgos.class);
        Call<JsonObject> call = apiArgos.uploadArgosImage(CoinUsConstants.COIN_US_ARGOS_API_KEY, email, submission_id, body);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getArgosAuthState(String email, String submission_id) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ARGOS);

        ApiArgos apiArgos = retrofit.createService(ApiArgos.class);
        Call<JsonObject> call = apiArgos.getArgosAuthState(email, submission_id);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public KYCReqExInfoDomain getKycReqEx(long icoID, long kycLevel, String KycUUID, long externalKey ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getKycRequestEx( icoID, kycLevel, KycUUID, externalKey );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (KYCReqExInfoDomain) coinUsResponse.getData().getItemsFindFirst(KYCReqExInfoDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<KYCReqExInfoDomain> getKycResult( String kycUUID ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        String wrapArg = String.format( "'%s'", kycUUID );

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getKycResult( wrapArg );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    //return (KYCReqExInfoDomain) coinUsResponse.getData().getItemsFindFirst(KYCReqExInfoDomain.class);
                    return (List<KYCReqExInfoDomain>) coinUsResponse.getData().getItems(KYCReqExInfoDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public IcoReferralDomain getIcoReferral(String referralCode ){
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoReferral( referralCode );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (IcoReferralDomain) coinUsResponse.getData().getItemsFindFirst(IcoReferralDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<IcoDomain> getIcoList(Map<String, Object> params, Map<String, Object> paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoList(params, paging);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<IcoDomain>) coinUsResponse.getData().getItems(IcoDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IcoDomain getIco(long icoId) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIco(icoId);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (IcoDomain) coinUsResponse.getData().getItemsFindFirst(IcoDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IcoSaleDomain getIcoSale(long icoCoinUsId) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoSale(icoCoinUsId);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse<IcoSaleDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return coinUsResponse.getData().getItemsFindFirst(IcoSaleDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IcoInvestmentDomain> getIcoInvestment(long icoCoinUsId, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoInvestment(icoCoinUsId, params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse<IcoInvestmentDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return coinUsResponse.getData().getItems(IcoInvestmentDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InvestmentTotalAmountDomain> getIcoInvestmentTotalAmount(long icoId) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getIcoInvestmentTotalAmount(icoId);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse<IcoInvestmentDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return coinUsResponse.getData().getItems(InvestmentTotalAmountDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CoinUsResponse insertIcoInvestment(long icoId, Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertIcoInvestment(icoId, params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse<IcoInvestmentDomain> coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return coinUsResponse;
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean insertCounselingBoard(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertCounselingBoard(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                JsonObject jsonObject = response.body();

                if (jsonObject.get("data") != null && !jsonObject.get("data").isJsonNull()) {
                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();

                    if (dataJsonObject.has("result")) {
                        JsonObject result = dataJsonObject.get("result").getAsJsonObject();

                        if (result != null && !result.isJsonNull()) {
                            if (result.has("isSuccess") && !result.get("isSuccess").isJsonNull()) {
                                boolean isSuccess = result.get("isSuccess").getAsBoolean();
                                CLog.d("isSuccess : " + isSuccess);
                                return isSuccess;
                            } else {
                                CLog.w("isSuccess json Object is null");
                            }
                        } else {
                            CLog.w("result is null");
                        }
                    } else {
                        CLog.w("data doesn't have result object");
                    }
                } else {
                    CLog.w("Data is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public EOSBPInfo getBPList() {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        Map<String, Object> params = new HashMap<>();
        params.put( "pageSize", 1000 );

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getBPList(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                EOSBPInfo eosBpInfo = EOSBPInfo.createFromJson( response.body() );
                return eosBpInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param params
     * @return kycRequestNo.
     */
    public JsonObject insertKycRequest(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.insertKycRequest(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PreorderKycDomain> getPreorderKycDomain(long wno) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getPreorderKyc(wno);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<PreorderKycDomain>) coinUsResponse.getData().getItems(PreorderKycDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<CurrencyDomain> getCurrencyList(Map<String, Object> params, PagingForm paging) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getCurrencyList(params, paging.getParams());
        Response<JsonObject> response;
        CoinUsResponse coinUsResponse;

        List<CurrencyDomain> returnList = new ArrayList<>();
        List<CurrencyDomain> tmpList;
        do {
            try {
                response = call.execute();
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                    if (coinUsResponse.getData() != null) {
                        tmpList = coinUsResponse.getData().getItems(CurrencyDomain.class);
                        returnList.addAll(tmpList);
                        paging.setPageNo(paging.getPageNo() + 1);
                        call = coinUsService.getCurrencyList(params, paging.getParams());
                    } else {
                        CLog.w("coinUsResponse.getData() is null");
                        break;
                    }
                } else {
                    CLog.w("response.body() is null");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while(coinUsResponse != null && coinUsResponse.getData() != null && coinUsResponse.getData().getPaging() != null &&
                coinUsResponse.getData().getPaging().isHasMore());

        return returnList.isEmpty() ? null : returnList;
    }

    public List<LanguageDomain> getLanguageList(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getLanguageList(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return (List<LanguageDomain>) coinUsResponse.getData().getItems(LanguageDomain.class);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;


    }

    public void getCurrencyListAsync(Map<String, Object> params, Map<String, Object> paging, final CoinUsResponseCallback callback) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getCurrencyList(params, paging);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());
                    if (callback != null) {
                        synchronized (CoinUsDataManager.synchronizationLock) {
                            CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                            if (coinUsResponse.getData() != null) {
                                callback.onResultFetched(coinUsResponse.getData().getItems(CurrencyDomain.class));
                            }
                        }
                    } else {
                        CLog.w("callback is null");
                    }
                } else {
                    CLog.w("response is null");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.w("t message :: " + t.getMessage());
                } else {
                    CLog.w("throwable is null");
                }
            }
        });
    }

    public void updateUserNotiYn(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.updateUserNotiYn(params);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateUserLanguageCd(Map<String, Object> params) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.updateUserLanguageCd(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null && response.body() != null) {
                    CLog.d("result : " + response.body());

                } else {
                    CLog.w("response is null");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t != null) {
                    CLog.w("t message :: " + t.getMessage());
                } else {
                    CLog.w("throwable is null");
                }
            }
        });
    }

    public JsonObject getBalanceFromNet(String walletAddress) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ETHER_PLORER);

        ApiEthplorer apiEthplorer = retrofit.createService(ApiEthplorer.class);
        Call<JsonObject> call = apiEthplorer.getAddressInfo(walletAddress, CoinUsUtils.getEthKey());

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getEthBalanceFromEtherscan(String walletAddress) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ETHERSCAN);

        ApiEtherscan apiEtherscan = retrofit.createService(ApiEtherscan.class);
        Call<JsonObject> call = apiEtherscan.getEthBalance(walletAddress, CoinUsUtils.getEtherscanKey());

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getEthBalanceFromAPI_BLOCK( String walletAddress ) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getBlockAPIClient(),
                CoinUsConstants.API_BLOCK);

        Map<String, Object> params = new HashMap<>();

        ApiBlock apiBlock = retrofit.createService(ApiBlock.class);
        Call<JsonObject> call = apiBlock.getEthAddress( walletAddress, params );
        try {
            Response<JsonObject> response = call.execute();
            if( response.body() == null )
                return null;

            JsonObject data = CoinUsUtils.Json.getJsonObject( response.body(),"data");
            if( null == data )
                return null;

            JsonArray items = CoinUsUtils.Json.getJsonArray( data, "items" );
            if( null == items )
                return null;

            if( items.size() < 1 )
                return null;

            JsonElement je = items.get(0);
            JsonObject walletInfo = je.getAsJsonObject();

            return walletInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JsonObject getEthAddress( String walletAddress ) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        Map<String, Object> params = new HashMap<>();

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getEthAddress( walletAddress, params);
        try {
            Response<JsonObject> response = call.execute();
            if( response.body() == null )
                return null;

            JsonObject data = CoinUsUtils.Json.getJsonObject( response.body(),"data");
            if( null == data )
                return null;

            JsonArray items = CoinUsUtils.Json.getJsonArray( data, "items" );
            if( null == items )
                return null;

            if( items.size() < 1 )
                return null;

            JsonElement je = items.get(0);
            JsonObject walletInfo = je.getAsJsonObject();

            return walletInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JsonObject getTokenBalanceFromEtherscan(String contractAddress, String walletAddress) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ETHERSCAN);

        ApiEtherscan apiEtherscan = retrofit.createService(ApiEtherscan.class);
        Call<JsonObject> call = apiEtherscan.getTokenBalance(contractAddress, walletAddress, CoinUsUtils.getEtherscanKey());

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getPendingTransactionStatusFromEtherscan(String txHash) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ETHERSCAN);

        ApiEtherscan apiEtherscan = retrofit.createService(ApiEtherscan.class);
        Call<JsonObject> call = apiEtherscan.getPendingTransactionStatus(txHash, CoinUsUtils.getEtherscanKey());

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getAPIBlock(CnusWallet cnusWallet) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getBlockAPIClient(),
                CoinUsConstants.API_BLOCK);

        Map<String, Object> params = new HashMap<>();
        //params.put("currency", "KRW");    //  none으로 주면 다 긁어온다.
        params.put("includes", "tokens");
        ApiBlock apiBlock = retrofit.createService(ApiBlock.class);
        Call<JsonObject> call = apiBlock.getEthAddress( cnusWallet.getWalletAddress(), params );

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getEthplorer(CnusWallet cnusWallet) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getExternalAPIClient(),
                CoinUsConstants.API_ETHER_PLORER);

        ApiEthplorer apiEthplorer = retrofit.createService(ApiEthplorer.class);
        Call<JsonObject> call = apiEthplorer.getAddressInfo(cnusWallet.getWalletAddress(), CoinUsUtils.getEthKey());

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

//        call.enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response != null && response.body() != null) {
//                    CLog.d("response : " + response.body());
//                    List<GeneralCoins> generalCoins = mLocalRequester.getGeneralCoins();
//
//                    List<MyCoins> unknownCoins = getUserUnknownToken(accountNo, response.body(), generalCoins, publicAddress);
//                    mLocalRequester.updateMyCoinsWithUnknownToken(accountNo, unknownCoins, coinUsResponse);
////                    coinUsResponse.onResultFetched(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                CLog.w("onFailure called : " + t != null ? t.toString() : "");
//                coinUsResponse.onResultFetched(null);
//            }
//        });
    }

    private CoinUsResponseCode getCoinUsResponseCode(int statusCode) {
        for (CoinUsResponseCode responseCode : CoinUsResponseCode.values()) {
            if (responseCode.getCode() == statusCode) {
                return responseCode;
            }
        }

        return CoinUsResponseCode.CODE_UNKNOWN;
    }

    private void printLogAPIErrorFromCoinUsServer(int statusCode, String message, String methodName) {
        if (message != null) {
            CLog.e(methodName + " Error - Code : " + statusCode + " Message : " + message);
        }
    }

    private void printLogAPIErrorResponse(Response<JsonObject> response, String methodName) {
        if (response != null) {
            String message = "";
            if (response.message() != null) {
                message = response.message();
            }

            CLog.e(methodName + " Error - Code : " + response.code() + " Message : " + message);
        }
    }

//    private void updateEtherCnusWalletAndCnusWalletToken(CnusWallet cnusWallet, List<CnusToken> cnusTokens, JsonObject jsonObject) {
//        if (jsonObject != null) {
//            JsonElement element = jsonObject.get("address");
//            if (jsonObject != null) {
//                CLog.d("address : " + element.getAsString());
//            }
//
//            JsonObject ethJO = jsonObject.getAsJsonObject("ETH");
//            if (!ethJO.isJsonNull()) {
//
//                JsonElement balanceETHJE = ethJO.get("balance");
//                if (!balanceETHJE.isJsonNull()) {
//                    BigDecimal balance = balanceETHJE.getAsBigDecimal();
//                    cnusWallet.setBalance(balance.doubleValue());
//                    CLog.d("balance : " + String.valueOf(cnusWallet.getBalance()));
//
//                }
//                JsonElement totalInJE = ethJO.get("totalIn");
//                if (!totalInJE.isJsonNull()) {
//                    BigDecimal totalIn = totalInJE.getAsBigDecimal();
//                    CLog.d("totalIn : " + totalIn.floatValue());
//                }
//                JsonElement totalOutJE = ethJO.get("totalOut");
//                if (!totalOutJE.isJsonNull()) {
//                    BigDecimal totalOut = totalOutJE.getAsBigDecimal();
//                    CLog.d("totalOut : " + totalOut.floatValue());
//                }
//
//                JsonElement countTxsJE = jsonObject.get("countTxs");
//                if (!countTxsJE.isJsonNull()) {
//                    int countTxs = countTxsJE.getAsInt();
//                    CLog.d("countTxs : " + countTxs);
//                }
//
//                JsonArray tokensJA = jsonObject.getAsJsonArray("tokens");
//                if (tokensJA != null && !tokensJA.isJsonNull()) {
//                    for (int i = 0; i < tokensJA.size(); i++) {
//                        MyCoins coin = new MyCoins();
//
//                        JsonObject tokenJO = tokensJA.get(i).getAsJsonObject();
//                        JsonObject tokenInfoJO = tokenJO.getAsJsonObject("tokenInfo");
//                        if (!tokenInfoJO.isJsonNull()) {
//                            coin.setContractAddress(tokenInfoJO.get("address").getAsString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setDecimal(tokenInfoJO.get("decimals").getAsInt());
//                            coin.setSymbol(tokenInfoJO.get("symbol").getAsString());
//                            coin.setTokenYn(true);
//                            coin.setCoinType(ServiceCoinType.ETHER.toString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setServerVersion("1");
////                            coin.setUnknowCoinYn(isUnknownCoin(coin.getCoinNm()));
//
//                            coin.setMainDisplayYn(false);
//                            coin.setUnknownCoinYn(true);
//
//                            if (generalCoins != null && generalCoins.size() > 0) {
//                                for (GeneralCoins gCoin : generalCoins) {
//                                    if (gCoin != null && gCoin.getSymbol() != null && gCoin.getSymbol().length() > 0) {
//                                        if (gCoin.getSymbol().equals(coin.getSymbol())) {
//                                            coin.setUnknownCoinYn(false);
//                                            coin.setMainDisplayYn(true);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//
//                            // fixme (Giwung) Hardcoded MainDisplayYn to CoinUs
//                            if (coin.getSymbol().equals("CNUS")) {
//                                coin.setMainDisplayYn(true);
//                                coin.setUnknownCoinYn(false);
//                            }
//
//                            coin.setAccountNo(accountNo);
//                            coin.setAddressIndex(addressIndex);
//                        }
//
//                        JsonElement balanceJE = tokenJO.get("balance");
//                        if (balanceJE != null && !balanceJE.isJsonNull()) {
//
//                            String logBalance = balanceJE.getAsString();
//
//                            if (logBalance.toLowerCase().contains("e+")) {
//                                String[] splitedBalance = logBalance.toLowerCase().split("e+");
//                                if (splitedBalance != null && splitedBalance.length == 2) {
//                                    BigDecimal value = new BigDecimal(splitedBalance[0]);
//                                    double valueLog = Double.parseDouble(splitedBalance[1]);
//
//                                    CLog.d(coin.getCoinNm() + ", currentBalance : " + value + " e+ " + valueLog);
//                                    for (int index = 0; index < valueLog; index++) {
//                                        value = value.multiply(BigDecimal.TEN);
//                                    }
//
//                                    BigDecimal decimal = BigDecimal.ONE;
//                                    for (int index = 0; index < coin.getDecimal(); index++) {
//                                        decimal = decimal.multiply(BigDecimal.TEN);
//                                    }
//
//                                    value = value.divide(decimal);
//                                    coin.setCurrentBalance(value.doubleValue());
//                                } else {
//                                    coin.setCurrentBalance(balanceJE.getAsDouble());
//                                }
//                            }
//                        }
//
//                        coin.setPublicAddress(publicAddress);
//
//                        //2.5e+20 => 2.5 * 10^20
//                        unknownCoins.add(coin);
//                    }
//                } else {
//                    CLog.d("doesn't have any coin.");
//                }
//            }
//
//        } else {
//            CLog.w("jsonObject is null");
//        }
//
//
//    }
//
//    private List<CnusToken> getUserUnknownToken(CnusWallet cnusWallet, JsonObject jsonObject, List<CnusToken> generalCoins) {
//        List<CnusToken> unknownCoins = new ArrayList<>();
//        if (jsonObject != null) {
//            JsonElement element = jsonObject.get("address");
//            if (jsonObject != null) {
//                CLog.d("address : " + element.getAsString());
//            }
//
//            JsonObject ethJO = jsonObject.getAsJsonObject("ETH");
//            if (!ethJO.isJsonNull()) {
//                CnusToken myCoinEth = new CnusToken();
//
//                myCoinEth.setCoinNm("ETH");
//                myCoinEth.setAddressIndex(addressIndex);
//                myCoinEth.setPublicAddress(publicAddress);
//                myCoinEth.setUnknownCoinYn(false);
//                myCoinEth.setMainDisplayYn(true);
//                myCoinEth.setSymbol("ETH");
//                myCoinEth.setCoinType(ServiceCoinType.ETHER.toString());
//
//                JsonElement balanceETHJE = ethJO.get("balance");
//                if (!balanceETHJE.isJsonNull()) {
//                    BigDecimal balance = balanceETHJE.getAsBigDecimal();
//                    myCoinEth.setCurrentBalance(balance.doubleValue());
//                    CLog.d("balance : " + String.valueOf(myCoinEth.getCurrentBalance()));
////                    myCoinEth.setCurrentBalance(balance.doubleValue());
//
//                }
//                JsonElement totalInJE = ethJO.get("totalIn");
//                if (!totalInJE.isJsonNull()) {
//                    BigDecimal totalIn = totalInJE.getAsBigDecimal();
//                    CLog.d("totalIn : " + totalIn.floatValue());
//                }
//                JsonElement totalOutJE = ethJO.get("totalOut");
//                if (!totalOutJE.isJsonNull()) {
//                    BigDecimal totalOut = totalOutJE.getAsBigDecimal();
//                    CLog.d("totalOut : " + totalOut.floatValue());
//                }
//
//                JsonElement countTxsJE = jsonObject.get("countTxs");
//                if (!countTxsJE.isJsonNull()) {
//                    int countTxs = countTxsJE.getAsInt();
//                    CLog.d("countTxs : " + countTxs);
//                }
//                unknownCoins.add(myCoinEth);
//
//                JsonArray tokensJA = jsonObject.getAsJsonArray("tokens");
//                if (tokensJA != null && !tokensJA.isJsonNull()) {
//                    for (int i = 0; i < tokensJA.size(); i++) {
//                        MyCoins coin = new MyCoins();
//
//                        JsonObject tokenJO = tokensJA.get(i).getAsJsonObject();
//                        JsonObject tokenInfoJO = tokenJO.getAsJsonObject("tokenInfo");
//                        if (!tokenInfoJO.isJsonNull()) {
//                            coin.setContractAddress(tokenInfoJO.get("address").getAsString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setDecimal(tokenInfoJO.get("decimals").getAsInt());
//                            coin.setSymbol(tokenInfoJO.get("symbol").getAsString());
//                            coin.setTokenYn(true);
//                            coin.setCoinType(ServiceCoinType.ETHER.toString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setServerVersion("1");
////                            coin.setUnknowCoinYn(isUnknownCoin(coin.getCoinNm()));
//
//                            coin.setMainDisplayYn(false);
//                            coin.setUnknownCoinYn(true);
//
//                            if (generalCoins != null && generalCoins.size() > 0) {
//                                for (GeneralCoins gCoin : generalCoins) {
//                                    if (gCoin != null && gCoin.getSymbol() != null && gCoin.getSymbol().length() > 0) {
//                                        if (gCoin.getSymbol().equals(coin.getSymbol())) {
//                                            coin.setUnknownCoinYn(false);
//                                            coin.setMainDisplayYn(true);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//
//                            // fixme (Giwung) Hardcoded MainDisplayYn to CoinUs
//                            if (coin.getSymbol().equals("CNUS")) {
//                                coin.setMainDisplayYn(true);
//                                coin.setUnknownCoinYn(false);
//                            }
//
//                            coin.setAccountNo(accountNo);
//                            coin.setAddressIndex(addressIndex);
//                        }
//
//                        JsonElement balanceJE = tokenJO.get("balance");
//                        if (balanceJE != null && !balanceJE.isJsonNull()) {
//
//                            String logBalance = balanceJE.getAsString();
//
//                            if (logBalance.toLowerCase().contains("e+")) {
//                                String[] splitedBalance = logBalance.toLowerCase().split("e+");
//                                if (splitedBalance != null && splitedBalance.length == 2) {
//                                    BigDecimal value = new BigDecimal(splitedBalance[0]);
//                                    double valueLog = Double.parseDouble(splitedBalance[1]);
//
//                                    CLog.d(coin.getCoinNm() + ", currentBalance : " + value + " e+ " + valueLog);
//                                    for (int index = 0; index < valueLog; index++) {
//                                        value = value.multiply(BigDecimal.TEN);
//                                    }
//
//                                    BigDecimal decimal = BigDecimal.ONE;
//                                    for (int index = 0; index < coin.getDecimal(); index++) {
//                                        decimal = decimal.multiply(BigDecimal.TEN);
//                                    }
//
//                                    value = value.divide(decimal);
//                                    coin.setCurrentBalance(value.doubleValue());
//                                } else {
//                                    coin.setCurrentBalance(balanceJE.getAsDouble());
//                                }
//                            }
//                        }
//
//                        coin.setPublicAddress(publicAddress);
//
//                        //2.5e+20 => 2.5 * 10^20
//                        unknownCoins.add(coin);
//                    }
//                } else {
//                    CLog.d("doesn't have any coin.");
//                }
//            }
//
//        } else {
//            CLog.w("jsonObject is null");
//        }
//
//        return unknownCoins;
//    }




    //
//    private List<CnusToken> getUserUnknownToken(int addressIndex, JsonObject jsonObject, List<CnusToken> generalCoins, String publicAddress) {
//        List<CnusToken> unknownCoins = new ArrayList<>();
//        if (jsonObject != null) {
//            JsonElement element = jsonObject.get("address");
//            if (jsonObject != null) {
//                CLog.d("address : " + element.getAsString());
//            }
//
//            JsonObject ethJO = jsonObject.getAsJsonObject("ETH");
//            if (!ethJO.isJsonNull()) {
//                CnusToken myCoinEth = new CnusToken();
//
//                myCoinEth.setCoinNm("ETH");
//                myCoinEth.setAddressIndex(addressIndex);
//                myCoinEth.setPublicAddress(publicAddress);
//                myCoinEth.setUnknownCoinYn(false);
//                myCoinEth.setMainDisplayYn(true);
//                myCoinEth.setSymbol("ETH");
//                myCoinEth.setCoinType(ServiceCoinType.ETHER.toString());
//
//                JsonElement balanceETHJE = ethJO.get("balance");
//                if (!balanceETHJE.isJsonNull()) {
//                    BigDecimal balance = balanceETHJE.getAsBigDecimal();
//                    myCoinEth.setCurrentBalance(balance.doubleValue());
//                    CLog.d("balance : " + String.valueOf(myCoinEth.getCurrentBalance()));
////                    myCoinEth.setCurrentBalance(balance.doubleValue());
//
//                }
//                JsonElement totalInJE = ethJO.get("totalIn");
//                if (!totalInJE.isJsonNull()) {
//                    BigDecimal totalIn = totalInJE.getAsBigDecimal();
//                    CLog.d("totalIn : " + totalIn.floatValue());
//                }
//                JsonElement totalOutJE = ethJO.get("totalOut");
//                if (!totalOutJE.isJsonNull()) {
//                    BigDecimal totalOut = totalOutJE.getAsBigDecimal();
//                    CLog.d("totalOut : " + totalOut.floatValue());
//                }
//
//                JsonElement countTxsJE = jsonObject.get("countTxs");
//                if (!countTxsJE.isJsonNull()) {
//                    int countTxs = countTxsJE.getAsInt();
//                    CLog.d("countTxs : " + countTxs);
//                }
//                unknownCoins.add(myCoinEth);
//
//                JsonArray tokensJA = jsonObject.getAsJsonArray("tokens");
//                if (tokensJA != null && !tokensJA.isJsonNull()) {
//                    for (int i = 0; i < tokensJA.size(); i++) {
//                        MyCoins coin = new MyCoins();
//
//                        JsonObject tokenJO = tokensJA.get(i).getAsJsonObject();
//                        JsonObject tokenInfoJO = tokenJO.getAsJsonObject("tokenInfo");
//                        if (!tokenInfoJO.isJsonNull()) {
//                            coin.setContractAddress(tokenInfoJO.get("address").getAsString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setDecimal(tokenInfoJO.get("decimals").getAsInt());
//                            coin.setSymbol(tokenInfoJO.get("symbol").getAsString());
//                            coin.setTokenYn(true);
//                            coin.setCoinType(ServiceCoinType.ETHER.toString());
//                            coin.setCoinNm(tokenInfoJO.get("name").getAsString());
//                            coin.setServerVersion("1");
////                            coin.setUnknowCoinYn(isUnknownCoin(coin.getCoinNm()));
//
//                            coin.setMainDisplayYn(false);
//                            coin.setUnknownCoinYn(true);
//
//                            if (generalCoins != null && generalCoins.size() > 0) {
//                                for (GeneralCoins gCoin : generalCoins) {
//                                    if (gCoin != null && gCoin.getSymbol() != null && gCoin.getSymbol().length() > 0) {
//                                        if (gCoin.getSymbol().equals(coin.getSymbol())) {
//                                            coin.setUnknownCoinYn(false);
//                                            coin.setMainDisplayYn(true);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//
//                            // fixme (Giwung) Hardcoded MainDisplayYn to CoinUs
//                            if (coin.getSymbol().equals("CNUS")) {
//                                coin.setMainDisplayYn(true);
//                                coin.setUnknownCoinYn(false);
//                            }
//
//                            coin.setAccountNo(accountNo);
//                            coin.setAddressIndex(addressIndex);
//                        }
//
//                        JsonElement balanceJE = tokenJO.get("balance");
//                        if (balanceJE != null && !balanceJE.isJsonNull()) {
//
//                            String logBalance = balanceJE.getAsString();
//
//                            if (logBalance.toLowerCase().contains("e+")) {
//                                String[] splitedBalance = logBalance.toLowerCase().split("e+");
//                                if (splitedBalance != null && splitedBalance.length == 2) {
//                                    BigDecimal value = new BigDecimal(splitedBalance[0]);
//                                    double valueLog = Double.parseDouble(splitedBalance[1]);
//
//                                    CLog.d(coin.getCoinNm() + ", currentBalance : " + value + " e+ " + valueLog);
//                                    for (int index = 0; index < valueLog; index++) {
//                                        value = value.multiply(BigDecimal.TEN);
//                                    }
//
//                                    BigDecimal decimal = BigDecimal.ONE;
//                                    for (int index = 0; index < coin.getDecimal(); index++) {
//                                        decimal = decimal.multiply(BigDecimal.TEN);
//                                    }
//
//                                    value = value.divide(decimal);
//                                    coin.setCurrentBalance(value.doubleValue());
//                                } else {
//                                    coin.setCurrentBalance(balanceJE.getAsDouble());
//                                }
//                            }
//                        }
//
//                        coin.setPublicAddress(publicAddress);
//
//                        //2.5e+20 => 2.5 * 10^20
//                        unknownCoins.add(coin);
//                    }
//                } else {
//                    CLog.d("doesn't have any coin.");
//                }
//            }
//
//        } else {
//            CLog.w("jsonObject is null");
//        }
//
//        return unknownCoins;
//    }

    private boolean isUnknownCoin(String coinNm) {
        return coinNm.equals("CNUS");
    }
//
//    public void requestInfuraErc20(final int accountNo, int addressIndex, String publicAddress) {
//        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
//                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
//                CoinUsConstants.API_INFURA_ERC20);
//
//        ApiInfura apiInfura = retrofit.createService(ApiInfura.class);
//        List<ERC20Data> erc20Data = getInfuraDummyData(accountNo, publicAddress);
//
//        if (erc20Data != null) {
//            Map<String, String> header = getInfuraDummyHeader();
//            Call<JsonArray> call = apiInfura.requestERC20(header, erc20Data);
//            try {
//                Response<JsonArray> response = call.execute();
////                Response<JsonArray> response = call.execute();
//                CLog.d("response : " + response.toString());
//                if (response.body() != null) {
//                    CLog.d("response : " + response.body().toString());
//                    JsonArray coinArray = response.body().getAsJsonArray();
//                    if (coinArray != null) {
//                        for (int index = 0; index < coinArray.size(); index++) {
//                            JsonObject coinJO = coinArray.get(index).getAsJsonObject();
//                            String id = coinJO.get("id").getAsString();
//                            String resultBalance = coinJO.get("result").getAsString();
//                            CLog.d("id : " + id + " resultBalance : " + resultBalance);
//
//                            if (mERC20ResponseMapper != null) {
//                                if (mERC20ResponseMapper.containsKey(id)) {
//                                    Erc20RequestDummyClass erc20DummyClass = mERC20ResponseMapper.get(id);
//                                    MyCoins myCoin = mLocalRequester.getMyCoins(
//                                            accountNo,
//                                            addressIndex,
//                                            -1,
//                                            erc20DummyClass.getCoinNm(),
//                                            erc20DummyClass.getSymbol(),
//                                            ServiceCoinType.ETHER.toString());
//
//
//                                    if (myCoin != null) {
//                                        double balance = getErc20BalanceFromInfura(resultBalance, myCoin.getDecimal());
//                                        if (balance != myCoin.getCurrentBalance()) {
//                                            myCoin.setCurrentBalance(balance);
//                                            mLocalRequester.updateMyCoins(myCoin);
//                                            CLog.d("requestInfuraErc20 - updateMyCoins balance.");
//                                        }
//                                    } else {
//                                        CLog.w("requestInfuraErc20 - can't find saved coin. : " + erc20DummyClass.getCoinNm() + " " + erc20DummyClass.getSymbol());
//                                    }
//                                }
//                            } else {
//                                CLog.w("requestInfuraErc20 - mERC20ResponseMapper is null");
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private double getErc20BalanceFromInfura(String resultBalance, int decimal) {
        char[] balanceChar = resultBalance.toCharArray();
        int startIndex = 0;
        char zeroHex = '0';
        boolean isZeroValue = true;

        for (int i = 0; i < balanceChar.length; i++) {
            if (i == 0 || i == 1) {
                if (balanceChar[i] == '0' || balanceChar[i] == 'x' || balanceChar[i] == 'X') {
                    continue;
                }
            }

            if (balanceChar[i] != zeroHex) {
                startIndex = i;
                isZeroValue = false;
                break;
            }
        }

        if (isZeroValue) {
            return 0.0;
        } else {
            resultBalance = resultBalance.substring(startIndex, resultBalance.length());
            BigInteger balance = new BigInteger(resultBalance, 16);
            CLog.d("trimmed resultBalance Hex : " + resultBalance);

            return new BigDecimal(balance).divide(new BigDecimal("10").pow(decimal)).doubleValue();
        }
    }

    private Map<String, String> getInfuraDummyHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json, text/plain, */*");
        header.put("accept-encoding", "gzip, deflate, br");
        header.put("accept-language", "en-US,en;q=0.9,ko;q=0.8");
        header.put("content-type", "application/json; charset=UTF-8");
        header.put("origin", "https://www.myetherwallet.com");
        header.put("pragma", "no-cache");
        header.put("referer", "https://www.myetherwallet.com/");
        header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");

        return header;
    }

    public TransactionData getDeserializedTxWithActions(JsonObject payload) {
        RetrofitServiceGenerator retrofit = new RetrofitServiceGenerator(
                HttpClient.getInstance(CoinUsApplication.getInstance().getApplicationContext()).getClient(),
                CoinUsConstants.API_COIN_US);

        CoinUsService coinUsService = retrofit.createService(CoinUsService.class);
        Call<JsonObject> call = coinUsService.getDeserializedTxWithActions(payload);

        try {
            Response<JsonObject> response = call.execute();
            if (response != null && response.body() != null) {
                CLog.d("result : " + response.body());
                CoinUsResponse coinUsResponse = new Gson().fromJson(response.body(), CoinUsResponse.class);
                if (coinUsResponse.getData() != null) {
                    return ((List<TransactionData>) coinUsResponse.getData().getItems(TransactionData.class)).get(0);
                } else {
                    CLog.w("coinUsResponse.getData() is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ERC20Data
     */
//    private long[] ids = new long[] {1074864716537L, CoinUsConstants.INFURA_ERC20_CNUS_RANDOM_ID};
//    private String[] to = new String[] {"0x59416A25628A76b4730eC51486114c32E0B582A1", "0x2C79794A9682b7c3889245dafe03A8fDFa414751"};    // Contract Address.
//    private String dataPrefix = "0x70a08231000000000000000000000000d7b4ed15480087d3c8b31c42d624d96779e009fc";
    private String dataPrefix = "0x70a08231000000000000000000000000";
    private String pending = "pending";
    private String jsonrpc = "2.0";
    private String method = "eth_call";

//
//    private List<ERC20Data> getInfuraDummyData(int accountNo, String publicAddress) {
//        List<MyCoins> myCoins = mLocalRequester.getMyCoins(accountNo, ServiceCoinType.ETHER.toString());
//
//        if (myCoins != null && myCoins.size() > 0) {
//            mERC20ResponseMapper = new HashMap<>();
//
//            for (MyCoins myCoin : myCoins) {
//                if (myCoin.getContractAddress() != null && myCoin.getContractAddress().length() > 0) {
//                    Erc20RequestDummyClass erc20DummyClass = new Erc20RequestDummyClass();
//                    erc20DummyClass.setAccountNo(myCoin.getAccountNo());
//                    erc20DummyClass.setCoinNm(myCoin.getCoinNm());
//                    erc20DummyClass.setSymbol(myCoin.getSymbol());
//                    erc20DummyClass.setContractAddress(myCoin.getContractAddress());
//                    erc20DummyClass.setId(CoinUsUtils.getRandomId(13));
//                    mERC20ResponseMapper.put(erc20DummyClass.getId(), erc20DummyClass);
//                    CLog.d("requestId : " + erc20DummyClass.getId());
//                }
//            }
//
//            if (mERC20ResponseMapper != null && mERC20ResponseMapper.size() > 0) {
//
//                Set<String> keySet = mERC20ResponseMapper.keySet();
//                List<ERC20Data> erc20DataList = new ArrayList<>();
//
//                if (keySet != null && keySet.size() > 0) {
//                    int index = 0;
//                    for (String key : keySet) {
//                        // fixme Infura의 M. E. W Api를 사용하면서 3개 이상의 request data를 보내면 에러남.
//                        if (index++ == 4) {
//                            break;
//                        }
//                        String id = key;
//                        String contractAddress = mERC20ResponseMapper.get(key).getContractAddress();
//
//                        Gson gson = new Gson();
//                        ERC20Data erc20 = new ERC20Data();
//                        erc20.setId(Long.valueOf(id));
//                        erc20.setJsonrpc(jsonrpc);
//                        erc20.setMethod(method);
//
//                        Map<String, String> param = new HashMap<>();
//                        param.put("to", contractAddress);
//                        if (publicAddress != null) {
//                            if (publicAddress.startsWith("0x")) {
//                                publicAddress = publicAddress.substring("0x".length());
//                            }
//                        }
//                        CLog.d("publicAddress : " + publicAddress);
//                        param.put("data", dataPrefix + publicAddress);
//
//                        Object[] params = new Object[] {
//                                param,
//                                pending
//                        };
//                        erc20.setParams(params);
//
//                        erc20DataList.add(erc20);
//                        String jsonString = gson.toJson(erc20);
//                        CLog.d("Erc20 Value : " + jsonString);
//                    }
//                    return erc20DataList;
//                }
//            } else {
//                CLog.w("Wrong Collection Initialized.");
//            }
//        } else {
//            CLog.d("myCoins is null");
//        }
//        return null;
//    }
//

    private class Erc20RequestDummyClass {
        private String id;
        private String contractAddress;
        private String coinNm;
        private String symbol;
        private long accountNo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String getCoinNm() {
            return coinNm;
        }

        public void setCoinNm(String coinNm) {
            this.coinNm = coinNm;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public long getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(long accountNo) {
            this.accountNo = accountNo;
        }
    }

    /**
     * Only For Test.
     * @param callback
     */
    public void getCnusCoin(final CoinUsResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                GeneralCoins coins = CoinUsConstants.getDummyGeneralCoin();
//                callback.onResultFetched(coins);
            }
        }).start();
    }

    public static String multipartRequest(String urlTo, Map<String, String> parmas, String filepath, String filefield, String fileMimeType)  {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("x-api-key", "7kFFOJBvOA9k0DT8yEKBk9mxkBIe7gxd9DMVUwJL");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
            }

            inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void main(String[] arg){

        Map<String, String> params = new HashMap();
        params.put("type", "passport");

        String result = multipartRequest("https://api.argos-solutions.io/v2/submissions/coinus@test.com/17ehn21jmbjnjx4/documents", params, "D:\\KakaoTalk_20180809_133626815.png", "file", "images/png");

        System.out.println(result);
    }
}
