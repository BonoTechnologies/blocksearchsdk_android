package com.blocksearch.sdk.data.repository.remote.server.DataType;

import java.util.List;

import lombok.Data;

@Data
public class ScatterSigResultAcceptResponse implements ScatterResultResponse {
    // RESPONSE: requestSignature accept
    private List<String> signatures;
    private String returnedFields;
}
