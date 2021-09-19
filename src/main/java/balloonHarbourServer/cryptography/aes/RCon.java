package balloonHarbourServer.cryptography.aes;

import java.util.ArrayList;

public class RCon {

    public ArrayList<WordPoly> rcon = new ArrayList<>();

    private RCon() {
        rcon.add(new WordPoly(0x1, 0x0, 0x0, 0x0));
        BinPoly p = new BinPoly(0x2);
        rcon.add(new WordPoly(0x2, 0x0, 0x0, 0x0));
        for (int i = 2; i < 14; i++) {
            p = p.multiply(new BinPoly(0x2));
            WordPoly temp = new WordPoly(p.poly, 0x0, 0x0, 0x0);
            rcon.add(temp);
        }
    }

    public static RCon getInstance() {
        return RConHolder.INSTANCE;
    }

    private static class RConHolder {

        private static final RCon INSTANCE = new RCon();
    }
}