package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterFailureResultResponse implements ScatterResultResponse {
    // RESPONSE: requestSignature reject
    private String type;
    private String message;
    private long code;
    private boolean isError;
}
