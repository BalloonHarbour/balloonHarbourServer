package balloonHarbourServer.cryptography.encryptionmethods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class secp224r1 implements EcryptionMethod {

    BigInteger p = new BigInteger("ffffffffffffffffffffffffffffffff000000000000000000000001", 16);
    BigInteger a = new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffffffffffe", 16);
    BigInteger b = new BigInteger("b4050a850c04b3abf54132565044b0b7d7bfd8ba270b39432355ffb4", 16);
    BigInteger Gx = new BigInteger("b70e0cbd6bb4bf7f321390b94a03c1d356c21122343280d6115c1d21", 16);
    BigInteger Gy = new BigInteger("bd376388b5f723fb4c22dfe6cd4375a05a07476444d5819985007e34", 16);
    BigInteger n = new BigInteger("ffffffffffffffffffffffffffff16a2e0b8f03e13dd29455c5c2a3d", 16);
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