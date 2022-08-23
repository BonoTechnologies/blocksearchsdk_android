package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterApiResultResponse implements ScatterResultResponse{
    private String id;
    private boolean result;
}
