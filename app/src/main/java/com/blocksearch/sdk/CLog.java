package com.blocksearch.sdk;


import android.util.Log;

public class CLog {

    static final String TAG = "SearchSDK";

    /**
     * Log Level Error
     * @param message
     */
    public static void e(String message) {
        Log.e(TAG, buildLogMsg(message));
    }

    public static void w(String message) {
        Log.w(TAG, buildLogMsg(message));
    }

    public static void i(String message) {
        Log.i(TAG, buildLogMsg(message));
    }

    public static void d(String message) {
        Log.d(TAG, buildLogMsg(message));
    }

    public static void v(String message) {
        Log.v(TAG, buildLogMsg(message));
    }

    private static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("] ");
        sb.append("(");
        sb.append(Thread.currentThread().getName());
        sb.append(") ");
        sb.append(message);

        return sb.toString();
    }
}
