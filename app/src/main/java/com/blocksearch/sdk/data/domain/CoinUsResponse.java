package com.blocksearch.sdk.data.domain;

import com.blocksearch.sdk.data.domain.atomic.GearData;
import com.blocksearch.sdk.data.domain.atomic.GearStatus;

import lombok.Data;


@Data
public class CoinUsResponse<T> {

    private String apiVersion;

    private GearStatus status;
    private GearData<T> data;
}