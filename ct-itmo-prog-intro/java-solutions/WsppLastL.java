import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class WsppLastL {
    public static boolean isCorrectCharacter(char symbol) {
        return Character.isLetter(symbol) || Character.getType(symbol) == 0x14 || symbol == '\'';
    }

    public static void main(String[] args) {
        List<String> allWords = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        Map<String, Integer> counterWord = new HashMap<>();
        Map<String, IntList> lastWordIndex = new HashMap<>();
        try {
            try (MyScanner in = new MyScanner(new File(args[0]), "UTF8")) {
                while (in.hasNextLine()) {
                    Map<String, Integer> lastIndexOfWordInLine = new HashMap<>();
                    List<String> lineWords = new ArrayList<>();
                    int lineIndex = 1;
                    while (!in.isEndOfLine()) {
                        String symbols = in.next();
                        for (int i = 0; i < symbols.length(); i++) {
                            char read = symbols.charAt(i);
                            if (isCorrectCharacter(read)) {
                                word.append(read);
                            }
                            if (i == symbols.length() - 1 || !isCorrectCharacter(read)) {
                                if (!word.isEmpty()) {
                                    String addWord = word.toString().toLowerCase();
                                    //Integer tryGetCounterWord = counterWord.get(addWord);
                                    if (!lastIndexOfWordInLine.containsKey(addWord)) {
                                        lineWords.add(addWord);
                                    }
                                    counterWord.put(addWord, counterWord.getOrDefault(addWord, 0) + 1);
                                    //if (tryGetCounterWord == null) {
                                    //    counterWord.put(addWord, 1);
                                    //} else {
                                    //    counterWord.put(addWord, tryGetCounterWord + 1);
                                    //}
                                    lastIndexOfWordInLine.put(addWord, lineIndex);
                                    lineIndex++;
                                }
                                word = new StringBuilder();
                            }
                        }
                    }
                    for (String nextWord : lineWords) {
                        IntList addIndex = lastWordIndex.get(nextWord);
                        if (addIndex == null) {
                            addIndex = new IntList();
                            allWords.add(nextWord);
                        }
                        addIndex.add(lastIndexOfWordInLine.get(nextWord));
                        lastWordIndex.put(nextWord, addIndex);
                    }
                    in.goToNextLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Input file error:" + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(args[1]),
            StandardCharsets.UTF_8
        ))) {
            for (String allWord : allWords) {
                IntList outputIndex = lastWordIndex.get(allWord);
                writer.write(allWord + " " + counterWord.get(allWord) + " " + outputIndex);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Input file error:" + e.getMessage());
        }
    }
}