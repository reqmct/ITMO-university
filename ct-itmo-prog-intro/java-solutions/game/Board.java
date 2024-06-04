package game;

public interface Board {
    GameResult makeMove(Move move);

    Cell getTurnCell();

    Position getPosition();

    Board getNewBoard();
}
