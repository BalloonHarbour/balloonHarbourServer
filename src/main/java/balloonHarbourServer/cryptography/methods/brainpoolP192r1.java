package balloonHarbourServer.cryptography.methods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class brainpoolP192r1 implements Method {

    BigInteger p = new BigInteger("c302f41d932a36cda7a3463093d18db78fce476de1a86297", 16);
    BigInteger a = new BigInteger("6a91174076b1e0e19c39c031fe8685c1cae040e5c69a28ef", 16);
    BigInteger b = new BigInteger("469a28ef7c28cca3dc721d044f4496bcca7ef4146fbf25c9", 16);
    BigInteger Gx = new BigInteger("c0a0647eaab6a48753b033c56cb0f0900a2f5c4853375fd6", 16);
    BigInteger Gy = new BigInteger("14b690866abd5bb88b5f4828c1490002e6773fa2fa299b8f", 16);
    BigInteger n = new BigInteger("c302f41d932a36cda7a3462f9e9e916b5be8f1029ac4acc1", 16);
    BigInteger h = new BigInteger("1", 16);

    @Override
    public BigInteger[] getConfig() {
        return new BigInteger[]{p, a, b, Gx, Gy, n, h};
    }

    @Override
    public Hash getHash() {
        return new SHA256();
    }
}