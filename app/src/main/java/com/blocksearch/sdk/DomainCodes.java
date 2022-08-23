package com.blocksearch.sdk;

public enum DomainCodes {

    // 암호화 화폐
    COIN_MASTER ("1001000", R.string.domain_1001000), // 암호화 화폐
    COIN_BITCOIN ("1001001", R.string.domain_1001001), // Bitcoin
    COIN_LITECOIN ("1001002", R.string.domain_1001002), // Litecoin
    COIN_NAMECOIN ("1001003", R.string.domain_1001003), // Namecoin
    COIN_SWIFTCOIN ("1001004", R.string.domain_1001004), // SwiftCoin
    COIN_BYTECOIN ("1001005", R.string.domain_1001005), // Bytecoin
    COIN_PEERCOIN ("1001006", R.string.domain_1001006), // Peercoin
    COIN_DOGECOIN ("1001007", R.string.domain_1001007), // Dogecoin
    COIN_EMERCOIN ("1001008", R.string.domain_1001008), // Emercoin
    COIN_FEATHERCOIN ("1001009", R.string.domain_1001009), // Feathercoin
    COIN_GRIDCOIN ("1001010", R.string.domain_1001010), // Gridcoin
    COIN_PRIMECOIN ("1001011", R.string.domain_1001011), // Primecoin
    COIN_RIPPLE ("1001012", R.string.domain_1001012), // Ripple
    COIN_NXT ("1001013", R.string.domain_1001013), // Nxt
    COIN_AURORACOIN ("1001014", R.string.domain_1001014), // Auroracoin
    COIN_DASH ("1001015", R.string.domain_1001015), // Dash
    COIN_NEO ("1001016", R.string.domain_1001016), // NEO
    COIN_MAZACOIN ("1001017", R.string.domain_1001017), // MazaCoin
    COIN_MONERO ("1001018", R.string.domain_1001018), // Monero
    COIN_NEM ("1001019", R.string.domain_1001019), // NEM
    COIN_TETHER ("1001020", R.string.domain_1001020), // Tether
    COIN_POTCOIN ("1001021", R.string.domain_1001021), // PotCoin
    COIN_SYNEREO_AMP ("1001022", R.string.domain_1001022), // Synereo AMP
    COIN_TITCOIN ("1001023", R.string.domain_1001023), // Titcoin
    COIN_VERGE ("1001024", R.string.domain_1001024), // Verge
    COIN_STELLAR ("1001025", R.string.domain_1001025), // Stellar
    COIN_VERTCOIN ("1001026", R.string.domain_1001026), // Vertcoin
    COIN_ETHEREUM ("1001027", R.string.domain_1001027), // Ethereum
    COIN_ETHEREUM_CLASSIC ("1001028", R.string.domain_1001028), // Ethereum Classic
    COIN_IOTA ("1001029", R.string.domain_1001029), // IOTA
    COIN_DECRED ("1001030", R.string.domain_1001030), // Decred
    COIN_WAVES_PLATFORM ("1001031", R.string.domain_1001031), // Waves Platform
    COIN_ZCASH ("1001032", R.string.domain_1001032), // Zcash
    COIN_BITCOIN_CASH ("1001033", R.string.domain_1001033), // Bitcoin Cash
    COIN_EOS_IO ("1001034", R.string.domain_1001034), // EOS.IO
    COIN_CARDANO ("1001035", R.string.domain_1001035), // Cardano
    COIN_PETRO ("1001036", R.string.domain_1001036), // Petro
    COIN_QTUM ("1001037", R.string.domain_1001037), // Qtum

