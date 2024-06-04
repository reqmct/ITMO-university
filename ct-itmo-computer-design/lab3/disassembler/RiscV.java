package disassembler;

public class RiscV {
    private final int value;
    private final int addr;

    private final Symtab symtab;
    private String command = "unknown_instruction";
    private int markIndex;
    private String mark;

    public RiscV(int value, int addr, Symtab symtab) {
        this.value = value;
        this.addr = addr;
        this.symtab = symtab;
        parseOpcode();
    }

    public int getAddr() {
        return addr;
    }

    public boolean hasMark() {
        switch (command) {
            case "jal", "beq", "bne", "blt", "bge", "bltu", "bgeu" -> {
                return true;
            }
        }
        return false;
    }

    public int getMarkIndex() {
        return markIndex;
    }

    public String getMark() {
        if (mark == null) {
            mark = "<" + symtab.getFunctionNameByValue(getMarkIndex()) + ">";
        }
        return mark;
    }

    private int getOpcode() {
        return Functions.slice(value, 6, 0);
    }

    private String rType() {
        int funct7 = Functions.slice(value, 31, 25);
        String rs2 = Functions.getReg(Functions.slice(value, 24, 20));
        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
        int funct3 = Functions.slice(value, 14, 12);
        String rd = Functions.getReg(Functions.slice(value, 11, 7));
        int opcode = getOpcode();
        switch (opcode) {
            case 0b0010011 -> {
                switch (funct7) {
                    case 0b0000000, 0b0000001 -> {
                        switch (funct3) {
                            case 0b001 -> command = "slli";
                            case 0b101 -> command = "srli";
                        }
                    }
                    case 0b0100000, 0b0100001 -> command = "srai";
                }
            }
            case 0b0110011 -> {
                switch (funct7) {
                    case 0b0000000 -> {
                        switch (funct3) {
                            case 0b000 -> command = "add";
                            case 0b001 -> command = "sll";
                            case 0b010 -> command = "slt";
                            case 0b011 -> command = "sltu";
                            case 0b100 -> command = "xor";
                            case 0b101 -> command = "srl";
                            case 0b110 -> command = "or";
                            case 0b111 -> command = "and";
                        }
                    }
                    case 0b0100000 -> {
                        switch (funct3) {
                            case 0b000 -> command = "sub";
                            case 0b101 -> command = "sra";
                        }
                    }
                    case 0b0000001 -> {
                        switch (funct3) {
                            case 0b000 -> command = "mul";
                            case 0b001 -> command = "mulh";
                            case 0b010 -> command = "mulhsu";
                            case 0b011 -> command = "mulhu";
                            case 0b100 -> command = "div";
                            case 0b101 -> command = "divu";
                            case 0b110 -> command = "rem";
                            case 0b111 -> command = "remu";
                        }
                    }
                }
            }
        }
        return String.format("%7s \t %s, %s, %s", command, rd, rs1, rs2);
    }

    private String iType() {
        int imm = Functions.getValue(Functions.slice(value, 31, 20), 31 - 20);
        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
        int funct3 = Functions.slice(value, 14, 12);
        String rd = Functions.getReg(Functions.slice(value, 11, 7));
        int opcode = getOpcode();
        switch (opcode) {
            case 0b1100111 -> {
                command = "jalr";
                return String.format("%7s \t %s, %s(%s)", command, rd, imm, rs1);
            }
            case 0b0000011 -> {
                switch (funct3) {
                    case 0b000 -> command = "lb";
                    case 0b001 -> command = "lh";
                    case 0b010 -> command = "lw";
                    case 0b100 -> command = "lbu";
                    case 0b101 -> command = "lhu";
                }
                return String.format("%7s \t %s, %s(%s)", command, rd, imm, rs1);
            }
            case 0b0010011 -> {
                switch (funct3) {
                    case 0b000 -> command = "addi";
                    case 0b010 -> command = "slti";
                    case 0b011 -> command = "sltiu";
                    case 0b100 -> command = "xori";
                    case 0b110 -> command = "ori";
                    case 0b111 -> command = "andi";
                    case 0b001, 0b101 -> {
                        return rType();
                    }
                }
            }
        }
        return String.format("%7s \t %s, %s, %s", command, rd, rs1, imm);
    }

    private String sType() {
        int imm = (Functions.slice(value, 31, 25) << 5) + Functions.slice(value, 11, 7);
        imm = Functions.getValue(imm, 11);
        String rs2 = Functions.getReg(Functions.slice(value, 24, 20));
        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
        int funct3 = Functions.slice(value, 14, 12);
        int opcode = getOpcode();
        switch (opcode) {
            case 0b0100011 -> {
                switch (funct3) {
                    case 0b000 -> command = "sb";
                    case 0b001 -> command = "sh";
                    case 0b010 -> command = "sw";
                }
            }
        }
        return String.format("%7s \t %s, %s(%s)", command, rs2, imm, rs1);
    }

