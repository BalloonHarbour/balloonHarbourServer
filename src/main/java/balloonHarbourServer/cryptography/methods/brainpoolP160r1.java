package balloonHarbourServer.cryptography.methods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class brainpoolP160r1 implements Method {

    BigInteger p = new BigInteger("e95e4a5f737059dc60dfc7ad95b3d8139515620f", 16);
    BigInteger a = new BigInteger("340e7be2a280eb74e2be61bada745d97e8f7c300", 16);
    BigInteger b = new BigInteger("1e589a8595423412134faa2dbdec95c8d8675e58", 16);
    BigInteger Gx = new BigInteger("bed5af16ea3f6a4f62938c4631eb5af7bdbcdbc3", 16);
    BigInteger Gy = new BigInteger("1667cb477a1a8ec338f94741669c976316da6321", 16);
    BigInteger n = new BigInteger("e95e4a5f737059dc60df5991d45029409e60fc09", 16);
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