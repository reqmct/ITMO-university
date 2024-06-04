package disassembler;

public class Section {
    private final int[] bytes;
    private final int[] sh_name = new int[4];
    private final int[] sh_type = new int[4];
    private final int[] sh_flags = new int[4];
    private final int[] sh_addr = new int[4];
    private final int[] sh_offset = new int[4];
    private final int[] sh_size = new int[4];
    private final int[] sh_link = new int[4];
    private final int[] sh_info = new int[4];
    private final int[] sh_addralign = new int[4];
    private final int[] sh_entsize = new int[4];
    private int byteIndex;

    public Section(int[] bytes) {
        this.bytes = bytes;
        initArray(sh_name);
        initArray(sh_type);
        initArray(sh_flags);
        initArray(sh_addr);
        initArray(sh_offset);
        initArray(sh_size);
        initArray(sh_link);
        initArray(sh_info);
        initArray(sh_addralign);
        initArray(sh_entsize);
    }

    private void initArray(int[] source) {
        for (int i = 0; i < source.length; i++) {
            source[i] = bytes[byteIndex];
            byteIndex++;
        }
    }


    public int getSh_name() {
        return Functions.bytesToInt(sh_name);
    }

    public int getSh_type() {
        return Functions.bytesToInt(sh_type);
    }

    public int getSh_flags() {
        return Functions.bytesToInt(sh_flags);
    }

    public int getSh_addr() {
        return Functions.bytesToInt(sh_addr);
    }

    public int getSh_offset() {
        return Functions.bytesToInt(sh_offset);
    }

    public int getSh_size() {
        return Functions.bytesToInt(sh_size);
    }

    public int getSh_link() {
        return Functions.bytesToInt(sh_link);
    }

    public int getSh_info() {
        return Functions.bytesToInt(sh_info);
    }

    public int getSh_addralign() {
        return Functions.bytesToInt(sh_addralign);
    }

    public int getSh_entsize() {
        return Functions.bytesToInt(sh_entsize);
    }


    public void out() {
        System.out.println(getSh_name() + "\t" +
                getSh_type() + "\t" +
                getSh_flags() + "\t" +
                getSh_addr() + "\t" +
                getSh_offset() + "\t" +
                getSh_size() + "\t" +
                getSh_link() + "\t" +
                getSh_info() + "\t" +
                getSh_addralign() + "\t" +
                getSh_entsize() + "\t");
    }

    public void out(String name) {
        System.out.println(name + "\t" +
                getSh_type() + "\t" +
                getSh_flags() + "\t" +
                getSh_addr() + "\t" +
                getSh_offset() + "\t" +
                getSh_size() + "\t" +
                getSh_link() + "\t" +
                getSh_info() + "\t" +
                getSh_addralign() + "\t" +
                getSh_entsize() + "\t");
    }
}
