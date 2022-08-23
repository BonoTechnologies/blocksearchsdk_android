package com.blocksearch.sdk.data.repository.remote.retrofit.service;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * SDK on 2018. 4. 12..
 */

public interface CoinUsService {

    /**
     * Boot API, 1. CoinUs App Store Version 정보, 2. push token 수집, 3. login 사용자 auth token update, 4. coin, token 최신 버젼
     *
     * @param params { deviceToken: String }
     */
    @GET("/v2/booting/coins-us-app")
    Call<JsonObject> getBootingCoinUsApp(
        @QueryMap Map<String, Object> params
    );

    /**
    * @param params { deviceToken: String }
    */

    @POST("/v2/booting/notifications/device")
    Call<JsonObject> insertBootingMobileDevicePushToken(
        @Body Map<String, Object> params
    );

    /**
     * bitcoin address balance 등 정보 조회
     *
     * @param address
     */
    @GET("/v2/bitcoin/address/{address}")
    Call<JsonObject> getBtcAddress(
            @Path("address") String address
    );

    /**
     * send btc address raw transactions,BTC 전송
     *
     * @param address
     * @param params { hex: String, from: String, amount: long }
     */
    @POST("/v2/bitcoin/address/{address}/send-raw-transactions")
    Call<JsonObject> sendBtcAddressRawTransactions(
            @Path("address") String address,
            @Body Map<String, Object> params
    );

    /**
     * 특정 주소의 utxo 목록
     *
     * @param address
     */
    @GET("/v2/bitcoin/address/{address}/utxo")
    Call<JsonObject> getBtcAddressTxUnspentList(
            @Path("address") String address
    );

    /**
     * estimateFee : Bitcoin-cli estimatesmartfee 2,lowFee : estimateFee * 0.6,middleFee : estimateFee * 0.9,highFee : estimateFee * 1.1
     *
     */
    @GET("/v2/bitcoin/estimate-fee")
    Call<JsonObject> getBtcEstimateSmartFee(
    );

    /**
     * estimateFee : bitcoin-cli estimaterawfee 2
     *
     */
    @GET("/v2/bitcoin/estimate-raw-fee")
    Call<JsonObject> getBtcEstimateRawFee(
    );

    /**
     * eos account balance 등 정보 조회
     *
     * @param account
     */
    @GET("/v2/eos/accounts/{account}/balance")
    Call<JsonObject> getEosAccountBalance(
            @Path("account") String account
    );

    /**
     * eos account token map
     *
     * @param account
     * @param code
     */
    @GET("/v2/eos/accounts/{account}/tokens/{code}")
    Call<JsonObject> getEosToken(
            @Path("account") String account,
            @Path("code") String code
    );

    /**
     * 이더리움 계정의 특정 토큰 정보
     *
     * @param address
     * @param contractAddress
     * @param params { currency: String }
     */
    @GET("/v2/ethereum/address/{address}/tokens/{contractAddress}")
    Call<JsonObject> getEthTokenByAddressContract(
            @Path("address") String address,
            @Path("contractAddress") String contractAddress,
            @QueryMap Map<String, Object> params
    );

    /**
     * 펜딩 tx 조회
     *
     * @param address
     * @param params { inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/bitcoin/address/{address}/pending-txs")
    Call<JsonObject> getBtcAddressPendingTxList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 펜딩 tx 조회
     *
     * @param address
     * @param params { inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/ethereum/address/{address}/pending-txs")
    Call<JsonObject> getEthPendingTxByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 펜딩 tx 조회
     *
     * @param address
     * @param params { inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/raven/address/{address}/pending-txs")
    Call<JsonObject> getRvnAddressPendingTxList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 펜딩 tx 조회
     *
     * @param address
     * @param params { inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/filecoin/address/{address}/pending-messages")
    Call<JsonObject> getFilPendingMessageByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인어스 암호화폐 목록,,[2018/12월 개편],    1) param -> coinSortDcd : 추가,    2) items -> userWalletCount : 추가, marketCap : 시세정보 변경,
     *
     * @param params { coinDcd: String, coinSymbol: String, coinNm: String, coinSortDcd: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/coins")
    Call<JsonObject> getCoinList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인어스 암호화폐 토큰 검색 목록
     *
     * @param params { coinId: int, coinDcd: String, contractAddress: String, tokenTypeDcd: String, tokenSymbol: String, tokenNm: String, tokenDecimals: int, useYn: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/tokens")
    Call<JsonObject> getTokenList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인어스 사용자 생성,,- UserInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	MISSING_ROOT_SEED_VALUE	지갑의 루트시드 값 누락
     500	ERROR	System Error
     ,
     *
     * @param params { userSeed: String, backupYn: String, restoreYn: String }
     */
    @POST("/v2/users")
    Call<JsonObject> insertUser(
            @Body Map<String, Object> params
    );

    /**
     * get my account
     *
     */
    @GET("/v2/my-account")
    Call<JsonObject> getMyAccount(
    );

    /**
     *
     */
    @POST("/v2/my-account/backup-log")
    Call<JsonObject> insertUserBackupLog(
    );

    /**
    */
    @POST("/v2/my-account/restore-log")
    Call<JsonObject> insertUserRestoreLog(
    );

    /**
     * 지갑 신규 생성,,- WalletInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     300	ERROR_DB_TRANS	error-db-trans
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	MISSING_COIN_ID	코인 아이디 누락
     303	UNAVAILABLE_COIN	사용할 수 없는 코인
     304	MISSING_WALLET_ADDRESS	지갑 주소 누락
     305	MISSING_WALLET_ADDRESS_INDEX	지갑 색인 번호 누락
     500	ERROR	System Error
     ,
     *
     * @param params { coinId: int, walletAddress: String, walletAddressIndex: int, walletAddressNm: String }
     */
    @POST("/v2/wallets")
    Call<JsonObject> insertWallet(
            @Body Map<String, Object> params
    );

    /**
     * 지갑정보 단 건 조회
     *
     * @param wno
     */
    @GET("/v2/wallets/{wno}")
    Call<JsonObject> getWallet(
            @Path("wno") long wno
    );

    /**
     * 지갑 이름 수정,,- WalletNmUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_WALLET	존재하지 않는 지갑
     302	UNAUTHORIZED_USER	권한 없음
     303	MISSING_WALLET_NM	지갑명 누락
     500	ERROR	System Error
     ,
     *
     * @param wno
     * @param params { walletAddressNm: String }
     */
    @PUT("/v2/wallets/{wno}/address-name")
    Call<JsonObject> updateWalletNm(
            @Path("wno") long wno,
            @Body Map<String, Object> params
    );

