package info.kgeorgiy.ja.shibanov.walk;

public interface Hasher {
    void updateHash(byte[] key, int length);
    int getHash();
}
