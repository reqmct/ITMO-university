package info.kgeorgiy.ja.shibanov.walk;

public class WalkException extends Exception{
    public WalkException(String message, Exception e) {
        super(message, e);
    }
    public WalkException(String message) {
        super(message);
    }
}
