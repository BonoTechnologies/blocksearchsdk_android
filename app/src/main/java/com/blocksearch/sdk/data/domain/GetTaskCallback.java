package com.blocksearch.sdk.data.domain;

/**
 * SDK on 2018. 4. 10..
 */

interface GetTaskCallback<T> {

    void onTaskLoaded(T task);

    void onDataNotAvailable(String message);
}