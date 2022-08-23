package com.blocksearch.sdk.data.domain.wallet.cnus;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import lombok.Data;

@Data
public class WalletCryptoDomain implements Serializable {

    private static final long serialVersionUID = 485442141108379132L;

    private long createDt;
    private long updateDt;
    private long cryptoSeq;
    private long wno;
    private String walletAddress;
    private String cryptoTypeDcd;
    private String cryptoType;
    private long cryptoId;
    private String cryptoImgPath;
    private String cryptoSymbol;
    private String cryptoNm;
    private String tokenTypeDcd;
    private String tokenType;
    private int tokenDecimals;
    private String contractAddress;
    private String balance;
    private String displayBalance;
    private WalletCryptoMarketCapDomain marketCap;
    private WalletAssetCryptoDomain asset;
    private int cryptoOrd;
    private String cryptoActiveYn;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeLong(createDt);
        out.writeLong(updateDt);
        out.writeLong(cryptoSeq);
        out.writeLong(wno);
        out.writeUTF(walletAddress != null ? walletAddress : "");
        out.writeUTF(cryptoTypeDcd != null ? cryptoTypeDcd : "");
        out.writeUTF(cryptoType != null ? cryptoType : "");
        out.writeLong(cryptoId);
        out.writeUTF(cryptoImgPath != null ? cryptoImgPath : "");
        out.writeUTF(cryptoSymbol != null ? cryptoSymbol : "");
        out.writeUTF(cryptoNm != null ? cryptoNm : "");
        out.writeUTF(tokenTypeDcd != null ? tokenTypeDcd : "");
        out.writeUTF(tokenType != null ? tokenType : "");
        out.writeInt(tokenDecimals);
        out.writeUTF(contractAddress != null ? contractAddress : "");
        out.writeUTF(balance != null ? balance : "0");
        out.writeUTF(displayBalance != null ? displayBalance : "0");
        out.writeObject(marketCap);
        out.writeObject(asset);
        out.writeInt(cryptoOrd);
        out.writeUTF(cryptoActiveYn != null ? cryptoActiveYn : "");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
       createDt = in.readLong();
       updateDt = in.readLong();
       cryptoSeq = in.readLong();
       wno = in.readLong();
       walletAddress = in.readUTF();
       cryptoTypeDcd = in.readUTF();
       cryptoType = in.readUTF();
       cryptoId = in.readLong();
       cryptoImgPath = in.readUTF();
       cryptoSymbol = in.readUTF();
       cryptoNm = in.readUTF();
       tokenTypeDcd = in.readUTF();
       tokenType = in.readUTF();
       tokenDecimals = in.readInt();
       contractAddress = in.readUTF();
       balance = in.readUTF();
       displayBalance = in.readUTF();
       marketCap = (WalletCryptoMarketCapDomain) in.readObject();
       asset = (WalletAssetCryptoDomain) in.readObject();
       cryptoOrd = in.readInt();
       cryptoActiveYn = in.readUTF();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }


    @Override
    synchronized public String toString() {
        String marketCapString = "";
        String assetString = "";
        if (marketCap != null) {
            marketCapString = marketCap.toString();
        }

        if (asset != null) {
            assetString = asset.toString();
        }

        return  "createDt : " + createDt + "\n" +
                "updateDt : " + updateDt + "\n" +
                "cryptoSeq : " + cryptoSeq + "\n" +
                "wno : " + walletAddress + "\n" +
                "walletAddress : " + walletAddress + "\n" +
                "cryptoTypeDcd : " + cryptoTypeDcd + "\n" +
                "cryptoType : " + cryptoType + "\n" +
                "cryptoId : " + cryptoId + "\n" +
                "cryptoImgPath : " + cryptoImgPath + "\n" +
                "cryptoSymbol : " + cryptoSymbol + "\n" +
                "cryptoNm : " + cryptoNm + "\n" +
                "tokenTypeDcd : " + tokenTypeDcd + "\n" +
                "tokenType : " + tokenType + "\n" +
                "tokenDecimals : " + tokenDecimals + "\n" +
                "contractAddress : " + contractAddress + "\n" +
                "balance : " + balance + "\n" +
                "displayBalance : " + displayBalance + "\n" +
                "marketCap : " + marketCapString + "\n" +
                "asset : " + assetString + "\n" +
                "cryptoOrd : " + cryptoOrd + "\n" +
                "cryptoActiveYn : " + cryptoActiveYn + "\n";
    }
}