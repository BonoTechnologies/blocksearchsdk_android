package com.blocksearch.sdk.data.domain.atomic;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SDK on 2018. 4. 10..
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class Status extends GearBase {

    private int code;

    private String message;

    private Error error;

}
