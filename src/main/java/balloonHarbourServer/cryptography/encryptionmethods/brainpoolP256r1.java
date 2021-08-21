package balloonHarbourServer.cryptography.encryptionmethods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class brainpoolP256r1 implements EcryptionMethod {

    BigInteger p = new BigInteger("a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e5377", 16);
    BigInteger a = new BigInteger("7d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9", 16);
    BigInteger b = new BigInteger("26dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b6", 16);
    BigInteger Gx = new BigInteger("8bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262", 16);
    BigInteger Gy = new BigInteger("547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f046997", 16);
    BigInteger n = new BigInteger("a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a7", 16);
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