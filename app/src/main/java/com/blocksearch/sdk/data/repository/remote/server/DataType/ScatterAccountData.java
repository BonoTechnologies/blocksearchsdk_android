package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterAccountData {
    private String blockchain;
    private String host;
    private String port;
    private String protocol;
    private String chainId;
}
