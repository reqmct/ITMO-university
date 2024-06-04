package disassembler;

import java.util.*;

public class Symtab {
    private final int[] bytes;
    private final Section symtabSection;
    private final int lineSize = 16;
    private final List<SymtabLine> lines;
    private final Map<Integer, String> convertFunctionName;
    private int functionNameIndex = 0;

    private final ParseString names;

    public Symtab(BinaryFile input, SectionHeader sectionHeader) {
        symtabSection = sectionHeader.getSectionByName(".symtab");
        bytes = input.getBytes(symtabSection.getSh_offset(), symtabSection.getSh_size());
        lines = new ArrayList<>();
        convertFunctionName = new HashMap<>();
        names = sectionHeader.getParseStringByInd(symtabSection.getSh_link());
        for (int i = 0; i < bytes.length; i += lineSize) {
            lines.add(new SymtabLine(Arrays.copyOfRange(bytes, i, i + lineSize), sectionHeader, names, i / lineSize));
        }
    }

    public String getFunctionNameByValue(int value) {
        for (int i = 0; i < lines.size(); i++) {
            SymtabLine line = lines.get(i);
            if (line.getValue() == value && line.getType().equals("FUNC")) {
                return line.getName();
            }
        }
        if (!convertFunctionName.containsKey(value)) {
            convertFunctionName.put(value, "L" + functionNameIndex);
            functionNameIndex++;
        }
        return convertFunctionName.get(value);
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(".symtab");
        out.append(System.lineSeparator());
        out.append("Symbol Value              Size Type     Bind     Vis       Index Name\n");
        for (int i = 0; i < lines.size(); i++) {
            out.append(lines.get(i));
            out.append(System.lineSeparator());
        }
        return out.toString();
    }
}