    private String bType() {
        int offset = Functions.slice(value, 31, 31);
        offset = (offset << 1) + Functions.slice(value, 7, 7);
        offset = (offset << 6) + Functions.slice(value, 30, 25);
        offset = (offset << 4) + Functions.slice(value, 11, 8);
        offset = Functions.getValue(offset << 1, 12);
        String rs2 = Functions.getReg(Functions.slice(value, 24, 20));
        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
        int funct3 = Functions.slice(value, 14, 12);
        int opcode = getOpcode();
        markIndex = addr + offset;
        switch (Functions.slice(value, 14, 12)) {
            case 0b000 -> command = "beq";
            case 0b001 -> command = "bne";
            case 0b100 -> command = "blt";
            case 0b101 -> command = "bge";
            case 0b110 -> command = "bltu";
            case 0b111 -> command = "bgeu";
        }
        return String.format("%7s \t %s, %s, %x", command, rs1, rs2, addr + offset);
    }

    private String uType() {
        int imm = Functions.getValue(Functions.slice(value, 31, 12), 31 - 12);
        String rd = Functions.getReg(Functions.slice(value, 11, 7));
        int opcode = getOpcode();
        switch (opcode) {
            case 0b0110111 -> command = "lui";
            case 0b0010111 -> command = "auipc";
        }
        return String.format("%7s \t %s, %d", command, rd, imm);
    }

    private String jType() {
        int offset = Functions.slice(value, 31, 31);
        offset = (offset << 8) + Functions.slice(value, 19, 12);
        offset = (offset << 1) + Functions.slice(value, 20, 20);
        offset = (offset << 10) + Functions.slice(value, 30, 21);
        offset = Functions.getValue((offset << 1), 20);
        int opcode = getOpcode();
        String rd = Functions.getReg(Functions.slice(value, 11, 7));
        command = "jal";
        markIndex = offset + addr;
        return String.format("%7s \t %s, %x", command, rd, (offset + addr));
    }

    private String csrType() {
        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
        String rd = Functions.getReg(Functions.slice(value, 11, 7));
        int csr = Functions.getValue(Functions.slice(value, 31, 27), 31 - 27);
        switch (Functions.slice(value, 14, 12)) {
            case 0b001 -> command = "csrrw";
            case 0b010 -> command = "csrrs";
            case 0b011 -> command = "csrrc";
            case 0b101 -> command = "csrrwi";
            case 0b110 -> command = "csrrsi";
            case 0b111 -> command = "csrrci";
        }
        return String.format("%7s \t %s, %s, %s", rd, csr, rs1);
    }

    private String argsFence(int value) {
        String ans = "";
        if (Functions.slice(value, 3, 3) == 1) {
            ans += 'i';
        }
        if (Functions.slice(value, 2, 2) == 1) {
            ans += 'o';
        }
        if (Functions.slice(value, 1, 1) == 1) {
            ans += 'r';
        }
        if (Functions.slice(value, 0, 0) == 1) {
            ans += 'w';
        }
        return ans;
    }

    private String fence() {
        switch (Functions.slice(value, 14, 12)) {
            case 0b000 -> {
                String pred = argsFence(Functions.slice(value, 27, 24));
                String succ = argsFence(Functions.slice(value, 23, 20));
                command = "fence";
                return String.format("%7s \t %s, %s", command, pred, succ);
            }
            case 0b001 -> {
                command = "fence.i";
                return String.format("%7s", command);
            }
        }
        return "";
    }

    private String other() {
        if (Functions.slice(value, 14, 12) != 0b000) {
            return csrType();
        }
        switch (Functions.slice(value, 31, 27)) {
            case 0b00000 -> {
                switch (Functions.slice(value, 24, 20)) {
                    case 0b00000 -> command = "ecall";
                    case 0b00001 -> command = "ebreak";
                    case 0b00010 -> command = "uret";
                }
            }
            case 0b00110 -> command = "mret";
            case 0b00010 -> {
                switch (Functions.slice(value, 26, 25)) {
                    case 0b00 -> {
                        switch (Functions.slice(value, 24, 20)) {
                            case 0b00010 -> command = "sret";
                            case 0b00101 -> command = "wfi";
                        }
                    }
                    case 0b01 -> {
                        String rs2 = Functions.getReg(Functions.slice(value, 24, 20));
                        String rs1 = Functions.getReg(Functions.slice(value, 19, 15));
                        command = "sfence.vma";
                        return String.format("%7s \t %s, %s", command, rs1, rs2);
                    }
                }
            }
        }
        return String.format("%7s", command);
    }

    public String parseOpcode() {
        switch (getOpcode()) {
            case 0b0110111, 0b0010111 -> {
                return uType();
            }
            case 0b1100111, 0b0000011, 0b0010011 -> {
                return iType();
            }
            case 0b0100011 -> {
                return sType();
            }
            case 0b0110011 -> {
                return rType();
            }
            case 0b1101111 -> {
                return jType();
            }
            case 0b1100011 -> {
                return bType();
            }
            case 0b0001111 -> {
                return fence();
            }
            case 0b1110011 -> {
                return other();
            }
        }
        return "unknown_instruction";
    }

    @Override
    public String toString() {
        String out = String.format("   %05x: \t %08x \t", addr, value) + parseOpcode();
        if (command.equals("unknown_instruction")) {
            out = "unknown_instruction";
        }
        if (hasMark()) {
            out += " " + getMark();
        }
        return out;
    }
}
