package com.blocksearch.sdk.data.repository.remote.server.DataType;

import com.google.gson.JsonElement;

import java.util.List;

import lombok.Data;

@Data
public class ScatterApiData {
    private RequestIdData data;
    private String plugin;

    @Data
    public class RequestIdData {
        private String type;
        private PayloadData payload;
        private String id;
        private String appkey;
        private String nonce;
        private String nextNonce;

        @Data
        public class PayloadData {
            // Request signature
            private FieldData fields;
            private TransactionData transaction;
            private BufData buf;
            private String blockchain;
            private NetworkData network;

            // Authenticate
            private String nonce;
            private String data;
            private String publicKey;

            // requiredFields
            private String origin;

            @Data
            public class FieldData {
                JsonElement accounts;
            }

            @Data
            public class TransactionData {
                private String expiration;
                private long ref_block_num;
                private long ref_block_prefix;
                private long net_usage_words;
                private long max_cpu_usage_ms;
                private long delay_sec;
                // context_free_actions
                private JsonElement actions;
                // transaction_extensions
            }

            @Data
            public class BufData {
                private String type;
                private List<Integer> data;
            }

            @Data
            public class NetworkData {
                private String name;
                private String protocol;
                private String host;
                private String port;
                private String blockchain;
                private String chainId;
                private String token;
            }
        }
    }
}
