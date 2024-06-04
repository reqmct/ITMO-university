import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyScanner implements Closeable {
    private final InputStreamReader in;
    private final char[] buffer = new char[1024];
    private int bufferIndex, bufferLen;
    private StringBuilder word = new StringBuilder();
    private final String lineSeparator = System.lineSeparator();

    public MyScanner(InputStream source) {
        in = new InputStreamReader(source);
    }

    public MyScanner(File source, String encoding) throws IOException {
        in = new InputStreamReader(new FileInputStream(source), encoding);
    }

    public MyScanner(String source) {
        in = new InputStreamReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8
        );
    }


    public boolean hasNext() {
        if (!word.isEmpty()) {
            return true;
        }
        try {
            fillBuffer();
            skipSpace();
            if (isEndOfBuffer()) {
                return false;
            }
            word = new StringBuilder(next());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String next() {
        try {
            if (word.isEmpty()) {
                while (!isEndOfBuffer() && !Character.isWhitespace(getSymbol())) {
                    word.append(getSymbol());
                    nextSymbol();
                }
            }
            String next = word.toString();
            word = new StringBuilder();
            return next;
        } catch (IOException e) {
            return "";
        }
    }

    public boolean hasNextInt() {
        if (!hasNext()) {
            return false;
        }
        if (word.isEmpty()) {
            return false;
        }
        if (word.charAt(word.length() - 1) == 'o' || word.charAt(word.length() - 1) == 'O') {
            return tryParseUnsignedInt(word.substring(0, word.length() - 1), 8);
        }
        return tryParseInt(word.toString(), 10);
    }

    public int nextInt() {
        String next = next();
        if (next.charAt(next.length() - 1) == 'o' || next.charAt(next.length() - 1) == 'O') {
            return Integer.parseUnsignedInt(next.substring(0, next.length() - 1), 8);
        }
        return Integer.parseInt(next);
    }

    public boolean hasNextLine() {
        try {
            fillBuffer();
            return bufferLen != -1;
        } catch (IOException e) {
            return false;
        }
    }

    public String nextLine() {
        try {
            while (!isEndOfBuffer() && !isLineSeparator()) {
                word.append(getSymbol());
                nextSymbol();
            }
            goToNextLine();
            String line = word.toString();
            word = new StringBuilder();
            return line;
        } catch (IOException e) {
            return "";
        }
    }

    private void skipSpace() throws IOException {
        while (!isEndOfBuffer() && Character.isWhitespace(getSymbol())) {
            nextSymbol();
        }
    }

    public boolean isEndOfLine() {
        try {
            while (!isEndOfBuffer() && Character.isWhitespace(getSymbol())) {
                if (isLineSeparator()) {
                    return true;
                }
                nextSymbol();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isLineSeparator() throws IOException {
        return getSymbol() == lineSeparator.charAt(0);
    }

    public void goToNextLine() throws IOException {
        try {
            for (int i = 0; i < lineSeparator.length() - 1; i++) {
                nextSymbol();
            }
            bufferIndex++;
        } catch (IOException e) {
            return;
        }
    }

    private void nextSymbol() throws IOException {
        bufferIndex++;
        fillBuffer();
    }

    private char getSymbol() throws IOException {
        fillBuffer();
        return buffer[bufferIndex];
    }

    private void fillBuffer() throws IOException {
        if (bufferIndex == bufferLen) {
            bufferIndex = 0;
            bufferLen = in.read(buffer);
        }
    }

    private boolean isDigit(String symbols) {
        int start = 0;
        if (symbols.charAt(0) == '-') {
            start = 1;
            if (symbols.length() == 1) {
                return false;
            }
        }
        for (int i = start; i < symbols.length(); i++) {
            if (!Character.isDigit(symbols.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isEndOfBuffer() {
        return bufferIndex >= bufferLen;
    }

    public void close() throws IOException {
        in.close();
    }

    private boolean tryParseInt(String s, int radix) {

        if (s == null) {
            return false;
        }

        if (radix < Character.MIN_RADIX) {
            return false;
        }

        if (radix > Character.MAX_RADIX) {
            throw new NumberFormatException("radix " + radix +
                " greater than Character.MAX_RADIX");
        }

        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return false;
                }

                if (len == 1) { // Cannot have lone "+" or "-"
                    return false;
                }
                i++;
            }
            int multmin = limit / radix;
            int result = 0;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0 || result < multmin) {
                    return false;
                }
                result *= radix;
                if (result < limit + digit) {
                    return false;
                }
                result -= digit;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean tryParseUnsignedInt(String s, int radix) {
        if (s == null) {
            return false;
        }

        int len = s.length();
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar == '-') {
                return false;
            } else {
                if (len <= 5 || // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits
                    (radix == 10 && len <= 9)) { // Integer.MAX_VALUE in base 10 is 10 digits
                    return tryParseInt(s, radix);
                } else {
                    long ell = Long.parseLong(s, radix);
                    return (ell & 0xffff_ffff_0000_0000L) == 0;
                }
            }
        } else {
            return false;
        }
    }
}