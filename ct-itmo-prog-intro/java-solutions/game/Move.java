package game;

public class Move {
    private final int x;
    private final int y;
    private final Cell value;

    public Move(final int x, final int y, final Cell value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Cell getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "row=" + x + ", column=" + y + ", value=" + value;
    }
}
