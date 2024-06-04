package disassembler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Parser {
    public static void parse(String inputFile, String outputFile) {
        try {
            BinaryFile elfFile = new BinaryFile(inputFile);
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile),
                        "UTF8"
                ));
                try {
                    ELFHeader elfHeader = new ELFHeader(elfFile);
                    SectionHeader sectionHeader = new SectionHeader(elfFile, elfHeader);
                    Symtab symtab = new Symtab(elfFile, sectionHeader);
                    ParseText text = new ParseText(elfFile, sectionHeader.getSectionByName(".text"), symtab);
                    writer.write(text.toString());
                    writer.newLine();
                    writer.write(symtab.toString());
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Output file exception:" + e.getMessage());
            } finally {
                elfFile.close();
            }
        } catch (IOException e) {
            System.out.println("Input file exception:" + e.getMessage());
        }
    }
}