    /**
     * 지갑 삭제, - 실제로 데이터를 삭제하는 것이 아니라 노출 여부 값을 'N'으로 갱신, - 지갑 복구 시 정책에 대해서는 협의 필요,,- WalletDeleteResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_WALLET	존재하지 않는 지갑
     302	UNAUTHORIZED_USER	권한 없음
     500	ERROR	System Error
     ,
     *
     * @param wno
     */
    @DELETE("/v2/wallets/{wno}")
    Call<JsonObject> deleteWallet(
            @Path("wno") long wno
    );

    /**
     * 이더리움 계정 정보 단 건 조회 (ETH 계정 정보 및 Balance)
     *
     * @param address
     * @param params { currency: String }
     */
    @GET("/v2/ethereum/address/{address}")
    Call<JsonObject> getEthAddress(
            @Path("address") String address,
            @QueryMap Map<String, Object> params
    );

    @GET("/v2/ethereum/estimate-fee")
    Call<JsonObject> getEthEstimateFee(
    );

    /**
     * Send eth address raw transactions,ETH 전송
     *
     * @param address
     * @param params { hex: String, txHash: String, txNonce: long, from: String, to: String, contractAddress: String, txValue: long, txGasPrice: long, txGas: long }
     */
    @POST("/v2/ethereum/address/{address}/send-raw-transactions")
    Call<JsonObject> sendEthAddressRawTransactions(
            @Path("address") String address,
            @Body Map<String, Object> params
    );



    /**
     * 지갑의 암호화폐 목록 상장 토큰 자산의 정보,,
     *
     * @param wno
     */
    @GET("/v2/wallets/{wno}/crypto-currency-price")
    Call<JsonObject> getWalletCryptoCurrencyPrice(
            @Path("wno") long wno
    );

    /**
     * 지갑정보 목록 조회
     *
     * @param params { coinId: int, coinDcd: String, displayYn: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/wallets")
    Call<JsonObject> getWalletList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 모든 지갑정보 목록 조회
     *
     * @param params { coinId: int, coinDcd: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/all-wallets")
    Call<JsonObject> getAllWalletList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 지갑의 암호화폐 목록, 활성화된 코인과 토큰 목록,,[2018/12월 개편],
     * 1) option parameters 변경,
     * 2) items -> balance 추가,
     * 3) items -> marketCap : 시세정보 변경,
     * 4) token auth sync 된 데이타 전달 (서버백단에서 처리),,
     *
     * @param wno
     * @param params { cryptoActiveYn: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/wallets/{wno}/crypto")
    Call<JsonObject> getWalletCryptoList(
            @Path("wno") long wno,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 지갑의 전체 토큰 목록 및 토큰 검색,,[2018/12월 개편],    1) option parameters 변경,    2) items -> balance 추가,    3) items -> marketCap : 시세정보 변경,    4) token auth sync 된 데이타 전달 (서버백단에서 처리),    * items Domain 변경 WalletCryptoTokenDomain => WalletTokenDomain,    * items.cryptoActiveYn == null 인 경우 + 노출,,
     *
     * @param wno
     * @param params { keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/wallets/{wno}/tokens")
    Call<JsonObject> getWalletTokenList(
            @Path("wno") long wno,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 트랜잭션 정보 단 건 조회
     *
     * @param txHash
     */
    @GET("/v2/ethereum/txs/{txHash}")
    Call<JsonObject> getEthTx(
            @Path("txHash") String txHash
    );

    /**
     * 이더리움 계정의 트랜잭션 상세
     *
     * @param address
     * @param txHash
     */
    @GET("/v2/ethereum/address/{address}/txs/{txHash}")
    Call<JsonObject> getEthTxByAddress(
            @Path("address") String address,
            @Path("txHash") String txHash
    );

    /**
     * bitcoin address tx 상세
     *
     * @param address
     * @param txHash
     */
    @GET("/v2/bitcoin/address/{address}/txs/{txHash}")
    Call<JsonObject> getBtcTxByAddressList(
            @Path("address") String address,
            @Path("txHash") String txHash
    );

    /**
     * eos tx view
     *
     * @param account
     * @param trxId
     */
    @GET("/v2/eos/accounts/{account}/txs/{trxId}")
    Call<JsonObject> getEosTx(
            @Path("account") String account,
            @Path("trxId") String trxId
    );

    /**
     * 특정 지갑의 토큰 노출 설정 목록
     *
     * @param wno
     */
    @GET("/v2/wallets/{wno}/crypto-tokens")
    Call<JsonObject> getWalletCryptoTokenList(
            @Path("wno") long wno
    );

    /**
     * 코인어스 암호화폐 토큰 신규 저장 - 사용자의 토큰 저장,,- TokenAutoDiscoveryInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     300	ERROR_DB_TRANS	error-db-trans
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	UNAUTHORIZED_USER	권한 없음
     303	NOT_EXISTS_COIN_ID	존재하지 않는 암호화폐 아이디
     304	MISSING_CONTRACT_ADDRESS	스마트 컨트랙트 주소 누락
     305	ALREADY_REGISTRED_TOKEN	이미 등록된 암호화폐 토큰
     306	MISSING_TOKEN_SYMBOL	암호화폐 토큰 심볼 정보 누락
     307	MISSING_TOKEN_NAME	암호화폐 토큰 이름 정보 누락
     308	MISSING_TOKEN_DECIMALS	암호화폐 토큰 표시 자리수 누락
     500	ERROR	System Error
     ,
     *
     * @param wno
     * @param coinId
     * @param params { contractAddress: String, tokenTypeDcd: String, tokenSymbol: String, tokenNm: String, tokenDecimals: int }
     */
    @POST("/v2/wallets/{wno}/coin/{coinId}/auto-discovery-tokens")
    Call<JsonObject> insertTokenAutoDiscovery(
            @Path("wno") long wno,
            @Path("coinId") int coinId,
            @Body Map<String, Object> params
    );


    /**
     * 지갑 암호화폐 목록 노출 여부 갱신,,[2018/12월 개편],    1) resultCodesType 변경,    2) cryptoActiveYn 입력 설정 파라미터 변경,,- WalletTokenActiveYnUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	UNAUTHORIZED_USER	권한 없음
     302	NOT_EXISTS_TOKEN_ID	존재하지 않는 암호화폐 토큰 아이디
     303	WRONG_OR_MISSING_ACTIVE_SETTING_VALUE	올바르지 않은 활성화 설정 값이거나 설정 값이 누락되었음
     500	ERROR	System Error
     ,
     *
     * @param wno
     * @param tokenId
     * @param params { cryptoActiveYn: String }
     */
    @PUT("/v2/wallets/{wno}/tokens/{tokenId}/active")
    Call<JsonObject> updateWalletTokenActiveYn(
            @Path("wno") long wno,
            @Path("tokenId") long tokenId,
            @Body Map<String, Object> params
    );