    // ICO 분류
    ICO_CATEGORY_MASTER ("1003000", R.string.domain_1003000), // ICO 분류
    ICO_CATEGORY_ART ("1003001", R.string.domain_1003001), // Art
    ICO_CATEGORY_ARTIFICIAL_INTELLI ("1003002", R.string.domain_1003002), // Artificial Intelligence
    ICO_CATEGORY_BANKING ("1003003", R.string.domain_1003003), // Banking
    ICO_CATEGORY_BIG_DATA ("1003004", R.string.domain_1003004), // Big Data
    ICO_CATEGORY_BUSINESS_SERVICES ("1003005", R.string.domain_1003005), // Business services
    ICO_CATEGORY_CASINO_GAMBLING ("1003006", R.string.domain_1003006), // Casino & Gambling
    ICO_CATEGORY_CHARITY ("1003007", R.string.domain_1003007), // Charity
    ICO_CATEGORY_COMMUNICATION ("1003008", R.string.domain_1003008), // Communication
    ICO_CATEGORY_CRYPTOCURRENCY ("1003009", R.string.domain_1003009), // Cryptocurrency
    ICO_CATEGORY_EDUCATION ("1003010", R.string.domain_1003010), // Education
    ICO_CATEGORY_ELECTRONICS ("1003011", R.string.domain_1003011), // Electronics
    ICO_CATEGORY_ENERGY ("1003012", R.string.domain_1003012), // Energy
    ICO_CATEGORY_ENTERTAINMENT ("1003013", R.string.domain_1003013), // Entertainment
    ICO_CATEGORY_HEALTH ("1003014", R.string.domain_1003014), // Health
    ICO_CATEGORY_INFRASTRUCTURE ("1003015", R.string.domain_1003015), // Infrastructure
    ICO_CATEGORY_INTERNET ("1003016", R.string.domain_1003016), // Internet
    ICO_CATEGORY_INVESTMENT ("1003017", R.string.domain_1003017), // Investment
    ICO_CATEGORY_LEGAL ("1003018", R.string.domain_1003018), // Legal
    ICO_CATEGORY_MANUFACTURING ("1003019", R.string.domain_1003019), // Manufacturing
    ICO_CATEGORY_MEDIA ("1003020", R.string.domain_1003020), // Media
    ICO_CATEGORY_OTHER ("1003021", R.string.domain_1003021), // Other
    ICO_CATEGORY_PLATFORM ("1003022", R.string.domain_1003022), // Platform
    ICO_CATEGORY_REAL_ESTATE ("1003023", R.string.domain_1003023), // Real estate
    ICO_CATEGORY_RETAIL ("1003024", R.string.domain_1003024), // Retail
    ICO_CATEGORY_SMART_CONTRACT ("1003025", R.string.domain_1003025), // Smart Contract
    ICO_CATEGORY_SOFTWARE ("1003026", R.string.domain_1003026), // Software
    ICO_CATEGORY_SPORTS ("1003027", R.string.domain_1003027), // Sports
    ICO_CATEGORY_TOURISM ("1003028", R.string.domain_1003028), // Tourism
    ICO_CATEGORY_VIRTUAL_REALITY ("1003029", R.string.domain_1003029), // Virtual Reality

    // ICO 진행 상태
    ICO_ST_MASTER ("1004000", R.string.domain_1004000), // ICO 진행 상태
    ICO_ST_ACTIVE ("1004001", R.string.domain_1004001), // 진행
    ICO_ST_UPCOMING ("1004002", R.string.domain_1004002), // 예정
    ICO_ST_ENDED ("1004003", R.string.domain_1004003), // 종료

    // 알림 채널 타입
    NOTIAPP_TYPE_MASTER ("1005000", R.string.domain_1005000), // 알림 채널 타입
    NOTIAPP_TYPE_SMS ("1005001", R.string.domain_1005001), // 문자 알림
    NOTIAPP_TYPE_WEB ("1005002", R.string.domain_1005002), // 웹 알림
    NOTIAPP_TYPE_EMAIL ("1005003", R.string.domain_1005003), // 이메일 알림
    NOTIAPP_TYPE_APP_PUSH ("1005004", R.string.domain_1005004), // 앱 알림

    // 모바일 운영시스템
    MOBILE_OS_MASTER ("1006000", R.string.domain_1006000), // 모바일 운영시스템
    MOBILE_OS_ANDROID ("1006001", R.string.domain_1006001), // Android
    MOBILE_OS_IOS ("1006002", R.string.domain_1006002), // iOS

