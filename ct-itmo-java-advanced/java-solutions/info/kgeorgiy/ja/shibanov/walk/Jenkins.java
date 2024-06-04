package info.kgeorgiy.ja.shibanov.walk;

public class Jenkins implements Hasher {
    private int hash = 0;
    private static final int MAX_BYTE = 0xFF;
    public void updateHash(byte[] key, int length) {
        int i = 0;
        while (i != length) {
            hash += key[i] & MAX_BYTE;
            i++;
            hash += hash << 10;
            hash ^= hash >>> 6;
        }
    }

    public int getHash() {
        int tmp = hash;
        tmp += tmp << 3;
        tmp ^= tmp >>> 11;
        tmp += tmp << 15;
        return tmp;
    }
}
