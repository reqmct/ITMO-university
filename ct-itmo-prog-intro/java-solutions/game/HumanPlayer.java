package game;

import java.io.PrintStream;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    Move getMove(final Cell cell) {
        while (true) {
            String line = in.nextLine();
            Scanner scanLine = new Scanner(line);
            int x, y;
            if (scanLine.hasNextInt()) {
                x = scanLine.nextInt();
            } else {
                System.out.println("Incorrect values. Repeat the input:");
                continue;
            }
            if (scanLine.hasNextInt()) {
                y = scanLine.nextInt();
            } else {
                System.out.println("Incorrect values. Repeat the input:");
                continue;
            }
            if(scanLine.hasNext()){
                System.out.println("Incorrect values. Repeat the input:");
                continue;
            }
            return new Move(x, y, cell);
        }
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            out.println("Position");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");
            final Move move = getMove(cell);
            if (position.isValid(move)) {
                return move;
            }
            final int x = move.getX();
            final int y = move.getY();
            out.println("Move " + move + " is invalid");
        }
    }
}

