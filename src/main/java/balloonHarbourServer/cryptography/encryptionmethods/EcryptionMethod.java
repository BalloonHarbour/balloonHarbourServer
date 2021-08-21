package balloonHarbourServer.cryptography.encryptionmethods;

import balloonHarbourServer.cryptography.hashes.Hash;

import java.math.BigInteger;

public interface EcryptionMethod {

    BigInteger[] getConfig();

    Hash getHash();
}