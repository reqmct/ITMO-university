package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random;
    private int patternN = 4;
    private int patternM = 4;

    public RandomPlayer(final Random random) {
        this.random = random;
    }

    public RandomPlayer() {
        random = new Random();
    }

    public RandomPlayer(int patternN, int patternM) {
        random = new Random();
        this.patternN = patternN + 1;
        this.patternM = patternM + 1;
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            int r = random.nextInt(patternN);
            int c = random.nextInt(patternM);
            final Move move = new Move(r, c, cell);
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}