    /**
     * 이더리움 계정의 트랜잭션 목록
     *
     * @param address
     * @param params { inOutDcd: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/ethereum/address/{address}/txs")
    Call<JsonObject> getEthTxByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 이더리움 계정의 특정 토큰 전송 목록
     *
     * @param address
     * @param contractAddress
     * @param params }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/ethereum/address/{address}/tokens/{contractAddress}/transfers")
    Call<JsonObject> getEthTokenTransferByAddressList(
            @Path("address") String address,
            @Path("contractAddress") String contractAddress,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * Token Info 조회,
     *
     * @param contractAddress
     */
    @GET("/v2/ethereum/tokens/{contractAddress}")
    Call<JsonObject> getEthToken(
            @Path("contractAddress") String contractAddress
    );

    /**
     * eos account tx 목록 조회
     *
     * @param account
     * @param params { symbol: String, code: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/eos/accounts/{account}/txs")
    Call<JsonObject> getEosAccountTxList(
            @Path("account") String account,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * bitcoin address tx 목록 조회
     *
     * @param address
     * @param params }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/bitcoin/address/{address}/txs")
    Call<JsonObject> getBtcTxByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인어스 사용자의 언어설정 갱신,,- UserUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     500	ERROR	System Error
     ,
     *
     * @param params { languageCd: String }
     */
    @PUT("/v2/my-account/language")
    Call<JsonObject> updateUserLanguageCd(
            @Body Map<String, Object> params
    );


    /**
     * 코인어스 사용자의 통화설정 갱신,currencyCd는 API 참조 getCurrencyList,,- UserUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     500	ERROR	System Error
     ,
     *
     * @param params { uid: long, currencyCd: String }
     */
    @PUT("/v2/my-account/currency")
    Call<JsonObject> updateUserCurrencyCd(
            @Body Map<String, Object> params
    );

    /**
     * 공통코드 목록 조회
     *
     * @param params { dcd: String }
     */
    @GET("/v2/commons/codes")
    Call<JsonObject> getCommonCodeList(
            @QueryMap Map<String, Object> params
    );

    /**
     * 공통 언어 목록 조회
     *
     * @param params { languageCd: String }
     */
    @GET("/v2/commons/languages")
    Call<JsonObject> getLanguageList(
            @QueryMap Map<String, Object> params
    );

    /**
     * 공통 타임존 목록 조회
     *
     * @param params { timezoneId: String, countryCd: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/commons/timezones")
    Call<JsonObject> getTimezoneList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 공통코드 통화 목록
     *
     * @param params { currencyNm: String, currencySymbol: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/commons/currency")
    Call<JsonObject> getCurrencyList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인어스 사용자의 알림설정 갱신,,- UserUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     500	ERROR	System Error
     ,
     *
     * @param params { notiYn: String }
     */
    @PUT("/v2/my-account/notification-setting")
    Call<JsonObject> updateUserNotiYn(
        @Body Map<String, Object> params
    );

    /**
     * ger user auth token,,- GearCommonResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	FAIL	Fail
     302	INVALID_PARAMETER	필수 입력값 누락
     500	ERROR	System Error
     ,
     *
     * @param params { userSeed: String }
     */
    @POST("/v2/users/auth-token")
    Call<JsonObject> getUserAuthToken(
            @Body Map<String, Object> params
    );

    /**
     * 주소록 목록 검색
     *
     * @param params { coinId: long, coinDcd: String, coinSymbol: String, publicAddress: String, publicAddressUid: long, addressBookNm: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/address-books")
    Call<JsonObject> getUserAddressBookList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 주소록 단 건 정보 조회
     *
     * @param addressBookSeq
     */
    @GET("/v2/address-books/{addressBookSeq}")
    Call<JsonObject> getUserAddressBook(
            @Path("addressBookSeq") long addressBookSeq
    );

    /**
     * 지갑 사용자의 주소록 추가,,- UserAddressBookInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	MISSING_COIN_ID	코인 아이디 누락
     303	UNAVAILABLE_COIN	사용할 수 없는 코인
     304	MISSING_ADDRESS	상대방 주소 정보 누락
     305	MISSING_ADDRESS_BOOK_NM	주소록 이름 누락
     306	ALREADY_REGISTERED_ADDRESS_BOOK	이미 등록된 주소록
     500	ERROR	System Error
     ,
     *
     * @param params { coinId: long, publicAddress: String, addressBookNm: String, addressBookMemo: String }
     */
    @POST("/v2/address-books")
    Call<JsonObject> insertUserAddressBook(
            @Body Map<String, Object> params
    );

    @POST("/v2/wallet-invalid-logs")
    Call<JsonObject> insertErrWalletInfo(
            @Body Map<String, Object> params
    );


    /**
     * 주소록 정보 갱신,,- UserAddressBookUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_ADDRESS_BOOK	존재하지 않는 주소록
     302	UNAUTHORIZED_USER	권한 없음
     500	ERROR	System Error
     ,
     *
     * @param addressBookSeq
     * @param params { addressBookNm: String, addressBookMemo: String }
     */
    @PUT("/v2/address-books/{addressBookSeq}")
    Call<JsonObject> updateUserAddressBook(
            @Path("addressBookSeq") long addressBookSeq,
            @Body Map<String, Object> params
    );

    /**
     * 주소록 삭제,,- UserAddressBookDeleteResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_ADDRESS_BOOK	존재하지 않는 주소록
     302	UNAUTHORIZED_USER	권한 없음
     500	ERROR	System Error
     ,
     *
     * @param addressBookSeq
     */
    @DELETE("/v2/address-books/{addressBookSeq}")
    Call<JsonObject> deleteUserAddressBook(
            @Path("addressBookSeq") long addressBookSeq
    );

    /**
     * 코인 및 토큰 환율 조회
     *
     * @param symbol
     */
    @GET("/v2/crypto-currency/exchanges/{symbol}")
    Call<JsonObject> getCryptoCurrencyExchange(
            @Path("symbol") String symbol
    );


    /**
     * 환불 요청
     */
    @GET("/v2/ico/{icoId}/refund/{referralCode}")
    Call<JsonObject> reqPrivateRefund(
            @Path("icoId") long icoId,
            @Path("referralCode") String referralCode
    );
    @GET("/v2/ico/{icoId}/refund")
    Call<JsonObject> reqPublicRefund(
            @Path("icoId") long icoId
    );


    /**
     * ICO 참여 지갑 목록
     *
     */
    @GET("/v2/ico/{icoId}/walletlist/{referralCode}")
    Call<JsonObject> getPrivateIcoPurchaseWalletList(
            @Path("icoId") long icoId,
            @Path("referralCode") String referralCode
    );
    @GET("/v2/ico/{icoId}/walletlist")
    Call<JsonObject> getPublicIcoPurchaseWalletList(
            @Path("icoId") long icoId
    );


    /**
     * 사용자의 ICO 구매 내역 요청
     */
    @GET("/v2/ico/purchaselist")
    Call<JsonObject> getIcoPurchaseList(
    );

    /**
     * KYC 정보 저장.
     *
     * @param { kycUUID: String, kycRawData: String }
     */
    @POST("/v2/ico/{uid}/savekycinfo")
            Call<JsonObject> saveKycInfo(
            @Path("uid") long uid,
            @Body Map<String, Object> params
    );

