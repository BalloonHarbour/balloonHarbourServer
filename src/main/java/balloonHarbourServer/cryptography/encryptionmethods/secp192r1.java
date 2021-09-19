package balloonHarbourServer.cryptography.encryptionmethods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class secp192r1 implements EncryptionMethod {

    BigInteger p = new BigInteger("fffffffffffffffffffffffffffffffeffffffffffffffff", 16);
    BigInteger a = new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffc", 16);
    BigInteger b = new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16);
    BigInteger Gx = new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16);
    BigInteger Gy = new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16);
    BigInteger n = new BigInteger("ffffffffffffffffffffffff99def836146bc9b1b4d22831", 16);
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