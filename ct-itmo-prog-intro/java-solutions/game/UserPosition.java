package game;

public class UserPosition implements Position {
    KInRowBoard board;

    public UserPosition(KInRowBoard board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return board.toString();
    }

    @Override
    public boolean isValid(Move move) {
        return board.isValid(new Move(move.getX() - 1, move.getY() - 1, move.getValue()));
    }

    @Override
    public Cell getCell(final int x, final int y) {
        return board.getCell(x - 1, y - 1);
    }
}