    // 알림 서비스 플랫폼
    NOTI_PLATFORM_MASTER ("1007000", R.string.domain_1007000), // 알림 서비스 플랫폼
    NOTI_PLATFORM_AMAZON_SES ("1007001", R.string.domain_1007001), // Amazon SES
    NOTI_PLATFORM_FCM ("1007002", R.string.domain_1007002), // FCM
    NOTI_PLATFORM_APNS ("1007003", R.string.domain_1007003), // APNS

    // 알림 유형
    NOTI_TYPE_MASTER ("1008000", R.string.domain_1008000), // 알림 유형
    NOTI_TYPE_SYSTEM_ALERT ("1008001", R.string.domain_1008001), // 시스템 알림

    // 토큰 유형
    TOKEN_TYPE_MASTER ("1009000", R.string.domain_1009000), // 토큰 유형
    TOKEN_TYPE_ERC20 ("1009001", R.string.domain_1009001), // ERC20
    TOKEN_TYPE_ERC71 ("1009002", R.string.domain_1009002), // ERC71
    TOKEN_TYPE_NEO ("1009003", R.string.domain_1009003), // NEO
    TOKEN_TYPE_WAVES ("1009004", R.string.domain_1009004), // Waves
    TOKEN_TYPE_BITSHARES ("1009005", R.string.domain_1009005), // BitShares
    TOKEN_TYPE_STELLAR ("1009006", R.string.domain_1009006), // Stellar
    TOKEN_TYPE_QRC20 ("1009007", R.string.domain_1009007), // QRC20
    TOKEN_TYPE_OMNI ("1009008", R.string.domain_1009008), // Omni
    TOKEN_TYPE_COUNTERPARTY ("1009009", R.string.domain_1009009), // Counterparty
    TOKEN_TYPE_NEM ("1009010", R.string.domain_1009010), // NEM
    TOKEN_TYPE_UBIQ ("1009011", R.string.domain_1009011), // Ubiq
    TOKEN_TYPE_EOS ("1009012", R.string.domain_1009012), // EOS
    TOKEN_TYPE_ARDOR ("1009013", R.string.domain_1009013), // Ardor
    TOKEN_TYPE_NXT ("1009014", R.string.domain_1009014), // Nxt
    TOKEN_TYPE_ETHEREUM_CLASSIC ("1009015", R.string.domain_1009015), // Ethereum Classic
    TOKEN_TYPE_ACHAIN ("1009016", R.string.domain_1009016), // Achain
    TOKEN_TYPE_NUBITS ("1009017", R.string.domain_1009017), // NuBits
    TOKEN_TYPE_NEBULAS ("1009018", R.string.domain_1009018), // Nebulas
    TOKEN_TYPE_ONTOLOGY ("1009019", R.string.domain_1009019), // Ontology
    TOKEN_TYPE_VECHAIN_TOKEN ("1009020", R.string.domain_1009020), // Vechain [Token]

    // 암호화 화폐 유형
    CRYPTO_TYPE_MASTER ("1010000", R.string.domain_1010000), // 암호화 화폐 유형
    CRYPTO_TYPE_COIN ("1010001", R.string.domain_1010001), // Coin
    CRYPTO_TYPE_TOKEN ("1010002", R.string.domain_1010002), // Token

    // ICO 등록 요구사항
    ICO_REG_REQS_MASTER ("1011000", R.string.domain_1011000), // ICO 등록 요구사항
    ICO_REG_REQS_NONE ("1011001", R.string.domain_1011001), // None
    ICO_REG_REQS_KYC ("1011002", R.string.domain_1011002), // KYC
    ICO_REG_REQS_WHITELIST ("1011003", R.string.domain_1011003), // Whitelist
    ICO_REG_REQS_KYC_WHITELIST ("1011004", R.string.domain_1011004), // KYC & Whitelist

    // 부팅 알림 유형
    BOOT_NOTI_TYPE_MASTER ("1012000", R.string.domain_1012000), // 부팅 알림 유형
    BOOT_NOTI_TYPE_SERVICE_STOP ("1012001", R.string.domain_1012001), // 시스템 정기점검
    BOOT_NOTI_TYPE_APP_UPDATE ("1012002", R.string.domain_1012002), // 앱 업데이트
    BOOT_NOTI_TYPE_GENERAL_NOTIFICATION ("1012003", R.string.domain_1012003), // 일반 공지