    /**
     * KYC 인증 상태 리스트 요청
     *
     * ex>
     * icoIds "123,456,789"
     */
    @GET("/v2/ico/kycState/{icoIds}")
    Call<JsonObject> getKycRequestExList(
            @Path("icoIds") String icoIds
    );

    /**
        KYC 인증 요청
    * */
    @GET("/v2/ico/kycRequest/{icoId}/{kycLevel}/{kycUUID}/{externalKey}")
    Call<JsonObject> getKycRequestEx(
            @Path("icoId") long icoId,
            @Path("kycLevel") long kycLevel,
            @Path("kycUUID") String kycUUID,
            @Path("externalKey") long externalKey
    );

    /**
     * KYC 인증 결과 조회
     *      staging-apis.coinus.io/v2/ico/kycResult/'xxx','yyy'​
     * */
    @GET("/v2/ico/kycResult/{kycUUIDs}")
    Call<JsonObject> getKycResult(
            @Path("kycUUIDs") String kycUUIDs
    );

    /***
     * ICO의 KYC 인증 상태 정보 조회
     */
    @GET("/v2/ico/{icoId}/kyc/states")
    Call<JsonObject> getIcoKycUserState(
            @Path("icoId") String icoId
    );

    @POST("/v2/ico/{icoId}/kyc/states")
    Call<JsonObject> insertIcoKycUserState(
            @Path("icoId") String icoId,
            @Body Map<String, Object> params
    );

    /**  base URL < "https://apis.coinus.io/" >
    * ICO Rererral 코드로 조회
     *
     * @param referralCode
    * */
    @GET("/v2/ico/referer/{referralCode}")
    Call<JsonObject> getIcoReferral(
            @Path("referralCode") String referralCode
    );

    /**
     * ICO 검색 목록
     *
     * @param params { icoStDcd: String, icoStartUtc: long, icoEndUtc: long, icoCategoryDcd: String, icoNm: String, tokenSymbol: String, countryCd: String, displayYn: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/ico")
    Call<JsonObject> getIcoList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );


    /**
     * ICO 정보 단 건 조회
     *
     * @param icoId
     */
    @GET("/v2/ico/{icoId}")
    Call<JsonObject> getIco(
            @Path("icoId") long icoId
    );

    /**
     * ICO 판매를 위한 토큰 정보 단 건 조회
     *
     * @param icoId
     */
    @GET("/v2/ico/{icoId}/sales")
    Call<JsonObject> getIcoSale(
            @Path("icoId") long icoId
    );

    /**
     * 사용자의 특정 ICO 투자 정보 요약정보 조회
     *
     * @param icoId
     * @param params { wno: long }
     */
    @GET("/v2/ico/{icoId}/investments")
    Call<JsonObject> getIcoInvestment(
            @Path("icoId") long icoId,
            @QueryMap Map<String, Object> params
    );

    /**
     * ICO 투자 총 모금액
     *
     * @param icoId
     */
    @GET("/v2/ico/{icoId}/investments/total-amounts")
    Call<JsonObject> getIcoInvestmentTotalAmount(
            @Path("icoId") long icoId
    );

    /**
     * 특정 ICO의 KYC 계정 목록
     *
     * @param icoId
     * @param params { wno: long }
     */
    @GET("/v2/ico/{icoId}/kyc")
    Call<JsonObject> getIcoKycList(
            @Path("icoId") long icoId,
            @QueryMap Map<String, Object> params
    );

    /**
     * 사용자의 확정 KYC 목록
     *
     * @param params { keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/kyc")
    Call<JsonObject> getUserKycList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * KYC 정보 단 건 조회 (확정된 KYC 정보)
     *
     * @param kycNo
     */
    @GET("/v2/kyc/{kycNo}")
    Call<JsonObject> getKyc(
            @Path("kycNo") long kycNo
    );

    /**
     * KYC 신청 및 확정 전체 검색 목록
     *
     * @param params { kycNo: long, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/kyc/request")
    Call<JsonObject> getKycRequestList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * KYC 신청 정보 단 건 조회
     *
     * @param kycRequestNo
     */
    @GET("/v2/kyc/request/{kycRequestNo}")
    Call<JsonObject> getKycRequest(
            @Path("kycRequestNo") long kycRequestNo
    );

    /**
     * KYC 신청,,- KycRequestInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	EMAIL_VERIFICATION_IS_NOT_COMPLETE	이메일 검증 미완료
     303	MISSING_USER_NAME	사용자 이름 누락
     304	MISSING_COUNTRY_CODE	국가코드 누락
     500	ERROR	System Error
     ,
     *
     * @param params { userNm: String, userEmail: String, userContactNo: String, userBirthYmd: String, idPicturePath1: String, idPicturePath2: String, idPicturePath3: String, countryCd: String, userZipcode: String, userAddress1: String, userAddress2: String, userAddress3: String }
     */
    @POST("/v2/kyc/request")
    Call<JsonObject> insertKycRequest(
            @Body Map<String, Object> params
    );

    /**
     * KYC 신청 정보 수정,,- KycRequestUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_KYC_REQUEST_NO	존재하지 않는 KYC 신청 번호
     302	UNAUTHORIZED_USER	권한 없음
     303	MISSING_USER_NAME	사용자 이름 누락
     304	MISSING_COUNTRY_CODE	국가코드 누락
     500	ERROR	System Error
     ,
     *
     * @param kycRequestNo
     * @param params { userNm: String, userEmail: String, userContactNo: String, userBirthYmd: String, idPicturePath1: String, idPicturePath2: String, idPicturePath3: String, countryCd: String, userZipcode: String, userAddress1: String, userAddress2: String, userAddress3: String }
     */
    @PUT("/v2/kyc/request/{kycRequestNo}")
    Call<JsonObject> updateKycRequest(
            @Path("kycRequestNo") long kycRequestNo,
            @Body Map<String, Object> params
    );

    /**
     * KYC 신청정보 삭제,,- KycRequestDeleteResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_KYC_REQUEST_NO	존재하지 않는 KYC 신청 번호
     302	UNAUTHORIZED_USER	권한 없음
     303	CAN_NOT_DELETE_CONFIRMED_KYC	확정된 KYC는 삭제 불가
     500	ERROR	System Error
     ,
     *
     * @param kycRequestNo
     */
    @DELETE("/v2/kyc/request/{kycRequestNo}")
    Call<JsonObject> deleteKycRequest(
            @Path("kycRequestNo") long kycRequestNo
    );

    /**
     * CNUS 사전예약 토큰 구매 시 사용한 KYC의 검증 여부, - 검증이 완료 된 경우에만 토큰 전송이 가능, - 전송하고자 하는 토큰이 CNUS 토큰인 경우에만 확인,
     *
     * @param wno
     */
    @GET("/v2/pre-order-kyc/wallets/{wno}")
    Call<JsonObject> getPreorderKyc(
            @Path("wno") long wno
    );

