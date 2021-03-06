package balloonHarbourServer.cryptography;

import balloonHarbourServer.cryptography.encryptionmethods.EncryptionMethod;

import java.math.BigInteger;
import java.util.*;

public class ECC {

    private static BigInteger p, a, b, Gx, Gy, n, h;

    public ECC(EncryptionMethod m) {
        BigInteger[] cnfg = m.getConfig();
        p = cnfg[0];
        a = cnfg[1];
        b = cnfg[2];
        Gx = cnfg[3];
        Gy = cnfg[4];
        n = cnfg[5];
        h = cnfg[6];
    }

    public BigInteger[] genKeys() {
        BigInteger[] output = new BigInteger[3];

        output[0] = genPrivateKey();

        BigInteger[] P = point_mult(output[0], new BigInteger[]{Gx, Gy});

        output[1] = P[0];
        output[2] = P[1];

        return output;
    }

    private BigInteger genPrivateKey() {
        Random r = new Random();
        BigInteger max = n;
        BigInteger min = new BigInteger("2");
        BigInteger range = max.subtract(min);
        int len = max.bitLength();
        BigInteger out = new BigInteger(len, r);

        if (out.compareTo(min) < 0) {
            out = out.add(min);
        } else if (out.compareTo(range) >= 0) {
            out = out.mod(range).add(min);
        }
        return out;
    }

    /*public static BigInteger[] point_mult(BigInteger private_key, BigInteger[] G) {
        BigInteger[] res = null;
        List<Integer> i = new ArrayList<>();
        String[] s = private_key.toString(2).split("");

        for (int j = 0; j < s.length; j++) {
            i.add(Integer.parseInt(s[j]));
        }

        for (Integer bit : i) {
            res = point_add(res, res);

            if (bit == 1) {
                res = point_add(res, G);
            }
        }
        return res;
    }*/

    public static BigInteger[] point_mult(BigInteger private_key, BigInteger[] G) {
        BigInteger[] res = null;
        int iterations = private_key.bitLength();
        BigInteger bits = BigInteger.ZERO;

        while (private_key.compareTo(BigInteger.ZERO) != 0) {
            bits = bits.shiftLeft(1);
            bits = bits.or(private_key.and(BigInteger.ONE));
            private_key = private_key.shiftRight(1);
        }

        for (int i = 0; i < iterations; i++) {
            res = point_add(res, res);
            if (bits.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) {
                res = point_add(res, G);
            }
            bits = bits.shiftRight(1);
        }
        return res;
    }

    private static BigInteger[] point_add(BigInteger[] P1, BigInteger[] P2) {

        if (P1 == null) {
            return P2;
        }

        if (P2 == null) {
            return P1;
        }

        BigInteger P1x = P1[0];
        BigInteger P1y = P1[1];
        BigInteger P2x = P2[0];
        BigInteger P2y = P2[1];

        if (P1x.compareTo(P2x) == 0 && P1y.compareTo(P2y) != 0) {
            return null;
        }

        BigInteger s;
        if (P1x.compareTo(P2x) == 0) {
            s = new BigInteger("3").multiply(P1x.pow(2)).add(a).multiply((P1y.multiply(new BigInteger("2")).modInverse(p))).mod(p);
        } else {
            s = P1y.subtract(P2y).multiply(P1x.subtract(P2x).modInverse(p)).mod(p);
        }

        BigInteger[] out = new BigInteger[2];

        out[0] = s.pow(2).subtract(P1x).subtract(P2x).mod(p);
        out[1] = s.multiply(out[0].subtract(P1x)).add(P1y).negate().mod(p);
        return out;
    }
}