package balloonHarbourServer.cryptography.aes;

public class Aes {

    private static Key keys;

    public static byte[] encryptText(byte[] input, String cipherKey, boolean unicodeKey) {
        keys = new Key(cipherKey, unicodeKey);
        State[] stateBlock = AesParser.getStateBlocks(input);
        for (State state : stateBlock) {
            encryptState(state);
        }
        return AesParser.getBytesFromState(stateBlock);
    }

    public static byte[] decryptText(byte[] input, String cipherKey, boolean unicodeKey) {
        keys = new Key(cipherKey, unicodeKey);
        State[] stateBlock = AesParser.getStateBlocks(input);
        for (State state : stateBlock) {
            decryptState(state);
        }
        return AesParser.getBytesFromState(stateBlock);
    }

    private static void encryptState(State state) {
        addRoundKey(state, keys.getRoundKey(0));

        for (int i = 0; i < keys.getNr() - 1; i++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, keys.getRoundKey(i + 1));
        }

        subBytes(state);
        shiftRows(state);
        addRoundKey(state, keys.getRoundKey(keys.getNr()));
    }

    private static void decryptState(State state) {
        addRoundKey(state, keys.getRoundKey(keys.getNr()));
        invShiftRows(state);
        invSubBytes(state);

        for (int i = keys.getNr() - 2; i > -1; i--) {
            addRoundKey(state, keys.getRoundKey(i + 1));
            invMixColumns(state);
            invShiftRows(state);
            invSubBytes(state);
        }

        addRoundKey(state, keys.getRoundKey(0));
    }

    private static void addRoundKey(State s, State key) {
        for (int i = 0; i < 4; i++) {
            WordPoly word = s.collumnAsWord(i);
            WordPoly other = key.collumnAsWord(i);
            WordPoly roundWord = word.addTo(other);
            s.wordToCollumn(roundWord, i);
        }
    }

    private static void mixColumns(State s) {
        WordPoly other = new WordPoly(0x02, 0x01, 0x01, 0x03);
        for (int i = 0; i < 4; i++) {
            WordPoly word = s.collumnAsWord(i);
            WordPoly mixedWord = word.multiply(other);
            s.wordToCollumn(mixedWord, i);
        }
    }

    private static void invMixColumns(State s) {
        WordPoly other = new WordPoly(0x0e, 0x09, 0x0d, 0x0b);
        for (int i = 0; i < 4; i++) {
            WordPoly word = s.collumnAsWord(i);
            WordPoly mixedWord = word.multiply(other);
            s.wordToCollumn(mixedWord, i);
        }
    }

    private static void shiftRows(State s) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                int rowHead = s.state[i * 4];
                for (int k = 0; k < 3; k++) {
                    s.state[i * 4 + k] = s.state[i * 4 + k + 1];
                }
                s.state[i * 4 + 3] = rowHead;
            }
        }
    }

    private static void invShiftRows(State s) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                int rowTail = s.state[i * 4 + 3];
                for (int k = 2; k > -1; k--) {
                    s.state[i * 4 + k + 1] = s.state[i * 4 + k];
                }
                s.state[i * 4] = rowTail;
            }
        }
    }

    private static void subBytes(State s) {
        for (int i = 0; i < 16; i++) {
            int si = s.state[i];
            int x = (si & 0xf0) >> 4;
            int y = (si & 0x0f);
            s.state[i] = SBox.getInstance().apply(x, y);
        }
    }

    private static void invSubBytes(State s) {
        for (int i = 0; i < 16; i++) {
            int si = s.state[i];
            int x = (si & 0xf0) >> 4;
            int y = (si & 0x0f);
            s.state[i] = SBox.getInstance().invApply(x, y);
        }
    }
}