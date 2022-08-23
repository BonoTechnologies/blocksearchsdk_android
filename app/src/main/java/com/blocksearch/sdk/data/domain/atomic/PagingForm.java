package com.blocksearch.sdk.data.domain.atomic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * SDK on 2018. 4. 10..
 */

@Data
public class PagingForm implements Serializable {

    private int pageNo = 1;
    private int pageSize = 10;
    private long totalCount;
    private boolean hasMore;

    public Map<String,Object> getParams(){
        Map<String,Object> map = new HashMap<>();
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        return map;
    }
}