    /**
     * 이메일 정보 갱신,,- UserEmailUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	성공
     301	MISSING_USER_ID_OR_USER_EMAIL	사용자 번호 또는 사용자 이메일 누락
     302	NOT_CREATED_EMAIL_VERIFY_CD	이메일 인증코드가 생성되지 않음
     401	MISSING_MEMBER_EMAIL	입력하신 이메일을 다시 확인해주세요.
     500	ERROR	System Error
     ,
     *
     * @param params { userEmail: String }
     */
    @PUT("/v2/my-account/email")
    Call<JsonObject> updateUserEmail(
            @Body Map<String, Object> params
    );

    /**
     * 이메일 정보 갱신 인증코드 검증,,- UserEmailConfirmUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	성공
     301	DO_NOT_MATCH_VERIFY_CD	이메일 인증코드 불일치
     302	ALREADY_VERIFIED_EMAIL	이미 인증한 이메일
     303	EMAIL_VERIFY_TIEMOUT	이메일 인증코드 확인 시간 초과
     500	ERROR	System Error
     ,
     *
     * @param params { userEmail: String, emailVerifyCd: String }
     */
    @PUT("/v2/my-account/email-confirm")
    Call<JsonObject> updateUserEmailConfirm(
            @Body Map<String, Object> params
    );


    @Multipart
    @POST("/v2/kyc/id-picture")
    Call<JsonObject> updateMyProfilePicture(
            @Part MultipartBody.Part params
    );

    /**
     * get announcement notice list
     *
     * @param params { postYn: String, keyword: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/announcement-notices")
    Call<JsonObject> getAnnouncementNoticeList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * get announcement notice
     *
     * @param anncSeq
     */
    @GET("/v2/announcement-notices/{anncSeq}")
    Call<JsonObject> getAnnouncementNotice(
            @Path("anncSeq") long anncSeq
    );

    /**
     * ICO 투자,,- IcoInvestmentInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	ICO_THAT_CAN_NOT_BE_INVESTED	투자할 수 없는 ICO
     302	UNAUTHORIZED_USER	권한 없음
     303	WRONG_OR_MISSING_KYC	잘못된 KYC 계정 또는 KYC 계정 정보 누락
     304	PREVIOUSLY_INVESTED_IN_ICO_KYC_ACCOUNT	이전에 투자한 ICO의 KYC 계정 필요
     305	EXCEEDING_MAXIMUM_INVESTMENT	최대 투자액 초과
     500	ERROR	System Error
     ,
     *
     * @param icoId
     * @param params { wno: long, kycNo: long, investmentCurrency: String, investmentAmt: String }
     */
    @POST("/v2/ico/{icoId}/investments")
    Call<JsonObject> insertIcoInvestment(
            @Path("icoId") long icoId,
            @Body Map<String, Object> params
    );


    /**
     * 사용자 문의 등록,,- CounselingBoardInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     500	ERROR	System Error
     ,
     *
     * @param params { userNm: String, userEmail: String, userContactNo: String, counselingTitle: String, counselingContent: String, filePath1: String, filePath2: String, filePath3: String }
     */
    @POST("/v2/counseling-boards")
    Call<JsonObject> insertCounselingBoard(
            @Body Map<String, Object> params
    );


    /**
        @param params { pageSize: int}
    * */
    @GET("/v2/EOSBP")
    Call<JsonObject> getBPList(
        @QueryMap Map<String, Object> params
    );

    /**
     * 이오스 계정명이 생성 가능한지 조회
     * @param account
     */
    @GET("/v2/eos/accounts/{account}")
    Call<JsonObject> checkEosAccountNameDuplicate(
            @Path("account") String account
    );

    /**
     * 이오스 계정 구매 정보 조회
     */
    @GET("/v2/eos/accounts")
    Call<JsonObject> getEosAccountCreateInfo(
    );

    /**
     * 이오스 계정 구매 가격 조회
     */
    @GET("/v2/eos/account/prices")
    Call<JsonObject> getEosAccountPriceInfo(
    );

    /**
     * 이오스 계정 구매 정보 추가
     * @param params { wno: long, eosAccountPriceSeq: long, accountName: String, publicKey: String, purchaseTx: String }
     */
    @POST("/v2/eos/account/purchase")
    Call<JsonObject> insertEosAccountCreateInfo(
            @Body Map<String, Object> params
    );

    /**
     * 사용자의 총 자산 정보,,
     *
     * @param params { refreshYn: String }
     */
    @GET("/v2/wallets/total-assets")
    Call<JsonObject> getUserAssetCacheData(
            @QueryMap Map<String, Object> params
    );

    /**
     * 사용자가 최근 방문(사용)한 지갑 번호 단 건 조회,,
     *
     */
    @GET("/v2/wallets/recently-visit")
    Call<JsonObject> getUserRecentlyVisitWallet(
    );


    /**
     * 사용자 본인의 CoinVerse 전용 지갑 정보 단 건 조회
     *
     */
    @GET("/v2/bnus/coinverse-wallets/selected")
    Call<JsonObject> getUserCoinverseWallet(
            @QueryMap Map<String, Object> params
    );

    /**
     * Dashboard용 암호화폐별 Coin Marketcap 시세
     *
     * @param params }
     */
    @GET("/v2/crypto-market-caps/top")
    Call<JsonObject> getCryptoMarketCapForDashboardList(
            @QueryMap Map<String, Object> params
    );

    /**
     * CoinVerse 전용 지갑 정보 등록 후보 목록 조회
     *
     */
    @GET("/v2/bnus/coinverse-wallets/candidates")
    Call<JsonObject> getUserCoinverseCandidatesWallet(
    );

    /**
     * CoinVerse 전용 지갑 설정,,- UserCoinverseWalletInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	ONLY_YOUR_OWN_ETHER_WALLET_CAN_BE_REGISTERED	본인 소유의 이더리움 지갑만 등록 가능
     303	COINVERSE_WALLET_IS_ALREADY_REGISTERED	이미 코인버스 전용 지갑이 생성되었음
     500	ERROR	System Error
     ,
     *
     * @param params { wno: long }
     */
    @POST("/v2/bnus/coinverse-wallets")
    Call<JsonObject> insertUserCoinverseWallet(
            @Body Map<String, Object> params
    );

    /**
     * BNUS 거래 트랜잭션 생성 (주문 요청),,- BnusTxInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	MISSING_BNUS_TRANSACTION_HASH_VALUE	트랜잭션 해시 값 누락
     302	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     303	NOT_COINVERSE_WALLET	CoinVerse 전용 지갑이 아님
     304	MISSING_BNUS_TRANSACTION_TYPE_CODE	BNUS 트랜잭션 유형 코드 값 누락
     305	MISSING_TX_TIMESTAMP	트랜잭션 타임스탬프 값 누락
     500	ERROR	System Error
     ,
     *
     * @param params { txHash: String, bnusTxTypeDcd: String, wno: long, txFrom: String, txTo: String, txInValue: String, txEstimateValue: String }
     */
    @POST("/v2/bnus/txs")
    Call<JsonObject> insertBnusTx(
            @Body Map<String, Object> params
    );

