package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterAccountsResponse {
    private String name;
    private String authority;
    private String publicKey;
    private String blockchain;
    private String chainId;
    private boolean isHardware;
}
