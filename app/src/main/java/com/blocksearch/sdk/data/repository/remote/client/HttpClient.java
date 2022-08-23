package com.blocksearch.sdk.data.repository.remote.client;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blocksearch.sdk.CLog;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

/**
 * SDK on 2017. 12. 8..
 */


public class HttpClient {

    private final String TAG = HttpClient.class.getSimpleName();
    private Context mContext;

    //    private final int WRITE_TIMEOUT = 15; // api ethplorer 30초 이상 걸림.
    //    private final int READ_TIMEOUT = 15; // api ethplorer 30초 이상 걸림.
    //    private final int CONNECT_TIMEOUT = 15; // api ethplorer 30초 이상 걸림.
    private static final int WRITE_TIMEOUT = 60;
    private static final int CONNECT_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60;

    private HttpLoggingInterceptor mHttpLogginInterceptor;
    private CookieManager mCookieManager;

    private OkHttpClient mCoinUsAPIClient;  //  기존 API 사용 클라이언트.
    private OkHttpClient mCoinUsBlockAPIClient; //  인덱스 서버 API 사용 클라이언트.
    private OkHttpClient mExternalAPIClient;

    private static HttpClient sInstance;

    public synchronized static HttpClient getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HttpClient(context);
        }
        return sInstance;
    }

    private HttpClient(Context context) {
        mContext = context;

        mHttpLogginInterceptor = new HttpLoggingInterceptor();
        mHttpLogginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        mCoinUsAPIClient = buildClient( new HeaderInterceptor() );
        mCoinUsBlockAPIClient = buildClient( new BlockAPIHeaderInterceptor() );
        mExternalAPIClient = buildClient(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request());
            }
        });
    }

    private OkHttpClient buildClient( Interceptor headerInterceptor ) {
        return configureClient(new OkHttpClient().newBuilder())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                //                .cookieJar(new JavaNetCookieJar(mCookieManager)) //쿠키메니져 설정
                .addInterceptor( headerInterceptor ) // header 삽입
                .addInterceptor( new LoggingInterceptor() ) //http 로그 확인
                .build();
    }


    @NonNull
    public OkHttpClient getClient() {
        return mCoinUsAPIClient;
    }

    @NonNull
    public OkHttpClient getBlockAPIClient() {
        return mCoinUsBlockAPIClient;
    }

    @NonNull
    public OkHttpClient getExternalAPIClient() { return mExternalAPIClient; }

    /**
     * Http Request, Response Logging Interceptor
     */
    class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);

            long t1 = System.nanoTime();
            CLog.i(String.format("Sending request %s on %s%n%s%n",
                    request.url(), chain.connection(), request.headers()));

            if (request.body() != null) {
                CLog.i("body : " + bodyToString(request));
            }

            long t2 = System.nanoTime();
            if (response != null && response.request() != null) {
                CLog.i(String.format("Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            }
            return response;
        }

        private String bodyToString(final Request request){

            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                return "did not work";
            }
        }
    }


    /**
     * Header Interceptor
     */

    /**
     *
     * "Api-Key": "s15ke8n5ddf6t4sy8h4cpb91t4ibyztc",
     "App-Version": "0.0.1",
     "App-Language": "ko",
     "App-Device-Uuid": "test-device",
     "Auth-Token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXNlZWQiLCJleHAiOjE1NTQ5NzY5NTcsInZlcnNpb24iOjEwMDAxLCJpYXQiOjE1MjM0NDA5NTcsImp0aSI6MTAwMDR9.J6jnxDXTwe97AM45gBcWdEYtdy1K-3hNy3pqsZi7uQw"
     */


    class HeaderInterceptor implements Interceptor {

        private final String COIN_US_API_KEY = "Api-Key";
        private final String COIN_US_APP_VERSION = "App-Version";
        private final String COIN_US_LOCALE_TOKEN = "App-Language"; //en ko 둘중 하나.
//        private final String COIN_US_DEVICE_UUID = "App-Device-Uuid";
        private final String COIN_US_DEVICE_UUID = "App-Device-Udid";
        private final String COIN_US_DEVICE_CURRENCY = "App-Device-Currency";
        private final String COIN_US_AUTH_TOKEN = "Auth-Token";

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();

            requestBuilder.addHeader("user-agent" , CoinUsUtils.getUserAgent(mContext));
            requestBuilder.removeHeader(COIN_US_API_KEY);
            requestBuilder.addHeader(COIN_US_API_KEY, CoinUsConstants.COIN_US_API_KEY);
            // 아래 것으로 분기 타야 한다.
            //requestBuilder.addHeader(COIN_US_API_KEY, CoinUsConstants.COIN_US_BLOCK_API_KEY);

            if (CoinUsPrefManager.getCoinUsAuthToken(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_AUTH_TOKEN);
                requestBuilder.addHeader(COIN_US_AUTH_TOKEN, CoinUsPrefManager.getCoinUsAuthToken(mContext));
            }

            if (CoinUsPrefManager.getNewV2UniqueUdid(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_DEVICE_UUID);
                requestBuilder.addHeader(COIN_US_DEVICE_UUID, CoinUsPrefManager.getNewV2UniqueUdid(mContext));
            }

            if (CoinUsPrefManager.getLocaleToken(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_LOCALE_TOKEN);
                requestBuilder.addHeader(COIN_US_LOCALE_TOKEN, CoinUsPrefManager.getLocaleToken(mContext));
            }

            if (CoinUsPrefManager.getCoinUsAppVersion(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_APP_VERSION);
                requestBuilder.addHeader(COIN_US_APP_VERSION, CoinUsPrefManager.getCoinUsAppVersion(mContext));
            }

            if (CoinUsPrefManager.getCurCurency(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_DEVICE_CURRENCY);
                requestBuilder.addHeader(COIN_US_DEVICE_CURRENCY, CoinUsPrefManager.getCurCurency(mContext));
            }

            requestBuilder.method(request.method(), request.body());

            return chain.proceed(requestBuilder.build());
        }
    }

    class BlockAPIHeaderInterceptor implements Interceptor {

        private final String COIN_US_API_KEY = "Api-Key";
        private final String COIN_US_LOCALE_TOKEN = "App-Language"; //en ko 둘중 하나.

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();

            requestBuilder.removeHeader(COIN_US_API_KEY);
            requestBuilder.addHeader(COIN_US_API_KEY, CoinUsConstants.COIN_US_BLOCK_API_KEY);

            if (CoinUsPrefManager.getLocaleToken(mContext) != null) {
                requestBuilder.removeHeader(COIN_US_LOCALE_TOKEN);
                requestBuilder.addHeader(COIN_US_LOCALE_TOKEN, CoinUsPrefManager.getLocaleToken(mContext));
            }

            requestBuilder.method(request.method(), request.body());

            return chain.proceed(requestBuilder.build());
        }
    }
//TutorsGear/1.2 (Linux; Android 6.0.1; SM-N920K Build/MMB29K; wv)
    /**
     * UnCertificated 허용
     */
    private static OkHttpClient.Builder configureClient(final OkHttpClient.Builder builder) {
        final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) {
            }
        }};

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, certs, new SecureRandom());
        } catch (final java.security.GeneralSecurityException ex) {
            ex.printStackTrace();
        }

        try {
            final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            };

//            builder.sslSocketFactory(ctx.getSocketFactory()).hostnameVerifier(hostnameVerifier);
            builder.sslSocketFactory(ctx.getSocketFactory(),  (X509TrustManager) certs[0]).hostnameVerifier(hostnameVerifier);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return builder;
    }
}
