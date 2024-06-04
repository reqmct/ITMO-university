package game;

import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private Player players[];
    private int results[];

    public Tournament(Player players[]) {
        this.players = players;
        results = new int[players.length];

    }

    private void addResults(int resultOfGame, int i, int j) {
        if (resultOfGame == 0) {
            System.out.println("Game result is draw");
            results[i] = results[i] + 1;
            results[j] = results[j] + 1;
        } else if (resultOfGame == 1) {
            System.out.println("Won " + (i + 1));
            results[i] = results[i] + 3;
        } else {
            System.out.println("Won " + (j + 1));
            results[j] = results[j] + 3;
        }

    }

    public void play(Board board) {
        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {
                System.out.println("Player " + (i + 1) + " and " + (j + 1) + " are playing");
                final Game game1 = new Game(false, players[i], players[j]);
                int resultOfGame1 = game1.play(board);
                board = board.getNewBoard();
                addResults(resultOfGame1, i, j);
                System.out.println("Player " + (j + 1) + " and " + (i + 1) + " are playing");
                final Game game2 = new Game(false, players[j], players[i]);
                int resultOfGame2 = game2.play(board);
                board = board.getNewBoard();
                addResults(resultOfGame2, j, i);
                getResult();
            }
        }
    }

    private void getResult() {
        System.out.println("Results table:");
        for (int i = 0; i < results.length; i++) {
            System.out.println((i + 1) + " - " + results[i]);
        }
    }
}
