package com.blocksearch.sdk.core.wallet;

import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.wallet.BasicKeyChain;

public class SimpleKeyChain extends BasicKeyChain {

    public SimpleKeyChain(KeyCrypter crypter) {
        super(crypter);
    }

}
