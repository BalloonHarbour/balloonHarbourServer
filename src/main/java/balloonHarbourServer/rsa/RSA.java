package balloonHarbourServer.rsa;

import java.math.BigInteger;
import java.util.Random;

public class RSA {

    BigInteger p;
    BigInteger q;
    BigInteger n;
    BigInteger phi;
    BigInteger e;
    BigInteger d;
    int bits;

    public RSA(int bits) {
        this.bits = bits;
    }

    public RSACredentials createCredentials() {
        p = createPrime(bits);
        q = createPrime(bits);
        n = p.multiply(q);
        phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = createE(bits * 2);
        d = modInv(e, phi)[1];

        RSACredentials c = new RSACredentials();
        c.e = e;
        c.d = d;
        c.n = n;
        return c;
    }

    private BigInteger createPrime(int bits) {
        Random r = new Random();
        BigInteger lp = BigInteger.probablePrime(bits, r);
        return lp;
    }

    private BigInteger createE(int bits) {
        Random r = new Random();
        BigInteger e; //= new BigInteger(1024, r);

        do {
            e = new BigInteger(bits, r);
            while (e.min(phi).equals(phi)) {
                e = new BigInteger(bits, r);
            }
        } while (!ggT(e, phi).equals(BigInteger.ONE));
        return e;
    }

    private BigInteger ggT(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return a;
        } else {
            return ggT(b, a.mod(b));
        }
    }

    private BigInteger[] modInv(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return new BigInteger[] {
            a, BigInteger.ONE, BigInteger.ZERO
        };
        BigInteger[] vals = modInv(b, a.mod(b));
        BigInteger d = vals[0];
        BigInteger p = vals[2];
        BigInteger q = vals[1].subtract(a.divide(b).multiply(vals[2]));
        return new BigInteger[] {
                d, p, q
        };
    }

    public BigInteger StringToCipher(String msg) {
        String cipherString = "";
        String temp = "";
        int i = 0;
        int e = 0;
        while (i < msg.length()) {
            temp += (int)msg.charAt(i);
            while (e < 3 - String.valueOf((int)msg.charAt(i)).length()) {
                temp = "0" + (int)msg.charAt(i);
                e++;
            }
            e = 0;
            cipherString += temp;
            temp = "";
            i++;
        }
        return new BigInteger(String.valueOf(cipherString));
    }

    public String CipherToString(BigInteger msg) {
        String cipherString = msg.toString();
        String output = "";

        while (cipherString.length() % 3 != 0) {
            cipherString = "0" + cipherString;
        }

        int i = 0;
        int temp;
        while (i < cipherString.length()) {
            temp = Integer.parseInt(cipherString.substring(i, i + 3));
            output += (char) temp;
            i = i + 3;
        }
        return output;
    }

    public BigInteger encrypt(BigInteger msg, RSACredentials credentials) {
        return msg.modPow(credentials.e, credentials.n);
    }

    public BigInteger decrypt(BigInteger msg, RSACredentials credentials) {
        return msg.modPow(credentials.d, credentials.n);
    }
}