package disassembler;

public class ParseString {
    private final int[] bytes;

    public ParseString(int[] bytes) {
        this.bytes = bytes;
    }

    public String bytesToString(int index) {
        StringBuilder name = new StringBuilder();
        for (int i = index; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                break;
            }
            name.append((char) bytes[i]);
        }
        return name.toString();
    }
}
