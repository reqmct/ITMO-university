package disassembler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BinaryFile implements AutoCloseable {
    private final List<Integer> bytes;
    private final FileInputStream reader;

    public BinaryFile(final String source) throws IOException {
        bytes = new ArrayList<>();
        reader = new FileInputStream(source);
        int read;
        while ((read = reader.read()) != -1) {
            bytes.add(read);
        }
    }

    public int[] getBytes(int index, int size) {
        try {
            int[] outBytes = new int[size];
            for (int i = 0; i < size; i++) {
                outBytes[i] = bytes.get(index + i);
            }
            return outBytes;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("File not supported: invalid file size");
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }


}
