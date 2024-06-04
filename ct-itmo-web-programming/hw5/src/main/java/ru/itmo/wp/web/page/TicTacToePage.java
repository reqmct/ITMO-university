package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    // TODO: Implement it.

    private void action(HttpServletRequest request, Map<String, Object> view) {
        State state = (State) request.getSession().getAttribute("state");
        if (state == null) {
            newGame(request, view);
        } else {
            view.put("state", state);
        }
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        State state = new State();
        view.put("state", state);
        request.getSession().setAttribute("state", state);
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        State state = (State) request.getSession().getAttribute("state");
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String cell = names.nextElement();
            if (isCell(cell)) {
                state.doMove(cell.charAt(5) - '0',
                        cell.charAt(6) - '0');
                view.put("state", state);
                break;
            }
        }
    }

    private boolean isCell(final String cell) {
        return cell.matches("cell_[0-9]{2}");
    }

    public static final class State {
        private final int size;
        private final Character[][] cells;
        private String phase;
        private boolean crossesMove;

        private int cellScore;

        public State() {
            size = 3;
            cells = new Character[size][size];
            phase = "RUNNING";
            crossesMove = true;
            cellScore = 0;
        }

        public int getSize() {
            return size;
        }

        public Character[][] getCells() {
            return cells;
        }

        public String getPhase() {
            return phase;
        }

        public boolean isCrossesMove() {
            return crossesMove;
        }

        private char getMark() {
            return isCrossesMove() ? 'X' : 'O';
        }

        public void doMove(final int x, final int y) {
            if (isCorrectMove(x, y)) {
                cells[x][y] = getMark();
                update(x, y);
            }
        }

        private boolean checkBorder(final int x, final int y) {
            return 0 <= x && x < size &&
                    0 <= y && y < size;
        }

        public boolean isCorrectMove(final int x, final int y) {
            return checkBorder(x, y) &&
                    cells[x][y] == null;
        }

        private void update(int x, int y) {
            cellScore++;
            updPhase(x, y);
            crossesMove = !crossesMove;
        }

        private void updPhase(final int x, final int y) {
            if (isWin(x, y)) {
                phase = "WON_" + getMark();
            } else if (cellScore == size * size) {
                phase = "DRAW";
            }
        }

        private boolean isWin(final int x, final int y) {
            return checkLine(x, y, 1, 0) ||
                    checkLine(x, y, 0, 1) ||
                    checkLine(x, y, 1, 1) ||
                    checkLine(x, y, -1, 1);
        }

        private boolean checkLine(final int x, final int y, final int addX, final int addY) {
            return checkDirectionLine(x, y, addX, addY) + checkDirectionLine(x, y, -addX, -addY) - 1 ==
                    size;
        }

        private int checkDirectionLine(final int x, final int y, final int addX, final int addY) {
            int score = 0;
            for (int i = x, j = y; checkBorder(i, j); i += addX, j += addY) {
                if (cells[i][j] != cells[x][y]) {
                    return 0;
                }
                score++;
            }
            return score;
        }

    }

}
