package balloonHarbourServer.cryptography.methods;

import balloonHarbourServer.cryptography.hashes.Hash;

import java.math.BigInteger;

public interface Method {

    BigInteger[] getConfig();

    Hash getHash();
}