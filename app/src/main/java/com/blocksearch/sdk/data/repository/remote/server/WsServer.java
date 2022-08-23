package com.blocksearch.sdk.data.repository.remote.server;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theblockchain.coinus.wallet.CoinUsApplication;
import com.theblockchain.coinus.wallet.R;
import com.theblockchain.coinus.wallet.common.CLog;
import com.theblockchain.coinus.wallet.common.CoinUsConstants;
import com.theblockchain.coinus.wallet.common.CoinUsPrefManager;
import com.theblockchain.coinus.wallet.common.CoinUsUtils;
import com.theblockchain.coinus.wallet.common.dialog.DefaultDialog;
import com.theblockchain.coinus.wallet.core.bip39.Mnemonic;
import com.theblockchain.coinus.wallet.core.coins.CoinType;
import com.theblockchain.coinus.wallet.core.coins.eos.EosCoins;
import com.theblockchain.coinus.wallet.core.security.otp.CoinUsOTPManager;
import com.theblockchain.coinus.wallet.core.wallet.CoinUsWallet;
import com.theblockchain.coinus.wallet.core.wallet.SimpleHDKeyChain;
import com.theblockchain.coinus.wallet.core.wallet.eos.converter.EtherToEosKeyConverter;
import com.theblockchain.coinus.wallet.core.wallet.eos.crypto.ec.EosPrivateKey;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.EoscDataManager;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.model.api.BinToJsonRespnose;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.model.api.JsonToBinResponse;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.model.chain.Action;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.model.chain.SignedTransaction;
import com.theblockchain.coinus.wallet.core.wallet.eos.eosio.util.Utils;
import com.theblockchain.coinus.wallet.data.CoinUsDataManager;
import com.theblockchain.coinus.wallet.data.CoinUsResponseCallback;
import com.theblockchain.coinus.wallet.data.CoinUsResponsePagingCallback;
import com.theblockchain.coinus.wallet.data.domain.atomic.PagingForm;
import com.theblockchain.coinus.wallet.data.domain.wallet.cnus.WalletDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.dapp.UserDappWalletDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.dapp.UserDappWhitelistConvertedDomain;
import com.theblockchain.coinus.wallet.data.domain.wallet.dapp.UserDappWhitelistDomain;
import com.theblockchain.coinus.wallet.data.repository.local.scheme.CoinUsAccount;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterAccountData;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterAccountsResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterActionsData;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterApiAuthenticateResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterApiData;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterApiResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterApiResultResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterAuthorizationData;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterFailureResultResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterIdentityResultResponse;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterPairData;
import com.theblockchain.coinus.wallet.data.repository.remote.server.DataType.ScatterSigResultAcceptResponse;

import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;

public class WsServer extends WebSocketServer {

    public static final int LOGIN_ACTION_TYPE = 0;
    public static final int TRANSFER_ACTION_TYPE = 1;

    public static final String DATA_TYPE_NAME = "CoinUsMethod";
    public static final String DATA_TYPE_CONTRACT = "CoinUsContract";

    public static final String COINUS_MUTABLE = "CoinUsMutable";

    private static final String LOCALHOST = "127.0.0.1";
    private static final String SCATTER_SIG = "42/scatter,";
    private static final String SCATTER_CONNECTED = "[\"connected\"]";
    private static final String SCATTER_PAIRED = "[\"paired\",%b]";
    private static final String SCATTER_REKEY = "[\"rekey\"]";
    private static final String SCATTER_API = "[\"api\",%s]";

    private static final String SCATTER_LOGOUT = "[\"event\",{\"event\":\"logout\",\"payload\":{}}]";
    private static final String SCATTER_DCED = "[\"event\",{\"event\":\"dced\",\"payload\":{}}]";

    private static final long PREDEFINED_EXPIRATION = 600000;

    private static final String WHITELIST_DATA_FORMAT = "{\"toAddr\":\"%s\",\"amount\":\"%s\"}";

    private boolean paused = false;

    private ExecutorService executorService;

    private WalletDomain mWalletDomain = null;

    private String scatterHash;

    private long mEosDappWalletIndex = -1;

    private boolean mbNoEosCard = true;

    private boolean mbSorted = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    private Context mContext;

    private static final String possible_string = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private ConcurrentHashMap<String, EosConnection> mEosDappMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CountDownLatch> mEosDappLatch = new ConcurrentHashMap<>();

    private List<UserDappWhitelistConvertedDomain> mUserDappConvertedWhitelist = new ArrayList<>();

    private List<String> mTemporaryLoginAllow = new ArrayList<>();
    private long mLastLoginAttempt;

