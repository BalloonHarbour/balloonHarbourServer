package balloonHarbourServer.cryptography.aes;

public class BinPoly {

    public int poly;

    public BinPoly(int poly) {
        this.poly = poly;
    }

    public BinPoly addTo(BinPoly other) {
        return new BinPoly(this.poly ^ other.poly);
    }

    public void addToSelf(int other) {
        this.poly = this.poly ^ other;
    }

    public BinPoly multiply(BinPoly other) {
        int p = this.poly;
        int q = other.poly;
        BinPoly result = new BinPoly(0);
        int mask = 1;
        for (int i = 0; i < 8; i++) {
            if ((mask & q) != 0) {
                result.addToSelf(p);
            }

            p = p << 1;
            if (p > 255) {
                p = (p ^ 0x1B) & 0xFF;
            }
            mask = mask << 1;
        }
        return result;
    }
}