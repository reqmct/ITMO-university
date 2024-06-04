package disassembler;

public class ELFHeader {
    private final int[] bytes;
    private final int EI_MAG0;
    private final int EI_MAG1;
    private final int EI_MAG2;
    private final int EI_MAG3;
    private final int EI_CLASS;
    private final int EI_DATA;
    private final int EI_VERSION;
    private final int EI_OSABI;
    private final int EI_ABIVERSION;
    private final int[] e_type = new int[2];
    private final int[] e_machine = new int[2];
    private final int[] e_version = new int[4];
    private final int[] e_entry = new int[4];
    private final int[] e_phoff = new int[4];
    private final int[] e_shoff = new int[4];
    private final int[] e_flags = new int[4];
    private final int[] e_ehsize = new int[2];
    private final int[] e_phentsize = new int[2];
    private final int[] e_phum = new int[2];
    private final int[] e_shentsize = new int[2];
    private final int[] e_shnum = new int[2];
    private final int[] e_shstrndx = new int[2];

    private int byteIndex;

    public ELFHeader(BinaryFile input) {
        this.bytes = input.getBytes(0, 52);
        EI_MAG0 = bytes[0];
        EI_MAG1 = bytes[1];
        EI_MAG2 = bytes[2];
        EI_MAG3 = bytes[3];
        EI_CLASS = bytes[4];
        EI_DATA = bytes[5];
        EI_VERSION = bytes[6];
        EI_OSABI = bytes[7];
        EI_ABIVERSION = bytes[8];
        byteIndex = 16;
        initArray(e_type);
        initArray(e_machine);
        initArray(e_version);
        initArray(e_entry);
        initArray(e_phoff);
        initArray(e_shoff);
        initArray(e_flags);
        initArray(e_ehsize);
        initArray(e_phentsize);
        initArray(e_phum);
        initArray(e_shentsize);
        initArray(e_shnum);
        initArray(e_shstrndx);
        isValid();
    }

    private void isValid() {
        if (getE_ehsize() != 52) {
            throw new IllegalArgumentException("File is not supported: incorrect file header size");
        }
        if (getEI_MAG0() != 0x7f || getEI_MAG1() != 0x45 ||
                getEI_MAG2() != 0x4c || getEI_MAG3() != 0x46) {
            throw new IllegalArgumentException("File is not supported: incorrect file signature");
        }
        if (getEI_CLASS() != 1) {
            throw new IllegalArgumentException("File is not supported: incorrect file class");
        }
        if (getEI_DATA() != 1) {
            throw new IllegalArgumentException("File is not supported: incorrect encoding method");
        }
        if (getEI_VERSION() != 1) {
            throw new IllegalArgumentException("File is not supported: incorrect elf header version");
        }
        if (getE_machine() != 0xF3) {
            throw new IllegalArgumentException("File is not supported: incorrect hardware platform architecture");
        }
        if (getE_version() != 1) {
            throw new IllegalArgumentException("File is not supported: incorrect format version number");
        }
        if (getE_shoff() == 0) {
            throw new IllegalArgumentException("File is not supported: there is no section header table");
        }
        if (getE_shentsize() != 40) {
            throw new IllegalArgumentException("File is not supported: incorrect section header size");
        }
        if (getE_shnum() == 0) {
            throw new IllegalArgumentException("File is not supported: there is no section header table");
        }
        if (getE_shstrndx() == 0) {
            throw new IllegalArgumentException("File is not supported: there is no string table");
        }
    }

    private void initArray(int[] source) {
        for (int i = 0; i < source.length; i++) {
            source[i] = bytes[byteIndex];
            byteIndex++;
        }
    }

    public void out() {
        System.out.println("ELF Header:\t");
        System.out.println("Magic:\t" + getEI_MAG());
        System.out.println("Class:\t" + getEI_CLASS());
        System.out.println("Data:\t" + getEI_DATA());
        System.out.println("Version:\t" + getEI_VERSION());
        System.out.println("OS/ABI:\t" + getEI_OSABI());
        System.out.println("ABI Version:\t" + getEI_ABIVERSION());
        System.out.println("Type:\t" + getE_type());
        System.out.println(String.format("Machine: \t %x", getE_machine()));
        System.out.println("Version:\t" + getE_version());
        System.out.println(String.format("Entry point address: \t %x", getE_entry()));
        System.out.println("Start of program headers:\t" + getE_phoff());
        System.out.println("Start of section headers:\t" + getE_shoff());
        System.out.println("Flags:\t" + getE_flags());
        System.out.println("Size of this header:\t" + getE_ehsize());
        System.out.println("Size of program headers:\t" + getE_phentsize());
        System.out.println("Number of program headers:\t" + getE_phum());
        System.out.println("Size of section headers:\t" + getE_shentsize());
        System.out.println("Number of section headers:\t" + getE_shnum());
        System.out.println("Section header string table index:\t" + getE_shstrndx());
    }


    public int getEI_MAG0() {
        return EI_MAG0;
    }

    public int getEI_MAG1() {
        return EI_MAG1;
    }

    public int getEI_MAG2() {
        return EI_MAG2;
    }

    public int getEI_MAG3() {
        return EI_MAG3;
    }

    public String getEI_MAG() {
        return String.format("%x %x %x %x", getEI_MAG0(), getEI_MAG1(), getEI_MAG2(), getEI_MAG3());
    }

    public int getEI_CLASS() {
        return EI_CLASS;
    }

    public int getEI_DATA() {
        return EI_DATA;
    }

    public int getEI_VERSION() {
        return EI_VERSION;
    }

    public int getEI_OSABI() {
        return EI_OSABI;
    }

    public int getEI_ABIVERSION() {
        return EI_ABIVERSION;
    }

    public int getE_type() {
        return Functions.bytesToInt(e_type);
    }

    public int getE_machine() {
        return Functions.bytesToInt(e_machine);
    }

    public int getE_version() {
        return Functions.bytesToInt(e_version);
    }

    public int getE_entry() {
        return Functions.bytesToInt(e_entry);
    }

    public int getE_phoff() {
        return Functions.bytesToInt(e_phoff);
    }

    public int getE_shoff() {
        return Functions.bytesToInt(e_shoff);
    }

    public int getE_flags() {
        return Functions.bytesToInt(e_flags);
    }

    public int getE_ehsize() {
        return Functions.bytesToInt(e_ehsize);
    }

    public int getE_phentsize() {
        return Functions.bytesToInt(e_phentsize);
    }

    public int getE_phum() {
        return Functions.bytesToInt(e_phum);
    }

    public int getE_shentsize() {
        return Functions.bytesToInt(e_shentsize);
    }

    public int getE_shnum() {
        return Functions.bytesToInt(e_shnum);
    }

    public int getE_shstrndx() {
        return Functions.bytesToInt(e_shstrndx);
    }

}