    /**
     * 사용자의 최근 BNUS 거래 내역 TOP 1
     *
     */
    @GET("/v2/bnus/txs/recently")
    Call<JsonObject> getUserRecentlyBnusTx(
    );

    /**
     * BNUS 거래 내역
     *
     * @param params { bnusTxTypeDcd: String, txHash: String, blockHash: String, blockNumber: long }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/bnus/txs")
    Call<JsonObject> getBnusTxList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * BNUS 거래 영수정 조회
     *
     * @param txHash
     */
    @GET("/v2/bnus/txs/{txHash}")
    Call<JsonObject> getBnusTxReceipt(
            @Path("txHash") String txHash
    );

    /**
     * main dashboard 용 BNUS 토큰 정보,,  5-1) BNUS 교환 비율 : BNUS/CNUS,    1 BNUS = ${bnusPrice} CNUS,    ${exchangeTimestamp} <- 로그일자,,  5-2) 전일 대비 증감량 및 증감률,    ${priceFluctuation} (${priceFluctuationRates %}) -> + 4.30 (5.12 %),,  5-3) BNUS 원화/달러화/위안화,    ${currencyBnusPrice} ${currency} -> 5,530 KRW,,  5-4) 토큰 그래프 -토큰 상세 정보는 노출되지 않고 시세 추이선만 제공 -최근 시세 Top100 데이터,    ${data}.forEach() 처리,,  5-5) CNUS Balance, BNUS Total Supply,    ${displayCnusReservBalance}, ${displayBnusVolume},
     *
     */
    @GET("/v2/bnus/market-price-charts/dashboard")
    Call<JsonObject> getBnusTxDashboardChart(
    );

    /**
     * 앱 HOME 메뉴에 노출되는 이벤트 목록,,   items.bannerTargetUrl 에 {uid}, {authToken} 의 Template 이 존재할 경우 해당 값으로 치환하여 웹뷰를 호출해주세요.,
     *
     */
    @GET("/v2/dashboard/events")
    Call<JsonObject> getEventDashboardList(
    );

    /**
     * get announcement notice top
     *
     */
    @GET("/v2/announcement-notice/top")
    Call<JsonObject> getAnnouncementNoticeTop(
    );

    /**
     * BNUS Market 설정 관리,,activeYn : N인 경우 마켓 비활성화 처리,notiTitle : 비활성화(activeYn = N) 인 경유 노출 메시지,
     *
     */
    @GET("/v2/bnus/market-configs")
    Call<JsonObject> getBnusMarketConfig(
    );

    /**
     * Dapp 화이트리스트 생성
     */
    @POST("/v2/dapp/whitelist")
    Call<JsonObject> insertDappWhitelist(
            @Body Map<String, Object> params
    );

    /**
     * Dapp 화이트리스트 업데이트
     */
    @PUT("/v2/dapp/whitelist")
    Call<JsonObject> updateDappWhitelist(
            @Body Map<String, Object> params
    );

