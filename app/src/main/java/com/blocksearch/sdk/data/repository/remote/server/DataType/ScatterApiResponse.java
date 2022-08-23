package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterApiResponse {
    private String id;
    private ScatterResultResponse result;
}
