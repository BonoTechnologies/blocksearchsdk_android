package com.blocksearch.sdk.data.repository.remote.server.DataType;

import lombok.Data;

@Data
public class ScatterPairData {

    @Data
    public class PairData {
        private String appkey;
        private String origin;
        private boolean passthrough;
    }

    private PairData data;
    private String plugin;
}
