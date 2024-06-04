package game;

import javax.xml.transform.Result;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KInRowBoard implements Board {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, '0',
            Cell.E, '.',
            Cell.B, '#'
    );
    private final int n, m, k;
    private int empty;
    private final Cell[][] cells;
    private Cell turn;

    private String ban = "N";
    public Board getNewBoard() {
        return new KInRowBoard(n, m, k, ban);
    }

    public KInRowBoard(int n, int m, int k, String ban) {
        this.n = n;
        this.m = m;
        this.k = k;
        empty = n * m;
        this.cells = new Cell[n][m];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        this.ban = ban;
        if (ban.equals("Y")) {
            for (int i = 0; i < n; i += 1) {
                if (m - i - 1 < 0) {
                    break;
                }
                empty -= 1;
                cells[i][m - i - 1] = Cell.B;
                if(cells[i][i] != Cell.B) {
                    cells[i][i] = Cell.B;
                    empty -= 1;
                }
            }
        }
        turn = Cell.X;
    }

    public KInRowBoard(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
        empty = n * m;
        this.cells = new Cell[n][m];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }
    public Position getPosition() {
        return new UserPosition(this);
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public Cell getTurnCell() {
        return turn;
    }

    public boolean isValid(Move move) {
        return 0 <= move.getX() && move.getX() < getN()
                && 0 <= move.getY() && move.getY() < getM()
                && getCell(move.getX(), move.getY()) == Cell.E
                && turn == move.getValue();
    }

    public boolean isRightPosition(Move move) {
        return 0 <= move.getX() && move.getX() < getN()
                && 0 <= move.getY() && move.getY() < getM()
                && getCell(move.getX(), move.getY()) == move.getValue();
    }


    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    private int getScore(Move move, int stepX, int stepY) {
        int score = 1;
        Cell findElement = move.getValue();
        int startX = move.getX();
        int startY = move.getY();
        for (int i = 0; i < 2; i++) {
            int x = startX + stepX;
            int y = startY + stepY;
            while (isRightPosition(new Move(x, y, findElement))) {
                score += 1;
                x = x + stepX;
                y = y + stepY;
            }
            stepX = -stepX;
            stepY = -stepY;
        }
        return score;
    }

    @Override
    public GameResult makeMove(Move move) {
        move = new Move(move.getX() - 1, move.getY() - 1, move.getValue());
        if (!isValid(move)) {
            return GameResult.LOSE;
        }
        cells[move.getX()][move.getY()] = move.getValue();
        empty -= 1;
        int inDiaganal1 = getScore(move, 1, 1);
        int inDiaganal2 = getScore(move, -1, 1);
        int inRow = getScore(move, 1, 0);
        int inColumn = getScore(move, 0, 1);

        //System.out.println(inDiaganal1 + " " + inDiaganal2 + " " + inRow + " " + inColumn);

        if (inDiaganal1 >= k || inDiaganal2 >= k
                || inRow >= k || inColumn >= k) {
            return GameResult.WIN;
        }
        if (empty == 0) {
            return GameResult.DRAW;
        }
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return GameResult.UNKNOWN;
    }

    public void printBoard() {
        System.out.println("----------");
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                System.out.print(SYMBOLS.get(getCell(x, y)));
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                ans.append(SYMBOLS.get(getCell(x, y)));
            }
            ans.append(System.lineSeparator());
        }
        return ans.toString();
    }
}