    public WsServer(int port) {
        super(new InetSocketAddress(LOCALHOST, port));
        setReuseAddr(true);
        executorService = Executors.newFixedThreadPool(4);
        getDappUserWallet(CoinUsConstants.CNUS_COIN_ID_EOS);
        getUserWhiteListAll(null, new PagingForm());

        mContext = CoinUsApplication.getInstance().getApplicationContext();
        scatterHash = CoinUsPrefManager.getScatterHash(mContext);
        if("".equals(scatterHash)) {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < 2048; i++) {
                builder.append(possible_string.charAt((int) Math.floor(Math.random()*possible_string.length())));
            }
            scatterHash = CoinUsUtils.getSha256WithHex(builder.toString());
            CoinUsPrefManager.setScatterHash(mContext, scatterHash);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    public void reloadData() {
        getDappUserWallet(CoinUsConstants.CNUS_COIN_ID_EOS);
        mUserDappConvertedWhitelist.clear();
        getUserWhiteListAll(null, new PagingForm());
    }

    public synchronized List<UserDappWhitelistConvertedDomain> getUserDappWhitelistConverted() {
        if(!mbSorted) {
            mbSorted = true;
            Collections.sort(mUserDappConvertedWhitelist, new Comparator<UserDappWhitelistConvertedDomain>() {
                @Override
                public int compare(UserDappWhitelistConvertedDomain o1, UserDappWhitelistConvertedDomain o2) {
                    int compareResult = o1.getOrigin().compareTo(o2.getOrigin());
                    if(compareResult == 0) {
                        if(o1.getActionType() == 0) {
                            return -1;
                        } else if(o2.getActionType() == 0) {
                            return 1;
                        } else if(o1.getActionType() == o2.getActionType()) {
                            compareResult = checkWhitelistEquality(o1.getData(), o2.getData());
                        } else {
                            compareResult = o2.getActionType() - o1.getActionType();
                        }
                    }
                    return compareResult;
                }
            });
        }
        return mUserDappConvertedWhitelist;
    }

    public synchronized void setmUserDappConvertedWhitelist(List<UserDappWhitelistConvertedDomain> theList) {
        mUserDappConvertedWhitelist = theList;
    }

    private void getDappUserWallet(long _coinId) {
        CoinUsDataManager.getInstance().getUserDappWallet(
                String.valueOf(_coinId),
                new CoinUsResponseCallback<List<UserDappWalletDomain>>() {
                    @Override
                    public void onResultFetched(List<UserDappWalletDomain> response) {
                        if(response != null && response.size() > 0) {
                            UserDappWalletDomain userDappWalletDomain = response.get(0);
                            long resCoinId = userDappWalletDomain.getCoinId();
                            if (resCoinId == CoinUsConstants.CNUS_COIN_ID_EOS) {
                                mEosDappWalletIndex = userDappWalletDomain.getWalletAddressIndex();
                                getDappCandidatesWallet();
                            }
                        } else {
                            CLog.w("No Eos Dapp Wallet set");
                            mEosDappWalletIndex = -1;
                            mWalletDomain = null;
                        }
                        executorService.execute(new MessageHandler(null, null, null));
                    }

                    @Override
                    public void onResultFailed(int code, String message) {
                        CLog.w("code : " + code + " message : " + message);
                        mEosDappWalletIndex = -1;
                        mWalletDomain = null;
                    }
                });
    }

    private void getDappCandidatesWallet() {
        PagingForm pagingForm = new PagingForm();
        pagingForm.setPageSize(255);
        CoinUsDataManager.getInstance().getWalletList(
                String.valueOf(CoinUsConstants.CNUS_COIN_ID_EOS),
                null,
                CoinUsConstants.DISPLAY_TRUE,
                null,
                pagingForm,
                new CoinUsResponseCallback<List<WalletDomain>>() {
                    @Override
                    public void onResultFetched(List<WalletDomain> response) {
                        if (response != null && response.size() > 0) {
                            if (mEosDappWalletIndex != -1) {
                                WalletDomain wDomain;
                                for (int i = 0; i < response.size(); i++) {
                                    wDomain = response.get(i);
                                    if (wDomain.getWalletAddressIndex() == mEosDappWalletIndex) {
                                        mWalletDomain = wDomain;
                                        break;
                                    }
                                }
                            }
                        } else {
                            CLog.w("response is null or empty");
                        }
                    }

                    @Override
                    public void onResultFailed(int code, String message) {
                        CLog.w("code : " + code + " message : " + message);
                    }
                });
    }

    private synchronized void getUserWhiteListAll(final String keyword, PagingForm pagingForm) {

        CoinUsDataManager.getInstance().getDappWhiltelist(
                keyword,
                pagingForm,
                new CoinUsResponsePagingCallback<List<UserDappWhitelistDomain>>() {

                    @Override
                    public void onResultFetched(List<UserDappWhitelistDomain> response, PagingForm pagingForm) {
                        if (response != null && response.size() > 0) {
                            for(UserDappWhitelistDomain userDappWhitelistDomain : response) {
                                mUserDappConvertedWhitelist.add(UserDappWhitelistConvertedDomain.toUserDappWhitelistConvertedDomain(userDappWhitelistDomain));
                            }
                            if(pagingForm.isHasMore()){
                                pagingForm.setPageNo(pagingForm.getPageNo() + 1);
                                getUserWhiteListAll(keyword, pagingForm);
                            }
                        } else {
                            CLog.w("response is null");
                        }
                    }

                    @Override
                    public void onResultFailed(int code, String message) {
                        CLog.w("code : " + code + " message : " + message);
                    }
                });
    }

    public void setEosDappWallet(WalletDomain walletDomain) {
        mWalletDomain = walletDomain;
        mEosDappWalletIndex = walletDomain.getWalletAddressIndex();
    }

    public WalletDomain getEosDappWallet() {
        return mWalletDomain;
    }

    public boolean isEosDappWalletSet() {
        return mWalletDomain != null;
    }

    public long getmEosDappWalletIndex() {
        return mEosDappWalletIndex;
    }

    public void syncWhitelistDeletion(List<UserDappWhitelistConvertedDomain> theList) {
        for (UserDappWhitelistConvertedDomain userDappWhitelistConvertedDomain : theList) {
            Iterator<UserDappWhitelistConvertedDomain> iterator = mUserDappConvertedWhitelist.iterator();
            while (iterator.hasNext()) {
                UserDappWhitelistConvertedDomain userDappWhitelistDomain = iterator.next();

                if(userDappWhitelistConvertedDomain.getSeq() == userDappWhitelistDomain.getSeq()) {
                    iterator.remove();
                    CLog.d("whitelist removed : " + userDappWhitelistConvertedDomain.getSeq());
                    break;
                }
            }
        }
    }

    public void pause() {
        CLog.w("WS paused!!!!");
        paused = true;
    }

    public void resume() {
        CLog.w("WS resumed!!!!");
        paused = false;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        CLog.w("[WsServer] [onOpen]" + (conn.getRemoteSocketAddress() != null ? conn.getRemoteSocketAddress() : "NULL") + "[" + handshake.getResourceDescriptor() + "]");
        conn.send(String.format("%s%s", SCATTER_SIG, SCATTER_CONNECTED));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        CLog.w("[WsServer] [onClose]" + (conn.getRemoteSocketAddress() != null ? conn.getRemoteSocketAddress() : "NULL"));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.w("WsServer", message);
        if(!paused) {
            if(message.indexOf(SCATTER_SIG) == -1) return;
            String strippedMsg = message.replace(SCATTER_SIG, "").replace("\":,", "\":\"\",").replace("\":}", "\":\"\"}").substring(1);
            strippedMsg = strippedMsg.substring(0, strippedMsg.length() - 1);
            CLog.w(strippedMsg);
            int typeIndex = strippedMsg.indexOf(',');
            String type = strippedMsg.substring(0,typeIndex);
            String data = strippedMsg.substring(typeIndex + 1);

            JsonParser parser = new JsonParser();
            JsonObject jsonData = parser.parse(data).getAsJsonObject();
            MessageHandler msgHandler = new MessageHandler(conn, type, jsonData);
            if(mWalletDomain != null) {
                executorService.execute(msgHandler);
            } else {
                CLog.e("no eos wallet domain set. disregarding message received");
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(!paused) {
            if(conn != null) {
                CLog.w("[WsServer] [onError]" + (conn.getRemoteSocketAddress() != null ? conn.getRemoteSocketAddress() : "NULL") + "[" + ex.getLocalizedMessage() + "]");
            } else {
                CLog.w("[WsServer] [onError] conn is null");
            }
        }
    }

    @Override
    public void onStart() {
        CLog.w("[WsServer] [onStart]");
        setConnectionLostTimeout(0);
    }

    @Data
    private class EosConnection {
        private String appKey;
        private long expiration;
        private String origin;
        private String plugin;
        private WebSocket webSocket;
        private boolean paired;
    }

    private UserDappWhitelistConvertedDomain getTransferActionTypeSeq(String origin, String contractName, String methodName) {
        JsonObject data;
        for(UserDappWhitelistConvertedDomain userDappWhitelistConvertedDomain : mUserDappConvertedWhitelist) {
            if(userDappWhitelistConvertedDomain.getActionType() == TRANSFER_ACTION_TYPE && userDappWhitelistConvertedDomain.getOrigin().equals(origin)) {
                data = userDappWhitelistConvertedDomain.getData();
                JsonElement contract = data.get(DATA_TYPE_CONTRACT);
                JsonElement method = data.get(DATA_TYPE_NAME);
                if(contract != null && contract.getAsString().equals(contractName) &&
                    method != null && method.getAsString().equals(methodName)) {
                    return userDappWhitelistConvertedDomain;
                }
            }
        }
        return null;
    }

    private boolean checkWhitelistExists(String origin, JsonObject requestedData, int actionType) {
        switch (actionType) {
            case LOGIN_ACTION_TYPE : {
                for (UserDappWhitelistConvertedDomain userDappWhitelistConvertedDomain : mUserDappConvertedWhitelist) {
                    if (actionType == userDappWhitelistConvertedDomain.getActionType() && origin.equals(userDappWhitelistConvertedDomain.getOrigin())) {
                        return true;
                    }
                }
            }
            break;
            case TRANSFER_ACTION_TYPE : {
                String actionName = requestedData.get(DATA_TYPE_NAME).getAsString();
                for(UserDappWhitelistConvertedDomain userDappWhitelistConvertedDomain : mUserDappConvertedWhitelist) {
                    JsonObject data = userDappWhitelistConvertedDomain.getData();
                    if (actionType == userDappWhitelistConvertedDomain.getActionType() && origin.equals(userDappWhitelistConvertedDomain.getOrigin())) {
                        String dappActionName = data.get(DATA_TYPE_NAME).getAsString();
                        if(dappActionName.equals(actionName)) {
                            Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
                            String valueAsStr;
                            boolean allMatchFound = true;
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                valueAsStr = entry.getValue().toString();
                                if (!valueAsStr.equals("\"" + COINUS_MUTABLE + "\"") && (requestedData.get(entry.getKey()) == null || !valueAsStr.equals(requestedData.get(entry.getKey()).toString()))) {
                                    allMatchFound = false;
                                    break;
                                }
                            }
                            if(allMatchFound) {
                                return true;
                            }
                        }
                    }
                }
            }
            break;
        }
        return false;
    }

    private int checkWhitelistEquality(JsonObject aWhitelist, JsonObject inCheck) {
        int result;
        JsonElement aWhitelistContract = aWhitelist.get(DATA_TYPE_CONTRACT);
        String aWhitelistContractStr = aWhitelistContract != null ? aWhitelistContract.getAsString() : "";
        JsonElement aInCheckContract = inCheck.get(DATA_TYPE_CONTRACT);
        String aInCheckContractStr = aInCheckContract != null ? aInCheckContract.getAsString() : "";
        result = aWhitelistContractStr.compareTo(aInCheckContractStr);
        if(result == 0) {
            JsonElement aWhitelistMethod = aWhitelist.get(DATA_TYPE_NAME);
            String aWhitelistMethodStr = aWhitelistMethod != null ? aWhitelistMethod.getAsString() : "";
            JsonElement aInCheckMethod = inCheck.get(DATA_TYPE_NAME);
            String aInCheckMethodStr = aInCheckMethod != null ? aInCheckMethod.getAsString() : "";
            result = aWhitelistMethodStr.compareTo(aInCheckMethodStr);
        }
        return result;
    }

    public void setEosCardEmpty(boolean isEmpty) {
        mbNoEosCard = isEmpty;
    }

    public boolean checkEosCardEmpty() {
        return mbNoEosCard;
    }

    public void releaseLatch(String appKey) {
        CountDownLatch theLatch = mEosDappLatch.get(appKey);
        if(theLatch != null) {
            theLatch.countDown();
            CLog.i("Releasing latch for : " + appKey);
        } else {
            CLog.i("Latch not valid : " + appKey);
        }
    }

    public void clearTemporaryLoginAllow() {
        mTemporaryLoginAllow.clear();
    }

    private class MessageHandler implements Runnable{
        private WebSocket mConn;
        private String mType;
        private JsonObject mJsonData;
        private AtomicBoolean mbProceedConfirmed = new AtomicBoolean(false);
        private AtomicBoolean mbWhiltelistAddCOnfirmed = new AtomicBoolean(false);

        private Long seqId;

        private List<String> mCheckedKeys = new ArrayList<>();

        private static final String COMM_PAIR = "\"pair\"";
        private static final String COMM_REKEYED = "\"rekeyed\"";
        private static final String COMM_API = "\"api\"";
        private static final String COMM_EVENT = "\"event\"";

        private static final String SUBTYPE_REQUEST_ID = "getOrRequestIdentity";
        private static final String SUBTYPE_IDENTITY_FROM_PERMISSION = "identityFromPermissions";
        private static final String SUBTYPE_REQUEST_SIG = "requestSignature";
        private static final String SUBTYPE_FORGET_IDENTITY = "forgetIdentity";
        private static final String SUBTYPE_AUTHENTICATE = "authenticate";
        private static final String SUBTYPE_REQUEST_ARBITRARY_SIGNATURE = "requestArbitrarySignature";
        private static final String SUBTYPE_REQUEST_ADD_NETWORK = "requestAddNetwork";

        private static final String EOS_BLOCKCHAIN = "eos";
        private static final String EOS_MAINNET_CHAINID = "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906";

        private static final String INVALID_NONCE = "invalid_nonce";
        private static final String NONCE_MUST_BE_12_CHARACTERS = "You must provide a 12 character nonce for authentication";

        private static final String SIGNATURE_REJECTED_TYPE = "signature_rejected";
        private static final String SIGNATURE_REJECTED_MSG = "User rejected the signature reuqest";

        public MessageHandler(WebSocket conn, String type, JsonObject jsonData) {
            mConn = conn;
            mType = type;
            mJsonData = jsonData;
        }

        @Override
        public void run() {
            if(mConn == null) {
                getWalletList();
            } else {
                String responseStr;
                try {
                    switch (mType) {
                        case COMM_PAIR:
                            if (mJsonData.has("data")) {
                                Gson gson = new Gson();
                                ScatterPairData pairData = gson.fromJson(mJsonData, ScatterPairData.class);
                                String appKey = pairData.getData().getAppkey();
                                String hashedAppKey = appKey.contains("appkey:") ? CoinUsUtils.getSha256WithHex(appKey) : appKey;
                                if (mEosDappMap.containsKey(hashedAppKey)) {
                                    EosConnection eosConnection = mEosDappMap.get(hashedAppKey);
                                    long curTime = System.currentTimeMillis();
                                    if (eosConnection.getExpiration() < curTime) {
                                        mEosDappMap.remove(hashedAppKey);
                                        responseStr = String.format("%s%s", SCATTER_SIG, SCATTER_REKEY);
                                        CLog.w("sending : " + responseStr);
                                        mConn.send(responseStr);
                                    } else {
                                        curTime += PREDEFINED_EXPIRATION;
                                        eosConnection.setExpiration(curTime);
                                        eosConnection.setWebSocket(mConn);
                                        eosConnection.setPaired(true);
                                        mEosDappMap.put(hashedAppKey, eosConnection);
                                        responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, true));
                                        CLog.w("sending : " + responseStr);
                                        mConn.send(responseStr);
                                    }
                                } else {
                                    EosConnection eosConnection = new EosConnection();
                                    eosConnection.setAppKey(hashedAppKey);
                                    eosConnection.setOrigin(pairData.getData().getOrigin());
                                    long expiration = System.currentTimeMillis() + PREDEFINED_EXPIRATION;
                                    eosConnection.setExpiration(expiration);
                                    eosConnection.setWebSocket(mConn);
                                    eosConnection.setPlugin(pairData.getPlugin());
                                    eosConnection.setPaired(true);
                                    mEosDappMap.put(hashedAppKey, eosConnection);
                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, true));
                                    CLog.w("sending : " + responseStr);
                                    mConn.send(responseStr);
                                }
                            }
                            break;
                        case COMM_REKEYED:
                            if (mJsonData.has("data")) {
                                Gson gson = new Gson();
                                ScatterPairData pairData = gson.fromJson(mJsonData, ScatterPairData.class);
                                String appKey = pairData.getData().getAppkey();
                                String hashedAppKey = appKey.contains("appkey:") ? CoinUsUtils.getSha256WithHex(appKey) : appKey;
                                EosConnection eosConnection = new EosConnection();
                                eosConnection.setAppKey(hashedAppKey);
                                eosConnection.setOrigin(pairData.getData().getOrigin());
                                long expiration = System.currentTimeMillis() + PREDEFINED_EXPIRATION;
                                eosConnection.setExpiration(expiration);
                                eosConnection.setWebSocket(mConn);
                                eosConnection.setPlugin(pairData.getPlugin());
                                eosConnection.setPaired(true);
                                mEosDappMap.put(hashedAppKey, eosConnection);
                                responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, true));
                                CLog.w("sending : " + responseStr);
                                mConn.send(responseStr);
                            }
                            break;
                        case COMM_API:
                            if (mJsonData.has("data")) {
                                Gson gson = new Gson();
                                ScatterApiData apiData = gson.fromJson(mJsonData, ScatterApiData.class);
                                final String appKey = apiData.getData().getAppkey();
                                final EosConnection eosConnection = mEosDappMap.get(appKey);
                                CoinUsApplication coinUsApp = CoinUsApplication.getInstance();
                                if (eosConnection != null) {
                                    String subType = apiData.getData().getType();
                                    String subTypeId = apiData.getData().getId();
                                    ScatterApiResponse apiResponse = new ScatterApiResponse();
                                    apiResponse.setId(subTypeId);
                                    if (eosConnection.isPaired()) {
                                        switch (subType) {
                                            case SUBTYPE_REQUEST_ID: {
                                                long curTime = System.currentTimeMillis();
                                                if(curTime - mLastLoginAttempt > 3000) {
                                                    mTemporaryLoginAllow.clear();
                                                }
                                                if (!mTemporaryLoginAllow.contains(eosConnection.getOrigin()) && !checkWhitelistExists(eosConnection.getOrigin(), null, LOGIN_ACTION_TYPE)) {
                                                    CountDownLatch theLatch = mEosDappLatch.get(appKey);
                                                    if (theLatch == null) {
                                                        theLatch = new CountDownLatch(1);
                                                        mEosDappLatch.put(appKey, theLatch);
                                                        String titleMessage = String.format("%s\n\"%s\"\n%s", coinUsApp.getActivityContext().getString(R.string.dapp_4), eosConnection.getOrigin(), coinUsApp.getActivityContext().getString(R.string.dapp_23));
                                                        Spannable content = new SpannableString(titleMessage);
                                                        int startIndex = titleMessage.indexOf(eosConnection.getOrigin()) - 1;
                                                        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(coinUsApp.getActivityContext(), R.color.receive_menu_eos)), startIndex, startIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        content.setSpan(new StyleSpan(Typeface.BOLD), startIndex, startIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        coinUsApp.confirmDappPermission(null, null, content, null, null, coinUsApp.getActivityContext().getString(R.string.dapp_5), new SpannableString(coinUsApp.getActivityContext().getString(R.string.dapp_6)), CoinUsConstants.CNUS_COIN_ID_EOS, new DefaultDialog.DialogYesOrNoWithCheckListener() {
                                                            @Override
                                                            public void onYesClicked(DefaultDialog defaultDialog, String value, boolean isChecked, List checkedKeys) {
                                                                mTemporaryLoginAllow.add(eosConnection.getOrigin());
                                                                mLastLoginAttempt = System.currentTimeMillis();
                                                                final CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                if (curLatch != null) {
                                                                    mbProceedConfirmed.set(true);
                                                                    if (isChecked) {
                                                                        CoinUsDataManager.getInstance().insertDappWhitelist(eosConnection.getOrigin(), "", LOGIN_ACTION_TYPE, new CoinUsResponseCallback<Long>() {
                                                                            @Override
                                                                            public void onResultFetched(Long response) {
                                                                                if (response.intValue() != -1) {
                                                                                    seqId = response;
                                                                                    mbWhiltelistAddCOnfirmed.set(true);
                                                                                }
                                                                                curLatch.countDown();
                                                                            }

                                                                            @Override
                                                                            public void onResultFailed(int code, String message) {
                                                                                CLog.e(code + " : " + message);
                                                                                curLatch.countDown();
                                                                            }
                                                                        });
                                                                    } else {
                                                                        curLatch.countDown();
                                                                    }
                                                                }
                                                                defaultDialog.dismissAllowingStateLoss();
                                                            }

                                                            @Override
                                                            public void onNoClicked(DefaultDialog defaultDialog) {
                                                                CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                if (curLatch != null) {
                                                                    curLatch.countDown();
                                                                }
                                                                defaultDialog.dismissAllowingStateLoss();
                                                            }
                                                        });
                                                    } else {
                                                        CLog.w("already processing a request. Stop proceeding");
                                                        return;
                                                    }
                                                    try {
                                                        theLatch.await(60, TimeUnit.SECONDS);
                                                    } catch (InterruptedException e) {
                                                        CLog.e(e.getMessage());
                                                    }
                                                    mEosDappLatch.remove(appKey);
                                                } else {
                                                    if(mTemporaryLoginAllow.contains(eosConnection.getOrigin())) {
                                                        mTemporaryLoginAllow.remove(eosConnection.getOrigin());
                                                    }
                                                    mbProceedConfirmed.set(true);
                                                }
                                                if (mbWhiltelistAddCOnfirmed.get()) {
                                                    UserDappWhitelistConvertedDomain domain = new UserDappWhitelistConvertedDomain(seqId.intValue(), eosConnection.getOrigin(), null, LOGIN_ACTION_TYPE);
                                                    mUserDappConvertedWhitelist.add(domain);
                                                    mbSorted = false;
                                                }
                                                if (mbProceedConfirmed.get()) {
                                                    ScatterIdentityResultResponse resultResponse = new ScatterIdentityResultResponse();
                                                    JsonElement accounts = apiData.getData().getPayload().getFields().getAccounts();
                                                    ScatterAccountData accountData = accounts instanceof JsonObject ? gson.fromJson(accounts, ScatterAccountData.class) : gson.fromJson(accounts, ScatterAccountData[].class)[0];
                                                    resultResponse.setHash(scatterHash);
                                                    resultResponse.setPublicKey(mWalletDomain.getWalletAddress());
                                                    resultResponse.setName(mWalletDomain.getWalletAddressNm());
                                                    resultResponse.setKyc(false);
                                                    List<ScatterAccountsResponse> responseAccountList = new ArrayList<>();
                                                    ScatterAccountsResponse responseAccountData = new ScatterAccountsResponse();
                                                    responseAccountData.setName(mWalletDomain.getWalletAddressNm());
                                                    responseAccountData.setAuthority("active");
                                                    responseAccountData.setPublicKey(mWalletDomain.getWalletAddress());
                                                    if (accountData != null) {
                                                        responseAccountData.setBlockchain(accountData.getBlockchain());
                                                        responseAccountData.setChainId(accountData.getChainId());
                                                    } else {
                                                        responseAccountData.setBlockchain(EOS_BLOCKCHAIN);
                                                        responseAccountData.setChainId(EOS_MAINNET_CHAINID);
                                                    }
                                                    responseAccountData.setHardware(false);
                                                    responseAccountList.add(responseAccountData);
                                                    resultResponse.setAccounts(responseAccountList);
                                                    apiResponse.setResult(resultResponse);
                                                    responseStr = gson.toJson(apiResponse);
                                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                    CLog.w("sending " + responseStr);
                                                    mConn.send(responseStr);
                                                } else {
                                                    // Needs to check
                                                    eosConnection.setPaired(false);
                                                    mEosDappMap.put(appKey, eosConnection);
                                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, false));
                                                    CLog.w("sending : " + responseStr);
                                                    mConn.send(responseStr);
                                                }
                                            }
                                            break;
                                            case SUBTYPE_IDENTITY_FROM_PERMISSION: {
                                                long curTime = System.currentTimeMillis();
                                                if(curTime - mLastLoginAttempt > 3000) {
                                                    mTemporaryLoginAllow.clear();
                                                }
                                                if (!mTemporaryLoginAllow.contains(eosConnection.getOrigin()) && !checkWhitelistExists(eosConnection.getOrigin(), null, LOGIN_ACTION_TYPE)) {
                                                    CountDownLatch theLatch = mEosDappLatch.get(appKey);
                                                    if (theLatch == null) {
                                                        theLatch = new CountDownLatch(1);
                                                        mEosDappLatch.put(appKey, theLatch);
                                                        String titleMessage = String.format("%s\n\"%s\"\n%s", coinUsApp.getActivityContext().getString(R.string.dapp_4), eosConnection.getOrigin(), coinUsApp.getActivityContext().getString(R.string.dapp_23));
                                                        Spannable content = new SpannableString(titleMessage);
                                                        int startIndex = titleMessage.indexOf(eosConnection.getOrigin()) - 1;
                                                        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(coinUsApp.getActivityContext(), R.color.receive_menu_eos)), startIndex, startIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        content.setSpan(new StyleSpan(Typeface.BOLD), startIndex, startIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        coinUsApp.confirmDappPermission(null, null, content, null, null, coinUsApp.getActivityContext().getString(R.string.dapp_5), new SpannableString(coinUsApp.getActivityContext().getString(R.string.dapp_6)), CoinUsConstants.CNUS_COIN_ID_EOS, new DefaultDialog.DialogYesOrNoWithCheckListener() {
                                                            @Override
                                                            public void onYesClicked(DefaultDialog defaultDialog, String value, boolean isChecked, List checkedKeys) {
                                                                mTemporaryLoginAllow.add(eosConnection.getOrigin());
                                                                mLastLoginAttempt = System.currentTimeMillis();
                                                                final CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                if (curLatch != null) {
                                                                    mbProceedConfirmed.set(true);
                                                                    if (isChecked) {
                                                                        CoinUsDataManager.getInstance().insertDappWhitelist(eosConnection.getOrigin(), "", LOGIN_ACTION_TYPE, new CoinUsResponseCallback<Long>() {
                                                                            @Override
                                                                            public void onResultFetched(Long response) {
                                                                                if (response.intValue() != -1) {
                                                                                    seqId = response;
                                                                                    mbWhiltelistAddCOnfirmed.set(true);
                                                                                }
                                                                                curLatch.countDown();
                                                                            }

                                                                            @Override
                                                                            public void onResultFailed(int code, String message) {
                                                                                CLog.e(code + " : " + message);
                                                                                curLatch.countDown();
                                                                            }
                                                                        });
                                                                    } else {
                                                                        curLatch.countDown();
                                                                    }
                                                                }
                                                                defaultDialog.dismissAllowingStateLoss();
                                                            }

                                                            @Override
                                                            public void onNoClicked(DefaultDialog defaultDialog) {
                                                                CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                if (curLatch != null) {
                                                                    curLatch.countDown();
                                                                }
                                                                defaultDialog.dismissAllowingStateLoss();
                                                            }
                                                        });
                                                    } else {
                                                        CLog.w("already processing a request. Stop proceeding");
                                                        return;
                                                    }
                                                    try {
                                                        theLatch.await(60, TimeUnit.SECONDS);
                                                    } catch (InterruptedException e) {
                                                        CLog.e(e.getMessage());
                                                    }
                                                    mEosDappLatch.remove(appKey);
                                                } else {
                                                    if(mTemporaryLoginAllow.contains(eosConnection.getOrigin())) {
                                                        mTemporaryLoginAllow.remove(eosConnection.getOrigin());
                                                    }
                                                    mbProceedConfirmed.set(true);
                                                }
                                                if (mbWhiltelistAddCOnfirmed.get()) {
                                                    UserDappWhitelistConvertedDomain domain = new UserDappWhitelistConvertedDomain(seqId.intValue(), eosConnection.getOrigin(), null, LOGIN_ACTION_TYPE);
                                                    mUserDappConvertedWhitelist.add(domain);
                                                    mbSorted = false;
                                                }
                                                if (mbProceedConfirmed.get()) {
                                                    ScatterIdentityResultResponse resultResponse = new ScatterIdentityResultResponse();
                                                    resultResponse.setHash(scatterHash);
                                                    resultResponse.setPublicKey(mWalletDomain.getWalletAddress());
                                                    resultResponse.setName(mWalletDomain.getWalletAddressNm());
                                                    resultResponse.setKyc(false);
                                                    List<ScatterAccountsResponse> responseAccountList = new ArrayList<>();
                                                    ScatterAccountsResponse responseAccountData = new ScatterAccountsResponse();
                                                    responseAccountData.setName(mWalletDomain.getWalletAddressNm());
                                                    responseAccountData.setAuthority("active");
                                                    responseAccountData.setPublicKey(mWalletDomain.getWalletAddress());
                                                    responseAccountData.setBlockchain(EOS_BLOCKCHAIN);
                                                    responseAccountData.setChainId(EOS_MAINNET_CHAINID);
                                                    responseAccountData.setHardware(false);
                                                    responseAccountList.add(responseAccountData);
                                                    resultResponse.setAccounts(responseAccountList);
                                                    apiResponse.setResult(resultResponse);
                                                    responseStr = gson.toJson(apiResponse);
                                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                    CLog.w("sending " + responseStr);
                                                    mConn.send(responseStr);
                                                } else {
                                                    // Needs to check
                                                    eosConnection.setPaired(false);
                                                    mEosDappMap.put(appKey, eosConnection);
                                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, false));
                                                    CLog.w("sending : " + responseStr);
                                                    mConn.send(responseStr);
                                                }
                                            }
                                            break;
                                            case SUBTYPE_REQUEST_SIG: {
                                                CoinUsAccount account = CoinUsWallet.getInstance().getCurrentAccount();
                                                if (account != null) {
                                                    int sigRequestType = 0;
                                                    ScatterApiData.RequestIdData.PayloadData.TransactionData transactionData = apiData.getData().getPayload().getTransaction();
                                                    boolean allChecklistVerified = true;
                                                    List<JsonObject> jsonBinArgs = new ArrayList<>();
                                                    Map<String, String> mssArgs = new HashMap<>();
                                                    List<Action> actionsList;
                                                    if(transactionData.getActions() != null) {
                                                        actionsList = getActions(gson, transactionData.getActions());
                                                        BinToJsonRespnose binResponse;
                                                        JsonObject binResponseArgs;
                                                        for (Action action : actionsList) {
                                                            binResponse = EoscDataManager.transformBinToJson(action.getAccount(), action.getName(), action.getData().getAsString());
                                                            if (binResponse != null) {
                                                                binResponseArgs = binResponse.getArgs();
                                                                binResponseArgs.addProperty(DATA_TYPE_CONTRACT, action.getAccount());
                                                                binResponseArgs.addProperty(DATA_TYPE_NAME, action.getName());
                                                                if (allChecklistVerified) {
                                                                    allChecklistVerified = checkWhitelistExists(eosConnection.getOrigin(), binResponseArgs, TRANSFER_ACTION_TYPE);
                                                                }
                                                                jsonBinArgs.add(binResponseArgs);
                                                                String keyStr = action.getName() + "\n(" + action.getAccount() + ")";
                                                                String tmpStr = CoinUsUtils.getDappArgsAsFormattedString(binResponseArgs, true, getTransferActionTypeSeq(eosConnection.getOrigin(), action.getAccount(), action.getName()));
                                                                mssArgs.put(keyStr, tmpStr);
                                                                CLog.d(tmpStr);
                                                            } else {
                                                                CLog.d("[REQUEST_SIG] binResponse is null");
                                                            }
                                                        }
                                                    } else {
                                                        sigRequestType = 1;
                                                        actionsList = new ArrayList<>();
                                                        JsonObject payload = mJsonData.getAsJsonObject("data").getAsJsonObject("payload");
                                                        transactionData = CoinUsDataManager.getInstance().getDeserializedTxWithActions(payload);
                                                        JsonArray actionsListJsonArray = transactionData.getActions().getAsJsonArray();
                                                        JsonObject binResponse;
                                                        JsonObject binResponseArgs;
                                                        String actionName, actionAccount;
                                                        JsonToBinResponse jsonResponse;
                                                        Action newAction;
                                                        for (int i = 0; i < actionsListJsonArray.size(); i++) {
                                                            binResponse = actionsListJsonArray.get(i).getAsJsonObject();
                                                            binResponseArgs = binResponse.get("data").getAsJsonObject();
                                                            actionName = binResponse.get("name").getAsString();
                                                            actionAccount = binResponse.get("account").getAsString();
//                                                            jsonResponse = EoscDataManager.transformJsonToBin(actionAccount, actionName, binResponseArgs.toString());
//                                                            if(jsonResponse != null) {
                                                            newAction = new Action(actionAccount, actionName);
                                                            newAction.setAuthorization(getAuthorization(gson, binResponse.get("authorization")));
//                                                            newAction.setData(jsonResponse.getBinargs());
                                                            actionsList.add(newAction);
                                                            binResponseArgs.addProperty(DATA_TYPE_CONTRACT, actionAccount);
                                                            binResponseArgs.addProperty(DATA_TYPE_NAME, actionName);
                                                            if (allChecklistVerified) {
                                                                allChecklistVerified = checkWhitelistExists(eosConnection.getOrigin(), binResponseArgs, TRANSFER_ACTION_TYPE);
                                                            }
                                                            jsonBinArgs.add(binResponseArgs);
                                                            String keyStr = actionName + "\n(" + actionAccount + ")";
                                                            String tmpStr = CoinUsUtils.getDappArgsAsFormattedString(binResponseArgs, true, getTransferActionTypeSeq(eosConnection.getOrigin(), actionAccount, actionName));
                                                            mssArgs.put(keyStr, tmpStr);
                                                            CLog.d(tmpStr);
//                                                            } else {
//                                                                CLog.d("[REQUEST_SIG] jsonResponse is null");
//                                                            }
                                                        }
                                                    }

                                                    if (!allChecklistVerified) {
                                                        CountDownLatch theLatch = mEosDappLatch.get(appKey);
                                                        if (theLatch == null) {
                                                            theLatch = new CountDownLatch(1);
                                                            mEosDappLatch.put(appKey, theLatch);
                                                            String titleText = String.format(coinUsApp.getActivityContext().getString(R.string.dapp_20), eosConnection.getOrigin());
                                                            int originIndex = titleText.indexOf(eosConnection.getOrigin()) - 1;
                                                            Spannable content = new SpannableString(titleText);
                                                            content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(coinUsApp.getActivityContext(), R.color.receive_menu_eos)), originIndex, originIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            content.setSpan(new StyleSpan(Typeface.BOLD), originIndex, originIndex + eosConnection.getOrigin().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            coinUsApp.confirmDappPermission(null, null, content, mssArgs, coinUsApp.getActivityContext().getString(R.string.dapp_21), coinUsApp.getActivityContext().getString(R.string.dapp_5), null, CoinUsConstants.CNUS_COIN_ID_EOS, new DefaultDialog.DialogYesOrNoWithCheckListener() {
                                                                @Override
                                                                public void onYesClicked(DefaultDialog defaultDialog, String value, boolean isChecked, List checkedKeys) {
                                                                    CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                    if (curLatch != null) {
                                                                        mbProceedConfirmed.set(true);
                                                                        if (isChecked) {
                                                                            mCheckedKeys = checkedKeys;
                                                                            mbWhiltelistAddCOnfirmed.set(true);
                                                                        }
                                                                        curLatch.countDown();
                                                                    }
                                                                    defaultDialog.dismissAllowingStateLoss();
                                                                }

                                                                @Override
                                                                public void onNoClicked(DefaultDialog defaultDialog) {
                                                                    CountDownLatch curLatch = mEosDappLatch.get(appKey);
                                                                    if (curLatch != null) {
                                                                        curLatch.countDown();
                                                                    }
                                                                    defaultDialog.dismissAllowingStateLoss();
                                                                }
                                                            });
                                                        } else {
                                                            CLog.w("already processing a request. Stop proceeding");
                                                            return;
                                                        }
                                                        try {
                                                            theLatch.await(60, TimeUnit.SECONDS);
                                                        } catch (InterruptedException e) {
                                                            CLog.e(e.getMessage());
                                                        }
                                                        mEosDappLatch.remove(appKey);
                                                    } else {
                                                        mbProceedConfirmed.set(true);
                                                    }
                                                    if (mbWhiltelistAddCOnfirmed.get()) {
                                                        for (final JsonObject actionArgs : jsonBinArgs) {
                                                            final UserDappWhitelistConvertedDomain prevWhitelist = getTransferActionTypeSeq(eosConnection.getOrigin(), actionArgs.get(DATA_TYPE_CONTRACT).getAsString(), actionArgs.get(DATA_TYPE_NAME).getAsString());
                                                            for (String mutableKey : mCheckedKeys) {
                                                                if (actionArgs.has(mutableKey)) {
                                                                    actionArgs.addProperty(mutableKey, COINUS_MUTABLE);
                                                                }
                                                            }
                                                            if (prevWhitelist != null) {
                                                                CoinUsDataManager.getInstance().updateDappWhitelist(prevWhitelist.getSeq(), eosConnection.getOrigin(), gson.toJson(actionArgs), TRANSFER_ACTION_TYPE, new CoinUsResponseCallback<Long>() {
                                                                    @Override
                                                                    public void onResultFetched(Long response) {
                                                                        if (response.intValue() != -1) {
                                                                            prevWhitelist.setData(actionArgs);
                                                                            mbSorted = false;
                                                                            CLog.d("added to whitelist");
                                                                        } else {
                                                                            CLog.d("WHITELIST ADD FAILED,,,");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onResultFailed(int code, String message) {
                                                                        CLog.d("WHITELIST ADD FAILED  code : " + code + " message : " + message);
                                                                    }
                                                                });
                                                            } else {
                                                                CoinUsDataManager.getInstance().insertDappWhitelist(eosConnection.getOrigin(), gson.toJson(actionArgs), TRANSFER_ACTION_TYPE, new CoinUsResponseCallback<Long>() {
                                                                    @Override
                                                                    public void onResultFetched(Long response) {
                                                                        if (response.intValue() != -1) {
                                                                            UserDappWhitelistConvertedDomain domain = new UserDappWhitelistConvertedDomain(response.intValue(), eosConnection.getOrigin(), actionArgs, TRANSFER_ACTION_TYPE);
                                                                            mUserDappConvertedWhitelist.add(domain);
                                                                            mbSorted = false;
                                                                            CLog.d("added to whitelist");
                                                                        } else {
                                                                            CLog.d("WHITELIST ADD FAILED,,,");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onResultFailed(int code, String message) {
                                                                        CLog.d("WHITELIST ADD FAILED  code : " + code + " message : " + message);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                    if (mbProceedConfirmed.get()) {
                                                        String encryptedSeed;
                                                        if (CoinUsOTPManager.getInstance().getOTPUsageOption() == CoinUsConstants.OTP_USAGE_FORCE) {
                                                            encryptedSeed = CoinUsWallet.getInstance().getEncryptedSeedWithKeeperWithValidation(account);
                                                        } else {
                                                            encryptedSeed = account.getWalletSeed();
                                                        }
                                                        byte[] encrypted = Base64.decode(encryptedSeed, Base64.DEFAULT);
                                                        byte[] decrypted = CoinUsUtils.aesDecryptEcb(CoinUsConstants.KEY, account.getWalletPwd().getBytes(), encrypted);
                                                        try {
                                                            String decryptedSeed = new String(decrypted, "UTF-8");
                                                            DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(Mnemonic.getEntropyByteSeeds(decryptedSeed));
                                                            CoinType eosCoinType = new EosCoins();
                                                            DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
                                                            DeterministicKey rootKey = hierarchy.get(eosCoinType.getBip44Path(CoinUsConstants.CNUS_PRE_FIXED_ACCOUNT_NO), false, true);
                                                            SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, mWalletDomain.getWalletAddressIndex());
                                                            String etherPrivHex = Utils.bytesToHexString(keys.getDeterministicKey().getPrivKeyBytes());
                                                            EosPrivateKey key = EtherToEosKeyConverter.getEosPrivKeyFromEtherPrivKey(etherPrivHex);

                                                            ScatterSigResultAcceptResponse response = new ScatterSigResultAcceptResponse();
                                                            if(sigRequestType == 0) {
                                                                SignedTransaction signedTransaction = new SignedTransaction();
                                                                signedTransaction.setExpiration(transactionData.getExpiration());
                                                                String blockStr = "";
                                                                try {
                                                                    blockStr = getReferenceBlockString(transactionData.getRef_block_num(), transactionData.getRef_block_prefix());
                                                                    signedTransaction.setReferenceBlock(blockStr);
                                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                                    CLog.e("MUSTFIX_ERROR : " + blockStr);
                                                                }
                                                                signedTransaction.setActions(actionsList);

                                                                signedTransaction = EoscDataManager.getSignedTransaction(signedTransaction, apiData.getData().getPayload().getNetwork().getChainId(), key);

                                                                response.setSignatures(signedTransaction.getSignatures());
                                                                response.setReturnedFields(null);
                                                            } else {
                                                                JsonObject jsonTransaction = mJsonData.getAsJsonObject("data").getAsJsonObject("payload").getAsJsonObject("transaction");
                                                                String strSerializedTransaction = jsonTransaction.getAsJsonPrimitive("serializedTransaction").getAsString();
                                                                String chainId = jsonTransaction.getAsJsonPrimitive("chainId").getAsString();
                                                                response.setSignatures(EoscDataManager.getSignatureFromSerializedTransaction(strSerializedTransaction, chainId, key));
                                                                response.setReturnedFields(null);
                                                            }

                                                            int i = 0;
                                                            for (Action theAction : actionsList) {
                                                                if (theAction.getName().equals("transfer") && (theAction.getAccount().equals("eosio") || theAction.getAccount().equals("eosio.token"))) {
                                                                    Bundle params = new Bundle();
                                                                    params.putString("UUID", CoinUsPrefManager.getUniqueUuid(mContext));
                                                                    params.putString("origin", eosConnection.getOrigin());
                                                                    JsonElement quantityElm = jsonBinArgs.get(i).get("quantity");
                                                                    if (quantityElm != null) {
                                                                        params.putString("quantity", quantityElm.getAsString());
                                                                    }
                                                                    mFirebaseAnalytics.logEvent("DApp_Send", params);
                                                                }
                                                                i++;
                                                            }

                                                            apiResponse.setResult(response);
                                                            responseStr = gson.toJson(apiResponse);
                                                            responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                            CLog.w("sending " + responseStr);
                                                            mConn.send(responseStr);
                                                        } catch (UnsupportedEncodingException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        ScatterFailureResultResponse response = new ScatterFailureResultResponse();
                                                        response.setType(SIGNATURE_REJECTED_TYPE);
                                                        response.setMessage(SIGNATURE_REJECTED_MSG);
                                                        response.setCode(402);
                                                        response.setError(true);

                                                        apiResponse.setResult(response);
                                                        responseStr = gson.toJson(apiResponse);
                                                        responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                        CLog.w("sending " + responseStr);
                                                        mConn.send(responseStr);
                                                    }
                                                }
                                            }
                                            break;
                                            case SUBTYPE_FORGET_IDENTITY: {
                                                mEosDappMap.remove(appKey);

                                                ScatterApiResultResponse authResponse = new ScatterApiResultResponse();
                                                authResponse.setId(apiResponse.getId());
                                                authResponse.setResult(true);
                                                responseStr = gson.toJson(authResponse);
                                                responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                CLog.w("sending " + responseStr);
                                                mConn.send(responseStr);
                                            }
                                            break;
                                            case SUBTYPE_AUTHENTICATE: {
                                                CoinUsAccount account = CoinUsWallet.getInstance().getCurrentAccount();
                                                if (account != null) {
                                                    ScatterApiData.RequestIdData.PayloadData payloadData = apiData.getData().getPayload();

                                                    if (payloadData.getNonce().length() != 12) {
                                                        ScatterFailureResultResponse failureResponse = new ScatterFailureResultResponse();
                                                        failureResponse.setType(INVALID_NONCE);
                                                        failureResponse.setMessage(NONCE_MUST_BE_12_CHARACTERS);
                                                        apiResponse.setResult(failureResponse);
                                                        responseStr = gson.toJson(apiResponse);
                                                        responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                        CLog.w("sending " + responseStr);
                                                        mConn.send(responseStr);
                                                        break;
                                                    }

                                                    String encryptedSeed;
                                                    if (CoinUsOTPManager.getInstance().getOTPUsageOption() == CoinUsConstants.OTP_USAGE_FORCE) {
                                                        encryptedSeed = CoinUsWallet.getInstance().getEncryptedSeedWithKeeperWithValidation(account);
                                                    } else {
                                                        encryptedSeed = account.getWalletSeed();
                                                    }
                                                    byte[] encrypted = Base64.decode(encryptedSeed, Base64.DEFAULT);
                                                    byte[] decrypted = CoinUsUtils.aesDecryptEcb(CoinUsConstants.KEY, account.getWalletPwd().getBytes(), encrypted);
                                                    try {
                                                        String decryptedSeed = new String(decrypted, "UTF-8");
                                                        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(Mnemonic.getEntropyByteSeeds(decryptedSeed));
                                                        CoinType eosCoinType = new EosCoins();
                                                        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
                                                        DeterministicKey rootKey = hierarchy.get(eosCoinType.getBip44Path(CoinUsConstants.CNUS_PRE_FIXED_ACCOUNT_NO), false, true);
                                                        SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, mWalletDomain.getWalletAddressIndex());
                                                        String etherPrivHex = Utils.bytesToHexString(keys.getDeterministicKey().getPrivKeyBytes());
                                                        EosPrivateKey key = EtherToEosKeyConverter.getEosPrivKeyFromEtherPrivKey(etherPrivHex);

                                                        String signature = EoscDataManager.authenticateSign(payloadData.getData() != null ? payloadData.getData() : eosConnection.getOrigin(), payloadData.getNonce(), key);

                                                        ScatterApiAuthenticateResponse authResponse = new ScatterApiAuthenticateResponse();
                                                        authResponse.setId(apiResponse.getId());
                                                        authResponse.setResult(signature);
                                                        responseStr = gson.toJson(authResponse);
                                                        responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                        CLog.w("sending " + responseStr);
                                                        mConn.send(responseStr);
                                                    } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            break;
                                            case SUBTYPE_REQUEST_ARBITRARY_SIGNATURE: {
                                                CoinUsAccount account = CoinUsWallet.getInstance().getCurrentAccount();
                                                if (account != null) {
                                                    ScatterApiData.RequestIdData.PayloadData payloadData = apiData.getData().getPayload();

                                                    String encryptedSeed;
                                                    if (CoinUsOTPManager.getInstance().getOTPUsageOption() == CoinUsConstants.OTP_USAGE_FORCE) {
                                                        encryptedSeed = CoinUsWallet.getInstance().getEncryptedSeedWithKeeperWithValidation(account);
                                                    } else {
                                                        encryptedSeed = account.getWalletSeed();
                                                    }
                                                    byte[] encrypted = Base64.decode(encryptedSeed, Base64.DEFAULT);
                                                    byte[] decrypted = CoinUsUtils.aesDecryptEcb(CoinUsConstants.KEY, account.getWalletPwd().getBytes(), encrypted);
                                                    try {
                                                        String decryptedSeed = new String(decrypted, "UTF-8");
                                                        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(Mnemonic.getEntropyByteSeeds(decryptedSeed));
                                                        CoinType eosCoinType = new EosCoins();
                                                        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
                                                        DeterministicKey rootKey = hierarchy.get(eosCoinType.getBip44Path(CoinUsConstants.CNUS_PRE_FIXED_ACCOUNT_NO), false, true);
                                                        SimpleHDKeyChain keys = new SimpleHDKeyChain(rootKey, mWalletDomain.getWalletAddressIndex());
                                                        String etherPrivHex = Utils.bytesToHexString(keys.getDeterministicKey().getPrivKeyBytes());
                                                        EosPrivateKey key = EtherToEosKeyConverter.getEosPrivKeyFromEtherPrivKey(etherPrivHex);

                                                        String signature = EoscDataManager.requestArbitrarySign(payloadData.getData(), key);

                                                        ScatterApiAuthenticateResponse authResponse = new ScatterApiAuthenticateResponse();
                                                        authResponse.setId(apiResponse.getId());
                                                        authResponse.setResult(signature);
                                                        responseStr = gson.toJson(authResponse);
                                                        responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                        CLog.w("sending " + responseStr);
                                                        mConn.send(responseStr);
                                                    } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            break;
                                            case SUBTYPE_REQUEST_ADD_NETWORK: {
                                                ScatterApiResultResponse authResponse = new ScatterApiResultResponse();
                                                authResponse.setId(apiResponse.getId());
                                                authResponse.setResult(true);
                                                responseStr = gson.toJson(authResponse);
                                                responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_API, responseStr));
                                                CLog.w("sending " + responseStr);
                                                mConn.send(responseStr);
                                            }
                                            break;
                                        }
                                    } else {
                                        // api request when pairing is not made. Remove the key and send disconnected
                                        mEosDappMap.remove(appKey);
                                        mConn.send(String.format("%s%s", SCATTER_SIG, SCATTER_LOGOUT));
                                        mConn.send(String.format("%s%s", SCATTER_SIG, SCATTER_DCED));
                                    }
                                } else {
                                    responseStr = String.format("%s%s", SCATTER_SIG, String.format(SCATTER_PAIRED, false));
                                    CLog.w("sending " + responseStr);
                                    mConn.send(responseStr);
                                }
                            }
                            break;
                        case COMM_EVENT:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String getReferenceBlockString(long ref_block_num, long ref_block_prefix) {
            String hexRef = Long.toHexString(ref_block_num);
            String hexPrefix = Long.toHexString(ref_block_prefix);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < 8 - hexRef.length(); i++) {
                builder.append("0");
            }
            builder.append(hexRef);
            builder.append("00000000");
            if(hexPrefix.length() < 8) {
                StringBuilder padding = new StringBuilder();
                for(int i = 0; i < 8 - hexPrefix.length(); i++) {
                    padding.append("0");
                }
                padding.append(hexPrefix);
                hexPrefix = padding.toString();
            }
            int endIndex = hexPrefix.length();
            for(int i = 0; i < hexPrefix.length()/2; i++) {
                builder.append(hexPrefix.substring(endIndex-2, endIndex));
                endIndex -= 2;
            }
            return builder.toString();
        }

        private List<Action> getActions(Gson gson, JsonElement actions) {
            List<Action> actionsList = new ArrayList<>();
            if(actions instanceof JsonObject) {
                ScatterActionsData actionData = gson.fromJson(actions, ScatterActionsData.class);
                Action action = new Action(actionData.getAccount(), actionData.getName());
                action.setAuthorization(getAuthorization(gson, actionData.getAuthorization()));
                action.setData(actionData.getData());
                actionsList.add(action);
            } else {
                ScatterActionsData[] actionsData = gson.fromJson(actions, ScatterActionsData[].class);
                for(int i = 0; i < actionsData.length; i++) {
                    Action action = new Action(actionsData[i].getAccount(), actionsData[i].getName());
                    action.setAuthorization(getAuthorization(gson, actionsData[i].getAuthorization()));
                    action.setData(actionsData[i].getData());
                    actionsList.add(action);
                }
            }
            return actionsList;
        }

        private String[] getAuthorization(Gson gson, JsonElement authorization) {
            String[] authArray;
            if (authorization instanceof JsonObject) {
                ScatterAuthorizationData authData = gson.fromJson(authorization, ScatterAuthorizationData.class);
                authArray = new String[] { authData.getActor() + "@" + authData.getPermission() };
            } else {
                ScatterAuthorizationData[] authData = gson.fromJson(authorization, ScatterAuthorizationData[].class);
                authArray = new String[authData.length];
                for(int i = 0; i < authData.length; i++) {
                    authArray[i] = authData[i].getActor() + "@" + authData[i].getPermission();
                }
            }
            return authArray;
        }
    }

    private void getWalletList() {
        PagingForm pagingForm = new PagingForm();
        pagingForm.setPageSize(255);
        CoinUsDataManager.getInstance().getWalletList(
                String.valueOf(CoinUsConstants.CNUS_COIN_ID_EOS),
                null,
                null,
                null,
                pagingForm,
                new CoinUsResponseCallback<List<WalletDomain>>() {
                    @Override
                    public void onResultFetched(List<WalletDomain> response) {
                        if (response != null) {
                            if(response.size() > 0) {
                                mbNoEosCard = false;
                                mWalletDomain = null;
                                if(mEosDappWalletIndex != -1) {
                                    for (WalletDomain walletDomain : response) {
                                        if (walletDomain.getWalletAddressIndex() == mEosDappWalletIndex) {
                                            mWalletDomain = walletDomain;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                mbNoEosCard = true;
                            }
                        } else {
                            CLog.w("response is null");
                        }
                    }

                    @Override
                    public void onResultFailed(int code, String message) {
                        CLog.w("code : " + code + " message : " + message);
                    }
                });
    }
}
