import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class WordStatInput {
    public static boolean isCorrectCharacter(char symbol) {
        return Character.isLetter(symbol) || Character.getType(symbol) == 0x14 || symbol == '\'';
    }

    public static void main(String[] args) {
        try {
            MyScanner reader = new MyScanner(new File(args[0]), "UTF8");
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(args[1]),
                        "UTF8"
                ));
                try {
                    List<String> allWords = new ArrayList<>();
                    StringBuilder word = new StringBuilder();
                    Map<String, Integer> counterWord = new HashMap<>();
                    while (reader.hasNext()) {
                        String symbols = reader.next();
                        for (int i = 0; i < symbols.length(); i++) {
                            char c = symbols.charAt(i);
                            if (isCorrectCharacter(c)) {
                                word.append(c);
                            }
                            if (i == symbols.length() - 1 || !isCorrectCharacter(c)) {
                                if (!word.isEmpty()) {
                                    String addWord = word.toString().toLowerCase();
                                    if (!counterWord.containsKey(addWord)) {
                                        counterWord.put(addWord, 1);
                                        allWords.add(addWord);
                                    } else {
                                        counterWord.put(addWord, counterWord.get(addWord) + 1);
                                    }
                                }
                                word = new StringBuilder();
                            }
                        }
                    }
                    for (int i = 0; i < allWords.size(); i++) {
                        writer.write(allWords.get(i) + " " + counterWord.get(allWords.get(i)));
                        writer.newLine();
                    }
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Output file error:" + e.getMessage());
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Input file error:" + e.getMessage());
        }
    }
}