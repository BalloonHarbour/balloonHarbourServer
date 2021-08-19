package balloonHarbourServer.cryptography;

import balloonHarbourServer.cryptography.methods.Method;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ECC {

    BigInteger p, a, b, Gx, Gy, n, h;

    public ECC(Method m) {
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

        if (out.compareTo(min) > 0) {
            out = out.add(min);
        } else if (out.compareTo(range) >= 0) {
            out = out.mod(range).add(min);
        }
        return out;
    }

    private BigInteger[] point_mult(BigInteger private_key, BigInteger[] G) {
        BigInteger[] res = new BigInteger[2];
        List<Integer> i = new ArrayList<>();
        String[] s = private_key.toString(2).split("");

        for (int c = 0; c < private_key.toString(2).length(); c++) {
            i.add(Integer.parseInt(s[c]));
        }

        for (int j = 0; j < i.size(); j++) {
            if (j == 0) {
                res = G;
            } else {
                res = point_add(res, res);

                if (i.get(j) == 1) {
                    res = point_add(res, G);
                }
            }
        }
        return res;
    }

    private BigInteger[] point_add(BigInteger[] P1, BigInteger[] P2) {
        BigInteger P1x = P1[0];
        BigInteger P1y = P1[1];
        BigInteger P2x = P2[0];
        BigInteger P2y = P2[1];

        if (P1x.compareTo(P2x) == 0 && P1y.compareTo(P2y) != 0) {
            return null;
        }

        BigInteger s;
        if (P1x.compareTo(P2x) == 0) {
            s = ((new BigInteger("3")).multiply(P1x.pow(2)).mod(p).add(a)).mod(p).multiply((P1y.multiply(new BigInteger("2")).modInverse(p))).mod(p);
        } else {
            s = P2y.subtract(P1y).multiply(P2x.subtract(P1x).modInverse(p)).mod(p);
        }

        BigInteger[] out = new BigInteger[2];

        out[0] = s.pow(2).mod(p).subtract(P1x.multiply(new BigInteger("2"))).mod(p);
        out[1] = s.multiply(P1x.subtract(out[0])).mod(p).subtract(P1y.multiply(new BigInteger("2"))).mod(p);
        return out;
    }
}