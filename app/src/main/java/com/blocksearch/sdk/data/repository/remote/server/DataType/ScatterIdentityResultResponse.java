package com.blocksearch.sdk.data.repository.remote.server.DataType;

import java.util.List;

import lombok.Data;

@Data
public class ScatterIdentityResultResponse implements ScatterResultResponse {
    // RESPONSE: getOrRequestIdentity
    private String hash;
    private String publicKey;
    private String name;
    private boolean kyc;
    private List<ScatterAccountsResponse> accounts;
}
