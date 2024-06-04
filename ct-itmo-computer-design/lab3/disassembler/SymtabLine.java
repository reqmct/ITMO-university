package disassembler;

public class SymtabLine {
    private final int[] bytes;
    private final int indexOfLine;
    private final int[] name = new int[4];
    private final int[] value = new int[4];
    private final int[] size = new int[4];
    private final int info;
    private final int other;
    private final int[] shndx = new int[2];
    private int byteIndex;
    private final SectionHeader sectionHeader;
    private final ParseString names;

    public SymtabLine(int[] bytes, SectionHeader sectionHeader, ParseString names, int indexOfLine) {
        this.bytes = bytes;
        this.sectionHeader = sectionHeader;
        this.names = names;
        this.indexOfLine = indexOfLine;
        initArray(name);
        initArray(value);
        initArray(size);
        info = bytes[byteIndex++];
        other = bytes[byteIndex++];
        initArray(shndx);
    }

    @Override
    public String toString() {
        String out = String.format("[%4d] 0x%-15X %5d %-8s %-8s %-8s %6s %s", getIndexOfLine(), getValue(), getSize(),
                getType(), getBind(), getVis(), getShndx(), getName()
        );
        return out;
    }

    private void initArray(int[] source) {
        for (int i = 0; i < source.length; i++) {
            source[i] = bytes[byteIndex];
            byteIndex++;
        }
    }

    public String getName() {
        return names.bytesToString(Functions.bytesToInt(name));
    }

    public int getValue() {
        return Functions.bytesToInt(value);
    }

    public int getSize() {
        return Functions.bytesToInt(size);
    }

    public String getBind() {
        int out = (info >> 4);
        switch (out) {
            case 0 -> {
                return "LOCAL";
            }
            case 1 -> {
                return "GLOBAL";
            }
            case 2 -> {
                return "WEAK";
            }
            case 10 -> {
                return "LOOS";
            }
            case 12 -> {
                return "HIOS";
            }
            case 13 -> {
                return "LOPROC";
            }
            case 15 -> {
                return "HIPROC";
            }
        }
        return "";
    }

    public String getType() {
        int out = (((info) & 0xf));
        switch (out) {
            case 0 -> {
                return "NOTYPE";
            }
            case 1 -> {
                return "OBJECT";
            }

            case 2 -> {
                return "FUNC";
            }
            case 3 -> {
                return "SECTION";
            }
            case 4 -> {
                return "FILE";
            }
            case 5 -> {
                return "COMMON";
            }
            case 6 -> {
                return "TLS";
            }
            case 10 -> {
                return "LOOS";
            }
            case 12 -> {
                return "HIOS";
            }
            case 13 -> {
                return "LOPROC";
            }
            case 15 -> {
                return "HIPROC";
            }
        }
        return "";
    }

    public String getVis() {
        int out = ((other) & 0x3);
        switch (out) {
            case 0 -> {
                return "DEFAULT";
            }
            case 1 -> {
                return  "INTERNAL";
            }
            case 2 -> {
                return "HIDDEN";
            }
            case 3 -> {
                return "PROTECTED";
            }
        }
        return "";
    }

    public int getInfo() {
        return info;
    }

    public int getOther() {
        return other;
    }

    public String getShndx() {
        int index = Functions.bytesToInt(shndx);
        switch (index) {
            case 0 -> {
                return "UNDEF";
            }
            case 0xFF00 -> {
                return "LOPROC";
            }
            case 0xFF1F -> {
                return "HIPROC";
            }
            case 0xFFF1 -> {
                return "ABS";
            }
            case 0xFFF2 -> {
                return "COMMON";
            }
            case 0xFFFF -> {
                return "HIREVERVE";
            }
            default -> {
                return Integer.toString(index);
            }
        }

    }

    public int getIndexOfLine() {
        return indexOfLine;
    }
}
