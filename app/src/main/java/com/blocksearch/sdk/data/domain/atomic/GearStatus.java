package com.blocksearch.sdk.data.domain.atomic;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SDK on 2018. 4. 10..
 */

public class GearStatus extends HashMap<String, Object> {

    public int getCode(){
        return MapUtils.getIntValue(this, "code", 0);
    }
    public String getMessage(){
        return MapUtils.getString(this, "message", "");
    }

    public boolean isIsEditable(){
        return MapUtils.getBoolean(this, "isEditable", false);
    }

    public Map getError(){
        return MapUtils.getMap(this, "error");
    }

}