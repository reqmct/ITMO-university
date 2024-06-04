package disassembler;

import java.util.ArrayList;
import java.util.List;

public class SectionHeader {
    BinaryFile input;
    private final int sectionSize;
    private final int number;
    private final int stingTableIndex;
    private List<Section> sections;
    private final ParseString stringTable;


    public SectionHeader(BinaryFile input, ELFHeader elfHeader) {
        this.input = input;
        int start = elfHeader.getE_shoff();
        this.sectionSize = elfHeader.getE_shentsize();
        this.number = elfHeader.getE_shnum();
        this.stingTableIndex = elfHeader.getE_shstrndx();
        sections = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            int startSection = start + i * sectionSize;
            sections.add(new Section(input.getBytes(startSection, sectionSize)));
        }
        stringTable = getParseStringByInd(stingTableIndex);
    }


    public Section getSectionByName(String name) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            if (stringTable.bytesToString(section.getSh_name()).equals(name)) {
                return section;
            }
        }
        throw new IllegalArgumentException("There is no " + name);
    }

    public String getSectionName(int index) {
        return stringTable.bytesToString(getSection(index).getSh_name());
    }

    public ParseString getStringTable() {
        return stringTable;
    }

    public ParseString getParseStringByInd(int index) {
        Section section = sections.get(index);
        return new ParseString(input.getBytes(section.getSh_offset(),
                section.getSh_size()));
    }

    public Section getSection(int index) {
        try {
            return sections.get(index);
        } catch (IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Section " + index + "not found");
        }
    }


}
