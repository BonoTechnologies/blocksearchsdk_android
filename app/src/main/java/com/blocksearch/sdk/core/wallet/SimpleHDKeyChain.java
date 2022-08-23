package com.blocksearch.sdk.core.wallet;

import androidx.annotation.Nullable;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.BloomFilter;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.EncryptableKeyChain;
import org.bitcoinj.wallet.KeyBag;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.RedeemData;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.concurrent.Executor;


// prefixed m/44H/ H/ H/
// get Key of EX or IN  (m/44H/ H/ H/ EX or IN/ )
// last index addressIndex

public class SimpleHDKeyChain implements EncryptableKeyChain, KeyBag {

//    private DeterministicHierarchy hierarchy;
    private DeterministicKey rootKey;
    private final String PATH_FORMAT = "%d/%d";

    // Paths through the key tree. External keys are ones that are communicated to other parties. Internal keys are
    // keys created for change addresses, coinbases, mixing, etc - anything that isn't communicated. The distinction
    // is somewhat arbitrary but can be useful for audits.
    public static final ChildNumber EXTERNAL_PATH_NUM = ChildNumber.ZERO;
//    public static final ImmutableList<ChildNumber> EXTERNAL_PATH = ImmutableList.of(EXTERNAL_PATH_NUM);

    // The parent keys for external keys (handed out to other people) and internal keys (used for change addresses).
    private DeterministicKey externalKey, internalKey;

    // We simplify by wrapping a basic key chain and that way we get some functionality like key lookup and event
    // listeners "for free". All keys in the key tree appear here, even if they aren't meant to be used for receiving
    // money.
//    private final SimpleKeyChain simpleKeyChain;

    public SimpleHDKeyChain(DeterministicKey rootkey, int addressIndex) {
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootkey);
        externalKey = hierarchy.get(HDUtils.parsePath(String.format(PATH_FORMAT, 0, addressIndex)), true, true);
    }

    public SimpleHDKeyChain(DeterministicKey rootkey, int addressIndex, boolean isExternal) {
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootkey);
        externalKey = hierarchy.get(HDUtils.parsePath(String.format(PATH_FORMAT, isExternal ? 0 : 1, addressIndex)), true, true);
    }

    @Nullable
    public DeterministicKey getDeterministicKey() {
        return externalKey;
    }

    public String getAddress(KeyCrypter crypter, KeyParameter key) {
        return Numeric.prependHexPrefix(Keys.getAddress(getECKeyPair()));
    }

    public String getBitCoinAddress(AbstractBitcoinNetParams params, Script.ScriptType scriptType) {
        return Address.fromKey(params, externalKey, scriptType).toString();
    }

    public String getBitCoinAddress(AbstractBitcoinNetParams params) {
        return getBitCoinAddress(params, Script.ScriptType.P2PKH);
    }

    public ECKeyPair getECKeyPair() {
        return ECKeyPair.create(externalKey.getPrivKey());
    }

    @Override
    public SimpleHDKeyChain toEncrypted(CharSequence password) {
        return null;
    }

    @Override
    public EncryptableKeyChain toEncrypted(KeyCrypter keyCrypter, org.bouncycastle.crypto.params.KeyParameter aesKey) {
        return null;
    }

    @Override
    public SimpleHDKeyChain toDecrypted(CharSequence password) {
        return null;
    }

    @Override
    public EncryptableKeyChain toDecrypted(org.bouncycastle.crypto.params.KeyParameter aesKey) {
        return null;
    }

    @Override
    public boolean checkPassword(CharSequence password) {
        return false;
    }

    @Override
    public boolean checkAESKey(org.bouncycastle.crypto.params.KeyParameter aesKey) {
        return false;
    }

    @Nullable
    @Override
    public KeyCrypter getKeyCrypter() {
        return externalKey.getKeyCrypter();
    }

    @Nullable
    @Override
    public ECKey findKeyFromPubKeyHash(byte[] pubKeyHash, @Nullable Script.ScriptType scriptType) {
        return null;
    }

    @Nullable
    @Override
    public ECKey findKeyFromPubKey(byte[] pubkey) {
        return null;
    }

    @Nullable
    @Override
    public RedeemData findRedeemDataFromScriptHash(byte[] scriptHash) {
        return null;
    }

    @Override
    public boolean hasKey(ECKey key) {
        return false;
    }

    @Override
    public List<? extends ECKey> getKeys(KeyPurpose purpose, int numberOfKeys) {
        return null;
    }

    @Override
    public ECKey getKey(KeyPurpose purpose) {
        return null;
    }

    @Override
    public List<Protos.Key> serializeToProtobuf() {
        return null;
    }

    @Override
    public void addEventListener(KeyChainEventListener listener) {

    }

    @Override
    public void addEventListener(KeyChainEventListener listener, Executor executor) {

    }

    @Override
    public boolean removeEventListener(KeyChainEventListener listener) {
        return false;
    }

    @Override
    public int numKeys() {
        return 0;
    }

    @Override
    public int numBloomFilterEntries() {
        return 0;
    }

    @Override
    public long getEarliestKeyCreationTime() {
        return 0;
    }

    @Override
    public BloomFilter getFilter(int size, double falsePositiveRate, long tweak) {
        return null;
    }
}
