package com.blocksearch.sdk.data.domain.wallet.cnus;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import lombok.Data;

@Data
public class WalletAssetCryptoDomain implements Serializable {

    private static final long serialVersionUID = 447162160480775226L;

    private String asset;
    private String assetNm;
    private String assetOwner;
    private String ownerYn;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeUTF(asset != null ? asset : "");
        out.writeUTF(assetNm != null ? assetNm : "");
        out.writeUTF(assetOwner != null ? assetOwner : "");
        out.writeUTF(ownerYn != null ? ownerYn : "");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        asset = in.readUTF();
        assetNm = in.readUTF();
        assetOwner = in.readUTF();
        ownerYn = in.readUTF();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

    @Override
    public String toString() {
        return  "asset : " + asset + "\n" +
                "assetNm : " + assetNm + "\n" +
                "assetOwner : " + assetOwner + "\n" +
                "ownerYn : " + ownerYn + "\n";
    }
}
