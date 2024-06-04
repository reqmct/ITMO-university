package info.kgeorgiy.ja.shibanov.walk;

public class Walk {
    public static void main(String[] args) {
        try {
            BaseWalk.walk(args, 0);
        } catch (WalkException e) {
            System.err.println(e.getMessage());
        }
    }
}