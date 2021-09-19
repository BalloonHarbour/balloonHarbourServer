package balloonHarbourServer.cryptography.aes;

import java.util.Arrays;

public class AesParser {

    public static WordPoly charsToWord(char ch1, char ch2) {
        int c1 = ch1;
        int c2 = ch2;

        return new WordPoly((c1 & 0xff00) >> 8,
                (c1 & 0x00ff),
                (c2 & 0xff00) >> 8,
                (c2 & 0x00ff));
    }

    public static State[] getStateBlocks(byte[] input) {
        byte[] data;
        if (input.length % 16 != 0) {
            data = new byte[input.length - input.length % 16 + 16];
            System.arraycopy(input, 0, data, 0, input.length);
        } else {
            data = input;
        }

        State[] result = new State[data.length / 16];
        for (int i = 0; i < data.length / 16; i++) {
            result[i] = getStateFromBytes(Arrays.copyOfRange(data, i * 16, (i + 1) * 16));
        }
        return result;
    }

    public static State getStateFromBytes(byte[] bytes) {
        State result = new State();
        for (int i = 0; i < 4; i++) {
            result.wordToCollumn(bytesToWord(Arrays.copyOfRange(bytes, i * 4, (i + 1) * 4)), i);
        }
        return result;
    }

    public static WordPoly bytesToWord(byte[] bytes) {
        return new WordPoly((int)bytes[0], (int)bytes[1], (int)bytes[2], (int)bytes[3]);
    }

    public static byte[] getBytesFromState(State[] states) {
        byte[] result = new byte[states.length * 16];
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < 4; j++) {
                byte[] b = wordToByte(states[i].collumnAsWord(j));
                result[i * 16 + j * 4] = b[0];
                result[i * 16 + j * 4 + 1] = b[1];
                result[i * 16 + j * 4 + 2] = b[2];
                result[i * 16 + j * 4 + 3] = b[3];
            }
        }
        return TrimEnd(result);
    }

    public static byte[] wordToByte(WordPoly word) {
        byte[] result = new byte[4];
        result[0] = (byte) word.x0.poly;
        result[1] = (byte) word.x1.poly;
        result[2] = (byte) word.x2.poly;
        result[3] = (byte) word.x3.poly;
        return result;
    }

    public static byte[] TrimEnd(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOfRange(bytes, 0, i + 1);
    }
}