package com.blocksearch.sdk.data.domain.wallet.eth;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigInteger;

import lombok.Data;

@Data
public class EthSendConfirmDomain implements Serializable {

    private static final long serialVersionUID = 1166329974466897042L;

    private BigInteger balance; // wei
    private String displayBalance; // Eth
    private String balanceSymbol;
    private BigInteger fee; // wei
    private String displayFee; // wei
    private long gasLimit; // Gwei
    private long gasPrice; // Gwei
    private String fromAddress;
    private String toAddress;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeUTF(balance != null ? balance.toString() : "0");
        out.writeUTF(displayBalance != null && !displayBalance.isEmpty() ? displayBalance : "0");
        out.writeUTF(balanceSymbol != null && !balanceSymbol.isEmpty() ? balanceSymbol : "ETH");
        out.writeUTF(fee != null ? fee.toString() : "0");
        out.writeUTF(displayFee != null ? displayFee : "0");
        out.writeLong(gasLimit);
        out.writeLong(gasPrice);
        out.writeUTF(fromAddress);
        out.writeUTF(toAddress);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        balance = new BigInteger(in.readUTF());
        displayBalance = in.readUTF();
        balanceSymbol = in.readUTF();
        fee = new BigInteger(in.readUTF());
        displayFee = in.readUTF();
        gasLimit = in.readLong();
        gasPrice = in.readLong();
        fromAddress = in.readUTF();
        toAddress = in.readUTF();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

}
