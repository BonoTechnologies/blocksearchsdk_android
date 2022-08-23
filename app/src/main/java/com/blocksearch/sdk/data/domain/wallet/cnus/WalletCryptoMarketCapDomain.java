package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * SDK on 2018. 12. 6..
 */

@Data
public class WalletCryptoMarketCapDomain implements Serializable {

    private static final long serialVersionUID = -2421005873881083807L;

    private long cmcId;
    private String cmcSlug;
    private String cmcUrls;
    private String cmcActiveYn;
    private BigDecimal priceBtc;
    private BigDecimal priceUsd;
    private BigDecimal priceKrw;
    private BigDecimal priceCny;
    private BigDecimal priceJpy;
    private String currency;
    private String currencyPrice;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeLong(cmcId);
        out.writeUTF(cmcSlug != null ? cmcSlug : "");
        out.writeUTF(cmcUrls != null ? cmcUrls : "");
        out.writeUTF(cmcActiveYn != null ? cmcActiveYn : "");
        out.writeUTF(priceBtc != null ? priceBtc.toString() : "0");
        out.writeUTF(priceUsd != null ? priceUsd.toString() : "0");
        out.writeUTF(priceKrw != null ? priceKrw.toString() : "0");
        out.writeUTF(priceCny != null ? priceCny.toString() : "0");
        out.writeUTF(priceJpy != null ? priceJpy.toString() : "0");
        out.writeUTF(currency != null ? currency : "");
        out.writeUTF(currencyPrice != null ? currencyPrice : "");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        cmcId = in.readLong();
        cmcSlug = in.readUTF();
        cmcUrls = in.readUTF();
        cmcActiveYn = in.readUTF();
        priceBtc = new BigDecimal(in.readUTF());
        priceUsd = new BigDecimal(in.readUTF());
        priceKrw = new BigDecimal(in.readUTF());
        priceCny = new BigDecimal(in.readUTF());
        priceJpy = new BigDecimal(in.readUTF());
        currency = in.readUTF();
        currencyPrice = in.readUTF();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

    @Override
    public String toString() {
        return  "cmcId : " + cmcId + "\n" +
                "cmcSlug : " + cmcSlug + "\n" +
                "cmcUrls : " + cmcUrls + "\n" +
                "cmcActiveYn : " + cmcActiveYn + "\n" +
                "priceBtc : " + priceBtc + "\n" +
                "priceUsd : " + priceUsd + "\n" +
                "priceKrw : " + priceKrw + "\n" +
                "priceCny : " + priceCny + "\n" +
                "priceJpy : " + priceJpy + "\n" +
                "currency : " + currency + "\n" +
                "currencyPrice : " + currencyPrice + "\n";
    }
}