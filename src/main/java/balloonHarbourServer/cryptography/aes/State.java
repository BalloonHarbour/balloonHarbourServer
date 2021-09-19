package balloonHarbourServer.cryptography.aes;

public class State {

    public int[] state = new int[16];

    public WordPoly collumnAsWord(int x){
        return new WordPoly(state[x],state[4+x],state[8+x], state[12+x]);
    }

    public void wordToCollumn(WordPoly w, int x){
        state[x] = w.x0.poly;
        state[4+x] = w.x1.poly;
        state[8+x] = w.x2.poly;
        state[12+x] = w.x3.poly;
    }
}