    // 알림 전송 상태
    NOTI_SEND_ST_MASTER ("1013000", R.string.domain_1013000), // 알림 전송 상태
    NOTI_SEND_ST_REQUESTED ("1013001", R.string.domain_1013001), // 전송 요청
    NOTI_SEND_ST_ON_HOLD ("1013002", R.string.domain_1013002), // 전송 대기
    NOTI_SEND_ST_SENT ("1013003", R.string.domain_1013003), // 전송 완료
    NOTI_SEND_ST_FAILED ("1013004", R.string.domain_1013004), // 전송 실패
    NOTI_SEND_ST_RECEIVED ("1013005", R.string.domain_1013005), // 전송 완료

    // 이메일 검증 종류
    EMAIL_VERIFY_TYPE_MASTER ("1014000", R.string.domain_1014000), // 이메일 검증 종류
    EMAIL_VERIFY_TYPE_REGISTERED ("1014001", R.string.domain_1014001), // 이메일 등록 검증
    EMAIL_VERIFY_TYPE_CHANGE ("1014002", R.string.domain_1014002), // 이메일 변경 검증

    // 암호화폐 정렬순서
    COIN_SORT_MASTER ("1015000", R.string.domain_1015000), // 암호화폐 정렬순서
    COIN_SORT_ID ("1015001", R.string.domain_1015001), // 암호화폐 번호순
    COIN_SORT_DISPLAY_ORDER ("1015002", R.string.domain_1015002), // 암호화폐 노출순

    // BNUS 트랜잭션 유형
    BNUS_TX_TYPE_MASTER ("1016000", R.string.domain_1016000), // BNUS 트랜잭션 유형
    BNUS_TX_TYPE_BUY ("1016001", R.string.domain_1016001), // 매수
    BNUS_TX_TYPE_SELL ("1016002", R.string.domain_1016002), // 매도

    // Tag Element
    ELEMENT_MASTER ("1017000", R.string.domain_1017000), // Tag Element
    ELEMENT_DAPP ("1017001", R.string.domain_1017001), // DAPP

    // 행성의 구성원 역할
    PLANET_ROLE_MASTER ("1018000", R.string.domain_1018000), // 행성의 구성원 역할
    PLANET_ROLE_HEADER ("1018001", R.string.domain_1018001), // 의장
    PLANET_ROLE_SUBHEADER ("1018002", R.string.domain_1018002), // 부의장
    PLANET_ROLE_MEMBER ("1018003", R.string.domain_1018003), // 구성원

    // 표준상품 유형
    PRODUCT_TYPE_MASTER ("1019000", R.string.domain_1019000), // 표준상품 유형
    PRODUCT_TYPE_AD ("1019001", R.string.domain_1019001), // 광고 상품
    PRODUCT_TYPE_AIRDROP_PACKAGE ("1019002", R.string.domain_1019002), // 에어드랍 상품
    PRODUCT_TYPE_ECOMMERCE ("1019003", R.string.domain_1019003), // 이커머스 상품
    PRODUCT_TYPE_ITEM ("1019004", R.string.domain_1019004), // 아이템 상품

    // 표준상품 속성
    PRODUCT_ATTRIBUTE_MASTER ("1020000", R.string.domain_1020000), // 표준상품 속성
    PRODUCT_ATTRIBUTE_TERM_BASED ("1020001", R.string.domain_1020001), // 기간제
    PRODUCT_ATTRIBUTE_QUANTITY_BASED ("1020002", R.string.domain_1020002), // 종량제
    PRODUCT_ATTRIBUTE_COUNT_BASED ("1020003", R.string.domain_1020003), // 횟수제
    PRODUCT_ATTRIBUTE_TERM_AND_QUANTITY_BASED ("1020004", R.string.domain_1020004), // 기간제+종량제
    PRODUCT_ATTRIBUTE_TERM_AND_COUNT_BASED ("1020005", R.string.domain_1020005), // 기간제+횟수제

