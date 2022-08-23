package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import lombok.Data;


@Data
public class WalletDomain implements Serializable {

    private static final long serialVersionUID = 2936417521293968694L;

    private long wno;
    private long uid;
    private int coinId;
    private String walletAddress;
    private int walletAddressIndex;
    private String walletAddressNm;
    private String coinDcd;
    private String coin;
    private String coinImgPath;
    private String coinSymbol;
    private String coinNm;
    private String displayYn;

    public WalletDomain() {
    }

    public WalletDomain(WalletDomain original) {
        this.wno = original.wno;
        this.uid = original.uid;
        this.coinId = original.coinId;
        this.walletAddress = original.walletAddress;
        this.walletAddressIndex = original.walletAddressIndex;
        this.walletAddressNm = original.walletAddressNm;
        this.coinDcd = original.coinDcd;
        this.coin = original.coin;
        this.coinImgPath = original.coinImgPath;
        this.coinSymbol = original.coinSymbol;
        this.coinNm = original.coinNm;
        this.displayYn = original.displayYn;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeLong(wno);
        out.writeLong(uid);
        out.writeInt(coinId);
        out.writeUTF(walletAddress != null ? walletAddress : "");
        out.writeInt(walletAddressIndex);
        out.writeUTF(walletAddressNm != null ? walletAddressNm : "");
        out.writeUTF(coinDcd != null ? coinDcd : "");
        out.writeUTF(coin != null ? coin : "");
        out.writeUTF(coinImgPath != null ? coinImgPath : "");
        out.writeUTF(coinSymbol != null ? coinSymbol : "");
        out.writeUTF(coinNm != null ? coinNm : "");
        out.writeUTF(displayYn != null ? displayYn : "N");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        wno = in.readLong();
        uid = in.readLong();
        coinId = in.readInt();
        walletAddress = in.readUTF();
        walletAddressIndex = in.readInt();
        walletAddressNm = in.readUTF();
        coinDcd = in.readUTF();
        coin = in.readUTF();
        coinImgPath = in.readUTF();
        coinSymbol = in.readUTF();
        coinNm = in.readUTF();
        displayYn = in.readUTF();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

    @Override
    public String toString() {
        return  "wno : " + wno + "\n" +
                "uid : " + uid + "\n" +
                "coinId : " + coinId + "\n" +
                "walletAddress : " + walletAddress + "\n" +
                "walletAddressIndex : " + walletAddressIndex + "\n" +
                "walletAddressNm : " + walletAddressNm + "\n" +
                "coinDcd : " + coinDcd + "\n" +
                "coin : " + coin + "\n" +
                "coinImgPath : " + coinImgPath + "\n" +
                "coinSymbol : " + coinSymbol + "\n" +
                "coinNm : " + coinNm + "\n" +
                "displayYn : " + displayYn + "\n";
    }
}