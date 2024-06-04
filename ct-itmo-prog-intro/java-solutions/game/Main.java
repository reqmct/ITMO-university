package game;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int n, m, k, playersCount;
        String ban = "N";
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Enter n, m, k:");
            while (true) {
                String line = in.nextLine();
                Scanner scanLine = new Scanner(line);
                if (scanLine.hasNextInt()) {
                    n = scanLine.nextInt();
                    if (n <= 0) {
                        System.out.println("Enter the correct value");
                        continue;
                    }
                } else {
                    System.out.println("Enter the correct value");
                    continue;
                }
                if (scanLine.hasNextInt()) {
                    m = scanLine.nextInt();
                    if (m <= 0) {
                        System.out.println("Enter the correct value");
                        continue;
                    }
                } else {
                    System.out.println("Enter the correct value");
                    continue;
                }
                if (scanLine.hasNextInt()) {
                    k = scanLine.nextInt();
                    if (k <= 0) {
                        System.out.println("Enter the correct value");
                        continue;
                    }
                } else {
                    System.out.println("Enter the correct value");
                    continue;
                }
                if (scanLine.hasNext()) {
                    System.out.println("Enter the correct value");
                    continue;
                }
                break;
            }
            System.out.println("Enter the number of players");
            while (true) {
                String line = in.nextLine();
                Scanner scanLine = new Scanner(line);
                if (scanLine.hasNextInt()) {
                    playersCount = scanLine.nextInt();
                    if (playersCount <= 0) {
                        System.out.println("Enter the correct value");
                        continue;
                    }
                } else {
                    System.out.println("Enter the correct value");
                    continue;
                }
                break;
            }
            Player players[] = new Player[playersCount];
            players[0] = new HumanPlayer(); // добавление реального игрока

            players[1] = new HumanPlayer();
            for (int i = 2; i < playersCount; i += 1) {
                players[i] = new RandomPlayer(n, m);
            }
            if (n == m) {
                System.out.println("Do you want to add forbidden cells to the diagonal?(Y/N)");
                while (true) {
                    String line = in.nextLine();
                    Scanner scanLine = new Scanner(line);
                    if (scanLine.hasNext()) {
                        ban = scanLine.next();
                        if (!(ban.equals("Y") || ban.equals("N"))) {
                            System.out.println("Enter the correct value");
                            continue;
                        }
                    } else {
                        System.out.println("Enter the correct value");
                        continue;
                    }
                    break;
                }
            }
            final Tournament tournament = new Tournament(players);
            tournament.play(new KInRowBoard(n, m, k, ban));
        } catch (NoSuchElementException e){
            System.out.println("Exit...");
        }
    }
}
