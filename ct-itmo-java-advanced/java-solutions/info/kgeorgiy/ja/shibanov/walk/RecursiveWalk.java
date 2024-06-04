package info.kgeorgiy.ja.shibanov.walk;

public class RecursiveWalk {
    public static void main(String[] args) {
        try {
            BaseWalk.walk(args, -1);
        } catch (WalkException e) {
            System.err.println(e.getMessage());
        }
    }
}
