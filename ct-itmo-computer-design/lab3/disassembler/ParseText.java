package disassembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseText {
    private final List<RiscV> commands;
    private final Map<Integer, String> marks;

    public ParseText(BinaryFile input, Section text, Symtab symtab) {
        marks = new HashMap<>();
        commands = new ArrayList<>();
        int sh_addr = text.getSh_addr();
        int index = text.getSh_offset();
        for (int i = 0; i < text.getSh_size(); i += 4, index += 4, sh_addr += 4) {
            RiscV r = new RiscV(Functions.bytesToInt(input.getBytes(index, 4)), sh_addr, symtab);
            commands.add(r);
            if (r.hasMark()) {
                marks.put(r.getMarkIndex(), r.getMark());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(".text");
        out.append(System.lineSeparator());
        for (int i = 0; i < commands.size(); i++) {
            RiscV command = commands.get(i);
            if (marks.containsKey(command.getAddr())) {
                out.append(String.format("%08x   %s:", command.getAddr(), marks.get(command.getAddr())));
                out.append(System.lineSeparator());
            }
            out.append(command);
            out.append(System.lineSeparator());
        }
        return out.toString();
    }

}
