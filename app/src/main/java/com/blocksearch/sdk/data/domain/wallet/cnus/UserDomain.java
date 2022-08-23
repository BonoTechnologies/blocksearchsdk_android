package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.Serializable;

import lombok.Data;

/**
 * SDK on 2018. 12. 11..
 */

@Data
public class UserDomain implements Serializable {

    private long uid;
    private String userSeed;
    private String dbUdid;
    private String userEmail;
    private String userNm;
    private String userNnm;
    private String userAvatar;
    private String userContactNo;
    private String userBirthYmd;
    private String languageCd;
    private String countryCd;
    private String currencyCd;
    private String timezoneId;
    private int utcOffset;
    private int utcDstOffset;
    private String notiYn;
    private String backupYn;
    private long backupDt;
    private String restoreYn;
    private long restoreDt;

    private long createDt;
    private long updateDt;

}