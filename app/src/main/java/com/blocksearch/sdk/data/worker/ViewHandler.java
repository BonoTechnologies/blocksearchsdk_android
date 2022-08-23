package com.blocksearch.sdk.data.worker;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class ViewHandler extends Handler {

    public static final int SHOW_LOADING = 1001;
    public static  final int HIDE_LOADING = 1002;
    public static final int RESULT_FETCHED = 2001;
    public static final int ERROR = 3001;

    public ViewHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case RESULT_FETCHED : {
                Object[] objects = (Object[]) msg.obj;
                ((JobWorks.JobWorkFinal) objects[0]).onComplete(objects[1]);
            }
            break;
            case ERROR : {
                ((JobWorks.JobWorkError) msg.obj).errorHandle();
            }
            break;
            case SHOW_LOADING :
            case HIDE_LOADING : {
                ((JobWorks.JobWork) msg.obj).nextJob(null);
            }
            break;
        }
    }
}