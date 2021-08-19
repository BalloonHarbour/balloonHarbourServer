package balloonHarbourServer.cryptography.methods;

import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;

import java.math.BigInteger;

public class brainpoolP320r1 implements Method {

    BigInteger p = new BigInteger("d35e472036bc4fb7e13c785ed201e065f98fcfa6f6f40def4f92b9ec7893ec28fcd412b1f1b32e27", 16);
    BigInteger a = new BigInteger("3ee30b568fbab0f883ccebd46d3f3bb8a2a73513f5eb79da66190eb085ffa9f492f375a97d860eb4", 16);
    BigInteger b = new BigInteger("520883949dfdbc42d3ad198640688a6fe13f41349554b49acc31dccd884539816f5eb4ac8fb1f1a6", 16);
    BigInteger Gx = new BigInteger("43bd7e9afb53d8b85289bcc48ee5bfe6f20137d10a087eb6e7871e2a10a599c710af8d0d39e20611", 16);
    BigInteger Gy = new BigInteger("14fdd05545ec1cc8ab4093247f77275e0743ffed117182eaa9c77877aaac6ac7d35245d1692e8ee1", 16);
    BigInteger n = new BigInteger("d35e472036bc4fb7e13c785ed201e065f98fcfa5b68f12a32d482ec7ee8658e98691555b44c59311", 16);
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