    /**
     * Dapp 화이트리스트 목록 조회 (페이징)
     */
    @GET("/v2/dapp/whitelist")
    Call<JsonObject> getDappWhiltelist(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * Dapp 화이트리스트 삭제
     */
    @HTTP(method = "DELETE", path = "/v2/dapp/whitelist", hasBody = true)
    Call<JsonObject> deleteDappWhitelist(
            @Body List<Integer> params
    );

    /**
     * Dapp 대표지갑 설정 (신규 추가 또는 기존 대표지갑 변경)
     */
    @POST("/v2/dapp/wallet")
    Call<JsonObject> insertOrUpdateDappWallet(
            @Body Map<String, Object> params
    );

    /**
     * Dapp 대표지갑 조회(단건)
     */
    @GET("/v2/dapp/wallet/{coinId}")
    Call<JsonObject> getDappWallet(
            @Path("coinId") String coinId
    );

    /**
     * Dapp 대표지갑 조회
     */
    @GET("/v2/dapp/wallets")
    Call<JsonObject> getDappWallets(
    );

    @Headers({
            "Content-Type:application/json"
    })
    @POST("/v2/eos/dapps/txs")
    Call<JsonObject> getDeserializedTxWithActions(
            @Body JsonObject payload
    );

    /**
     * 사용자의 코인버스 플래닛 행성 정보 단 건 조회,
     *
     */
    @GET("/v2/coin-verse/planets/selected")
    Call<JsonObject> getUserPlanet(
    );

    /**
     * 코인버스 플래닛 생성,,- PlanetInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	ONLY_ONE_PLANET_CAN_BELONG	하나의 행성에만 소속 가능
     303	NOT_EXISTS_COINVERSE_WALLET	코인버스 전용카드를 반드시 등록해야만 Planet을 시작할 수 있음
     500	ERROR	System Error
     ,
     *
     * @param params { planetNm: String }
     */
    @POST("/v2/coin-verse/planets")
    Call<JsonObject> insertPlanet(
            @Body Map<String, Object> params
    );

    /**
     * [Planet-신규] 사용자의 NICK 정보 갱신,,- UserUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     500	ERROR	System Error
     ,
     *
     * @param params { userNnm: String }
     */
    @PUT("/v2/my-account/nickname")
    Call<JsonObject> updateUserNnm(
            @Body Map<String, Object> params
    );

    /**
     * [Planet-신규] 사용자의 AVATAR 이미지 갱신,,- UserUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     500	ERROR	System Error
     ,
     *
     * @param params { avatarId: String }
     */
    @PUT("/v2/my-account/avatar")
    Call<JsonObject> updateUserAvatar(
            @Body Map<String, Object> params
    );

    /**
     * insert planet member mine,,- PlanetMemberMineInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_PLANET_USER_ID	존재하지 않는 행성의 구성원
     302	NOT_EXISTS_MINE_ID	존재하지 않는 채굴 광산 아이디
     303	ALREADY_BEING_MINDED	해당 사용자가 이미 채굴중인 광산
     500	ERROR	System Error
     ,
     *
     * @param planetId
     * @param params { mineId: long }
     */
    @POST("/v2/coin-verse/planets/{planetId}/member-mines")
    Call<JsonObject> insertPlanetMemberMine(
            @Path("planetId") long planetId,
            @Body Map<String, Object> params
    );

    /**
     * 현재 채굴 라운드 정보 단 건 조회,,,
     ,1. 현재 시간이 주말일 경우에는 ROUND_SEQ=0으로 전달 -> 현재 진행중인 ROUND가 없음으로 표기,2. 현재 라운드 정보에 실시간 HashPower 및 다음 라운드 일련번호 추가 제공 (items.nextRound),3, 마이닝 중인 경우 라운드 채굴 관련 정보를 제공한다 (items.memberMining),
     ,,- parameter ,    seasonNo : 누락 시 현재 진행 중인 시즌으로 설정됨,
     *
     * @param mineId
     * @param params { seasonNo: int }
     */
    @GET("/v2/coin-verse/mines/{mineId}/mining-round/current")
    Call<JsonObject> getMiningCurrentRound(
            @Path("mineId") long mineId,
            @QueryMap Map<String, Object> params
    );

    /**
     * 사용자의 채굴 티켓 총 수량
     *
     * @param params { miningTicketDcd: String }
     */
    @GET("/v2/coin-verse/user-tickets/mining-volumes")
    Call<JsonObject> getUserTicketMiningVolume(
            @QueryMap Map<String, Object> params
    );

    /**
     * 본인의 RANKING을 기준으로 근접 RANKER 4명
     *
     * @param mineId
     * @param roundSeq
     */
    @GET("/v2/coin-verse/mines/{mineId}/mining-round/{roundSeq}/ranker-near")
    Call<JsonObject> getMiningRoundNearRankerRealtimeList(
            @Path("mineId") long mineId,
            @Path("roundSeq") long roundSeq
    );

    /**
     * 사용자의 암호화폐 교환 티켓 청구 수량
     *
     * @param params { miningTicketDcd: String }
     */
    @GET("/v2/coin-verse/user-tickets/claim-volumes")
    Call<JsonObject> getUserTicketClaimVolume(
            @QueryMap Map<String, Object> params
    );

    /**
     * 오늘 채굴한 암호화폐 교환권(TICKET) 누적 수량,,- parameter ,    seasonNo : 누락 시 현재 진행 중인 시즌으로 설정됨,    mining ymd : yyyymmdd, 누락 시 오늘일자 설정됨,   ,
     *
     * @param mineId
     * @param params { seasonNo: int, miningYmd: String }
     */
    @GET("/v2/coin-verse/mines/{mineId}/mining/daily-volume")
    Call<JsonObject> getPlanetMemberDailyMiningVolume(
            @Path("mineId") long mineId,
            @QueryMap Map<String, Object> params
    );

    /**
     * 일자별 채굴 내역,,- parameter ,    seasonNo : 누락 시 현재 진행 중인 시즌으로 설정됨,    mining ymd : yyyymmdd, 누락 시 오늘일자 설정됨,    pageSize : 23 ROUND 이므로 반드시 pageSize 를 50으로 설정,
     *
     * @param mineId
     * @param params { seasonNo: int, miningYmd: String }
     * @param paging { pageNo: int, pageSize: int
     */
    @GET("/v2/coin-verse/mines/{mineId}/mining/daily-mining")
    Call<JsonObject> getPlanetMemberDailyMiningList(
            @Path("mineId") long mineId,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 공통 Avatar Image 목록
     *
     */
    @GET("/v2/commons/avatar")
    Call<JsonObject> getAvatarList(
    );

    /**
     * 코인버스 플래닛 스토어의 아이템 목록
     *
     * @param params { productNm: String, currencyCd: String, freeYn: String, saleYn: String, displayYn: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/billing/sales-product-items")
    Call<JsonObject> getSalesProductItemList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인버스 스토어 아이템의 사용자 결제 목록
     *
     * @param params }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/payments")
    Call<JsonObject> getPaymentByUserItemList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 주문 신청,,- OrderInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	NOT_EXISTS_USER_ID	존재하지 않는 사용자 아이디
     302	NOT_REGISTERED_COINVERSE_WALLET	CoinVerse 전용 지갑이 등록되어 있지 않음
     303	NOT_EXIST_SALES_PRODUCT_NUMBER	존재하지 않는 판매상품
     304	SOLD_OUT	완판
     305	EXCEED_QUANTITIES_AVAILABLE_FOR_PURCHASE_THIS_MONTH	이번달 구매할 수 있는 수량 초과
     306	MISSING_PAYMENT_CONTRACT_ADDRESS	결제용 스마트컨트랙트 주소 누락
     500	ERROR	System Error
     ,
     *
     * @param params { spno: long, paymentAmount: String }
     */
    @POST("/v2/billing/orders")
    Call<JsonObject> insertOrder(
            @Body Map<String, Object> params
    );

    /**
     * 결제 신청 -> 블록 생성 전,,* block tx 생성 후 알림 push 협의 필요,,- PaymentInsertResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     301	MISSING_OR_INVALID_ORDER_NUMBER	누락되었거나 유효하지 않은 주문 번호
     302	MISSING_TRANSACTION_HASH_VALUE	트랜잭션 해시 값 누락
     303	INVALID_TRANSACTION_VALUE	실결제액 불일치
     304	ORDER_NUMBER_ALREADY_IN_PROGRESS	이미 결제가 진행 중인 주문 번호
     500	ERROR	System Error
     ,
     *
     * @param params { orderNo: long, txHash: String, txValue: String }
     */
    @POST("/v2/billing/payments")
    Call<JsonObject> insertPayment(
            @Body Map<String, Object> params
    );

    /**
     * 나의 아이템 목록
     *
     * @param params { planetId: long, mineId: long }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/coin-verse/planet-items")
    Call<JsonObject> getPlanetMemberItemList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 코인버스 플래닛 부스터 아이템 활성화 여부 갱신,,- PlanetMemberItemActiveUpdateResultCodes
     Number	Code	Mesage
     200	SUCCESS	Success
     300	ERROR_DB_TRANS	error-db-trans
     301	NOT_AUTHORIZED	권한 없음
     302	AUTHORIZATION_EXPIRED	권한 만료
     303	MISSING_OR_INVALID_ITEM_ACTIVATION_VALUE	누락되었거나 유효화지 않은 아이템 활성화 여부 값
     304	ITEM_IS_ALREADY_ACTIVE	아이템이 이미 활성화 상태임
     305	ITEM_IS_ALREADY_INACTIVE	아이템이 이미 비활성화 상태임
     500	ERROR	System Error
     ,
     *
     * @param itemSeq
     * @param params { activeYn: String }
     */
    @PUT("/v2/coin-verse/planet-items/{itemSeq}/active")
    Call<JsonObject> updatePlanetMemberItemActive(
            @Path("itemSeq") long itemSeq,
            @Body Map<String, Object> params
    );
    /**
     * get cnus tx dashboard chart
     *
     */
    @GET("/v2/cnus/market-price-charts/dashboard")
    Call<JsonObject> getCnusTxDashboardChart(
    );

    /**
     * get crypto market cap dashboard list
     *
     */
    @GET("/v2/crypto-market-cap/dashboard")
    Call<JsonObject> getCryptoMarketCapDashboardList(
    );

    /************************************ RVN API START ************************************/

    /**
     * 특정 주소의 트랜잭션 ID 목록
     *
     * @param address
     * @param params { inOut: String }
     */
    @GET("/v2/raven/address/{address}/tx-ids")
    Call<JsonObject> getRvnAddressTxIdList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params
    );

    /**
     * RVN 주소 정보 단 건 조회
     *
     * @param address
     */
    @GET("/v2/raven/address/{address}")
    Call<JsonObject> getRvnAddressInfo(
            @Path("address") String address
    );

    /**
     * 특정 주소의 트랜잭션 목록
     *
     * @param address
     */
    @GET("/v2/raven/address/{address}/txs")
    Call<JsonObject> getRvnAddressTxList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 특정 주소 기준의 영수증
     *
     * @param address
     * @param txId
     */
    @GET("/v2/raven/address/{address}/txs/{txId}")
    Call<JsonObject> getRvnAddressTx(
            @Path("address") String address,
            @Path("txId") String txId
    );

    /**
     * RVN 주소 검증
     *
     * @param address
     */
    @GET("/v2/raven/address/{address}/validate")
    Call<JsonObject> getRvnAddressValidate(
            @Path("address") String address
    );

    /**
     * send rvn address raw transactions,RVN 전송
     *
     * @param address
     * @param params { hex: String, from: String, amount: long }
     */
    @POST("/v2/raven/address/{address}/send-raw-transactions")
    Call<JsonObject> sendRvnAddressRawTransactions(
            @Path("address") String address,
            @Body Map<String, Object> params
    );

    /**
     * send rvn address raw transactions,RVN Asset 전송,* sendRvnAddressRawTransactions 과 URL은 동일하며 파라미터만 다름
     *
     * @param address
     * @param params { hex: String, assetNm: String }
     */
    @POST("/v2/raven/address/{address}/send-raw-transactions")
    Call<JsonObject> sendRvnAddressAssetRawTransactions(
            @Path("address") String address,
            @Body Map<String, Object> params
    );

    /**
     * 특정 주소의 utxo 목록
     *
     * @param address
     */
    @GET("/v2/raven/address/{address}/utxo")
    Call<JsonObject> getRvnAddressTxUnspentList(
            @Path("address") String address
    );

    /**
     * 보유자의 자산 상세 -> 한 번이라도 보유한 모든 목록을 제공,(자산 잔액도 같이 노출),,
     *
     * @param address
     * @param params { assetNm: String }
     */
    @GET("/v2/raven/address/{address}/assets")
    Call<JsonObject> getRvnAddressAsset(
            @Path("address") String address,
            @QueryMap Map<String, Object> params
    );

    /**
     * 특정 주소의 트랜잭션 목록
     *
     * @param address
     * @param params { assetNm: String, inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/raven/address/{address}/assets/txs")
    Call<JsonObject> getRvnAddressAssetTxList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * 특정 주소가 발생시킨 자산의 트랜잭션 영수증
     *
     * @param address
     * @param txId
     * @param params { assetNm: String }
     */
    @GET("/v2/raven/address/{address}/assets/txs/{txId}")
    Call<JsonObject> getRvnAddressAssetTx(
            @Path("address") String address,
            @Path("txId") String txId,
            @QueryMap Map<String, Object> params
    );

    /**
     * RVN Asset 목록 조회,,
     *
     * @param params { assetNm: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/raven/assets")
    Call<JsonObject> getRvnAssetList(
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * RVN TX FEE 조회,,
     *
     */
    @GET("/v2/raven/estimate-fee")
    Call<JsonObject> getRvnEstimateFee(
    );


    /************************************ RVN API END ************************************/

    /************************************ FILECOIN API START ************************************/
    /**
     * filecoin address balance 등 정보 조회
     *
     * @param address
     */
    @GET("/v2/filecoin/address/{address}")
    Call<JsonObject> getFileCoinAddressInfo(
            @Path("address") String address
    );

    /**
     * filecoin address nonce
     *
     * @param address
     */
    @GET("/v2/filecoin/address/{address}/nonce")
    Call<JsonObject> getFileCoinAddressNonce(
            @Path("address") String address
    );

    /**
     * filecoin address check validate,* 아직 미구현 rpc method 확인 필요
     *
     * @param address
     */
    @GET("/v2/filecoin/address/{address}/validate")
    Call<JsonObject> getFileCoinAddressValidate(
            @Path("address") String address
    );

    /**
     * send filecoin address raw transactions,Fil 전송
     *
     * @param address
     * @param params { version: long, method: long, params: String, from: String, to: String, value: long, nonce: long, gasFeeCap: long, gasLimit: long, gasPremium: long, signatureType: String, signatureData: String }
     */
    @POST("/v2/filecoin/address/{address}/send-raw-transactions")
    Call<JsonObject> sendFileCoinAddressRawTransactions(
            @Path("address") String address,
            @Body Map<String, Object> params
    );

    /**
     * FileCoin Estimate Message Gas Fee,
     *
     * @param params { address: String, method: long, params: String, from: String, to: String, value: long, nonce: long, gasFeeCap: long, gasLimit: long, gasPremium: long, maxFee: long }
     */
    @POST("/v2/filecoin/estimate-message-gas")
    Call<JsonObject> getFileCoinEstimateMessageGas(
            @Body Map<String, Object> params
    );

    /**
     * Filecoin Estimate Smart Gas Fee,
     *
     */
    @GET("/v2/filecoin/estimate-fee")
    Call<JsonObject> getFilecoinEstimateSmartFee(
    );


    /**
     * Filecoin Estimate Smart Gas Fee,
     *
     */
    @POST("/v2/filecoin/estimate-fee-cap")
    Call<JsonObject> getFileCoinEstimateGasFeeCap(
            @Body Map<String, Object> params
    );

    @POST("/v2/filecoin/estimate-fee-limit")
    Call<JsonObject> getFileCoinEstimateGasLimit(
            @Body Map<String, Object> params
    );

    /**
     * filecoin address message 전송 목록
     *
     * @param address
     * @param params { inOut: String }
     * @param paging { pageNo: int, pageSize: int}
     */
    @GET("/v2/filecoin/address/{address}/messages")
    Call<JsonObject> getFilMessageByAddressList(
            @Path("address") String address,
            @QueryMap Map<String, Object> params,
            @QueryMap Map<String, Object> paging
    );

    /**
     * filecoin address message 전송 상세
     *
     * @param address
     * @param messageId
     */
    @GET("/v2/filecoin/address/{address}/messages/{messageId}")
    Call<JsonObject> getFilMessageByAddress(
            @Path("address") String address,
            @Path("messageId") String messageId
    );

    /************************************ FILECOIN API END ************************************/

    /**
     * 유저가 보유한 erc-20의 시세정보를 보유량 내림차순으로 10건 조회
     *
     */
    @GET("/v2/crypto-market-caps/user-erc20")
    Call<JsonObject> getCryptoMarketCapOnlyUserErc20(
    );


}
