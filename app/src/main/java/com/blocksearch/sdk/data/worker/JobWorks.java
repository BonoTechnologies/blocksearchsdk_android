package com.blocksearch.sdk.data.worker;

public interface JobWorks {
    interface JobWork {
        Object nextJob(Object o);
    }

    interface JobWorkError {
        /**
         *
         * @param o
         * @return true - invalidate, false - validate
         */
        boolean errorCheck(Object o);
        void errorHandle();
    }

    interface JobWorkFinal {
        void onComplete(Object result);
    }

    enum JOB_TYPE {
        NETWORK,
        ERROR,
        COMPLETE,
        SHOW_INDICATOR,
        HIDE_INDICATOR;
    }
}
