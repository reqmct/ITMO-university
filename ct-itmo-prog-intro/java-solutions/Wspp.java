import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Wspp {
    public static boolean isCorrectCharacter(char symbol) {
        return Character.isLetter(symbol) || Character.getType(symbol) == 0x14 || symbol == '\'';         
    }

    public static void main(String[] args) {
        List<String> allWords = new ArrayList<>(); 
        StringBuilder word = new StringBuilder();
        Map<String, IntList> counterWord = new HashMap<>();
        try {
            MyScanner reader = new MyScanner(new File(args[0]), "UTF8");
            try {
                int index = 1;
                while(reader.hasNext()) {
                    String symbols = reader.next();
                    for(int i = 0; i < symbols.length(); i++) { 
                        char read = symbols.charAt(i);
                        if(isCorrectCharacter(read)) {
                            word.append(read);
                        } 
                        if(i == symbols.length() - 1 || !isCorrectCharacter(read)) {
                            if(!word.isEmpty()) {
                                String addWord = word.toString().toLowerCase();
                                IntList addIndex = counterWord.get(addWord);
                                if(addIndex == null) {
                                    addIndex = new IntList();
                                    allWords.add(addWord);
                                }
                                addIndex.add(index);
                                counterWord.put(addWord, addIndex);
                                index++;
                            }
                            word = new StringBuilder();
                        }
                    }
                }
            } finally {
                reader.close();
            } 
        } catch(IOException e) {
            System.out.println("Input file error:" + e.getMessage());
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]), 
                    "UTF8"
            ));
            try {
                for(int i = 0; i < allWords.size(); i++) {
                    IntList outputIndex = counterWord.get(allWords.get(i));
                    writer.write(allWords.get(i) + " " + outputIndex.size() + " " + outputIndex);
                    writer.newLine();
                }
            } finally {
                writer.close();
            }
        } catch(IOException e) {
            System.out.println("Input file error:" + e.getMessage());
        }
    }
}