    // 결제 상태
    PAYMENT_ST_MASTER ("1021000", R.string.domain_1021000), // 결제 상태
    PAYMENT_ST_PENDING ("1021001", R.string.domain_1021001), // 결제 진행
    PAYMENT_ST_COMPLETED ("1021002", R.string.domain_1021002), // 결제 완료
    PAYMENT_ST_FAILED ("1021003", R.string.domain_1021003), // 결제 취소

    // 매출 유형
    SALES_TYPE_MASTER ("1022000", R.string.domain_1022000), // 매출 유형
    SALES_TYPE_SALES ("1022001", R.string.domain_1022001), // 매출
    SALES_TYPE_REFUND ("1022002", R.string.domain_1022002), // 환불

    // 해시 파워
    MINING_HASH_POWER_MASTER ("1023000", R.string.domain_1023000), // 해시 파워
    MINING_HASH_POWER_BNUS ("1023001", R.string.domain_1023001), // BNUS
    MINING_HASH_POWER_CNUS ("1023002", R.string.domain_1023002), // CNUS
    MINING_HASH_POWER_POPULATION ("1023003", R.string.domain_1023003), // POPULATION

    // 채굴 티켓
    MINING_TICKET_MASTER ("1024000", R.string.domain_1024000), // 채굴 티켓
    MINING_TICKET_CNUS_TICKET ("1024001", R.string.domain_1024001), // CNUS Ticket

    // 채굴 라운드 유형
    ROUND_TYPE_MASTER ("1025000", R.string.domain_1025000), // 채굴 라운드 유형
    ROUND_TYPE_MINING ("1025001", R.string.domain_1025001), // 채굴 라운드
    ROUND_TYPE_SNAPSHOT ("1025002", R.string.domain_1025002), // 스냅샷 라운드

    // 채굴 라운드 상태
    ROUND_ST_MASTER ("1026000", R.string.domain_1026000), // 채굴 라운드 상태
    ROUND_ST_TO_BE_MINED ("1026001", R.string.domain_1026001), // 채굴 예정
    ROUND_ST_MINING_PROGRESS ("1026002", R.string.domain_1026002), // 채굴 진행
    ROUND_ST_MINING_SNAPSHOT ("1026003", R.string.domain_1026003), // 채굴 스냅샷
    ROUND_ST_MINED ("1026004", R.string.domain_1026004), // 채굴 완료
    ROUND_ST_MINING_CANCELED ("1026005", R.string.domain_1026005), // 채굴 취소

    // 채굴 유형
    MINING_TYPE_MASTER ("1027000", R.string.domain_1027000), // 채굴 유형
    MINING_TYPE_BASIC ("1027001", R.string.domain_1027001), // 기본
    MINING_TYPE_MARKETING ("1027002", R.string.domain_1027002), // 마케팅

    // 채굴 시즌 유형
    SEASON_TYPE_MASTER ("1028000", R.string.domain_1028000), // 채굴 시즌 유형
    SEASON_TYPE_PREPARATION ("1028001", R.string.domain_1028001), // 채굴 준비 시즌
    SEASON_TYPE_PROGRESS ("1028002", R.string.domain_1028002), // 채굴 진행 시즌

    // 채굴 티켓 청구 상태
    CLAIM_ST_MASTER ("1029000", R.string.domain_1029000), // 채굴 티켓 청구 상태
    CLAIM_ST_CLAIMED ("1029001", R.string.domain_1029001), // 청구 신청
    CLAIM_ST_CANCELED ("1029002", R.string.domain_1029002), // 신청 취소
    CLAIM_ST_COMPLETED ("1029003", R.string.domain_1029003), // 정산 완료
    CLAIM_ST_FAILED ("1029004", R.string.domain_1029004), // 정산 실패
    ;

    private String dcd;
    private int resourceId;

    DomainCodes(String dcd, int resourceId){
        this.dcd = dcd;
        this.resourceId = resourceId;
    }

    public String getDcd(){
        return dcd;
    }

    public int getResourceId(){
        return resourceId;
    }

    public String getMessage() {
        return SearchApplication.getInstance().getActivityContext().getString(this.resourceId);
    }

}

