package com.blocksearch.sdk.data.repository.remote.server.DataType;

import com.google.gson.JsonElement;

import lombok.Data;

@Data
public class ScatterActionsData {
    private String account;
    private String name;
    private JsonElement authorization;
    private String data;
}
