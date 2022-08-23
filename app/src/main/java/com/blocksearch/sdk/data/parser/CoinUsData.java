package com.blocksearch.sdk.data.parser;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SDK on 2018. 4. 2..
 */

public class CoinUsData extends HashMap<String, Object> {

    public <T> List<T> getItems(Class<T> toValueType, String key) {
        List<T> result = new ArrayList<>();
        List items = (List)this.get(key);
        if (items!=null){
            Gson gson = new Gson();
            for (int i = 0; i < items.size(); i++) {
                result.add(gson.fromJson(gson.toJsonTree(items.get(i)), toValueType));
            }
            return result;
        } else { // 2017.01.12 /Giwung /Added Else Snippet code for a single data without "items"
            Gson gson = new Gson();
            result.add(gson.fromJson(gson.toJsonTree(this), toValueType));
        }
        return result;
    }

}
