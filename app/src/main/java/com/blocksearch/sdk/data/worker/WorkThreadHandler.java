package com.blocksearch.sdk.data.worker;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkThreadHandler extends Handler {

    private final Handler mainHandler;

    public WorkThreadHandler(@NonNull Looper looper, Handler mainHandler) {
        super(looper);
        this.mainHandler = mainHandler;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.obj instanceof HashMap) {

            Map<JobWorks.JOB_TYPE, Object> jobs = getJobsInHashMap(msg.obj);

            Message message;
            JobWorks.JobWork netWork = (JobWorks.JobWork) jobs.get(JobWorks.JOB_TYPE.NETWORK);
            JobWorks.JobWorkError errorCheck = (JobWorks.JobWorkError) jobs.get(JobWorks.JOB_TYPE.ERROR);
            JobWorks.JobWorkFinal jobWorkFinal = (JobWorks.JobWorkFinal) jobs.get(JobWorks.JOB_TYPE.COMPLETE);

            if (netWork == null || errorCheck == null || jobWorkFinal == null)
                return;

            // Show Indicator
            if (jobs.containsKey(JobWorks.JOB_TYPE.SHOW_INDICATOR)) {
                message = mainHandler.obtainMessage(ViewHandler.SHOW_LOADING);
                message.obj = jobs.get(JobWorks.JOB_TYPE.SHOW_INDICATOR);
                mainHandler.sendMessageAtFrontOfQueue(message);
            }

            try {
                // Work
                Object o = netWork.nextJob(null);

                // Error Check
                boolean error = errorCheck.errorCheck(o);

                if (!error) {
                    message = mainHandler.obtainMessage(ViewHandler.RESULT_FETCHED);
                    Object[] resultObj = new Object[2];
                    resultObj[0] = jobWorkFinal;
                    resultObj[1] = o;
                    message.obj = resultObj;
                } else {
                    message = mainHandler.obtainMessage(ViewHandler.ERROR);
                    message.obj = errorCheck;
                }

                // Result Fetch
                mainHandler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jobs.containsKey(JobWorks.JOB_TYPE.HIDE_INDICATOR)) {
                // Hide Indicator

                message = mainHandler.obtainMessage(ViewHandler.HIDE_LOADING);
                message.obj = jobs.get(JobWorks.JOB_TYPE.HIDE_INDICATOR);
                mainHandler.sendMessageDelayed(message, 500L);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<JobWorks.JOB_TYPE, Object> getJobsInHashMap(Object obj) {
        if (obj instanceof HashMap) {
            HashMap<?, ?> castedObj = (HashMap<?, ?>) obj;
            Set<?> keySet = castedObj.keySet();

            for (Object o : keySet) {
                if (o instanceof JobWorks.JOB_TYPE) {
                    return (HashMap<JobWorks.JOB_TYPE, Object>) obj;
                }
            }
        }

        return null;
    }
}
