package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MarkdownParser {
    private final MyScanner in;
    private final Map<String, String> convertTags = Map.of("*", "em", "_", "em", "`",
            "code", "**", "strong", "__", "strong", "--", "s", "![", "");
    private final Map<String, String> specialCharacter = Map.of("<", "&lt;", ">", "&gt;",
            "&", "&amp;");
    private int start;

    public MarkdownParser(String inputFileName) throws IOException {
        in = new MyScanner(new File(inputFileName), "UTF8");
        start = 0;
    }

    private StringBuilder getParagraph() {
        StringBuilder lines = new StringBuilder();
        String line = "";
        while (in.hasNextLine() && line.isEmpty()) {
            line = in.nextLine();
        }
        if (!line.isEmpty()) {
            lines.append(line);
        }
        while (in.hasNextLine()) {
            line = in.nextLine();
            if (line.isEmpty()) {
                break;
            }
            lines.append(System.lineSeparator());
            lines.append(line);
        }
        return lines;
    }

    private String getStartTag(String htmlTag) {
        if (htmlTag.isEmpty())
            return "";
        return "<" + htmlTag + ">";
    }

    private String getEndTag(String htmlTag) {
        if (htmlTag.isEmpty())
            return "";
        return "</" + htmlTag + ">";
    }

    private boolean isMarkdownTag(String tag) {
        return convertTags.containsKey(tag);
    }

    private StringBuilder parseImage(StringBuilder paragraph) {
        StringBuilder alt = new StringBuilder();
        StringBuilder src = new StringBuilder();
        int index;
        for (index = 0; index < paragraph.length(); index++) {
            String imgTag = String.valueOf(paragraph.charAt(index)) + paragraph.charAt(index + 1);
            if (imgTag.equals("](")) {
                index += 2;
                break;
            }
            alt.append(paragraph.charAt(index));
        }
        for (; index < paragraph.length(); index++) {
            src.append(paragraph.charAt(index));
        }
        StringBuilder img = new StringBuilder();
        img.append("<img alt='");
        img.append(alt);
        img.append("' src='");
        img.append(src);
        img.append("'>");
        return img;
    }

    private boolean isSpace(char c) {
        return Character.isWhitespace(c);
    }

    private String getHtmlTag(String tag) {
        return convertTags.get(tag);
    }

    private String getSpecialCharacter(String tag) {
        return specialCharacter.getOrDefault(tag, tag);
    }

    private StringBuilder parseParagraph(StringBuilder paragraph, String findTag) {
        int len = paragraph.length();
        int index;
        StringBuilder htmlParagraph = new StringBuilder();
        for (index = start; index < len; index++) {
            String maybeMarkdownTag = String.valueOf(paragraph.charAt(index));
            if (paragraph.charAt(index) == '\\') {
                maybeMarkdownTag = "";
                if (index < len - 1) {
                    index += 1;
                    maybeMarkdownTag += paragraph.charAt(index);
                    htmlParagraph.append(maybeMarkdownTag);
                }
                continue;
            }
            if (index < len - 1) {
                if (isMarkdownTag(maybeMarkdownTag + paragraph.charAt(index + 1))) {
                    maybeMarkdownTag += paragraph.charAt(index + 1);
                    index++;
                }
            }
            if (isMarkdownTag(maybeMarkdownTag) && !(findTag.equals("![") || findTag.equals("]("))) {
                String htmlTag = getHtmlTag(maybeMarkdownTag);
                if (maybeMarkdownTag.equals(findTag)) {
                    start = index;
                    htmlParagraph.append(getEndTag(htmlTag));
                    return htmlParagraph;
                } else {
                    start = index + 1;
                    StringBuilder add = parseParagraph(paragraph, maybeMarkdownTag);
                    index = start;
                    if (start == -1) {
                        htmlParagraph.append(maybeMarkdownTag);
                        htmlParagraph.append(add);
                        return htmlParagraph;
                    } else {
                        htmlParagraph.append(getStartTag(htmlTag));
                        htmlParagraph.append(add);
                    }
                    continue;
                }
            }
            if (findTag.equals("![") && index < len - 1) {
                String imgTag = String.valueOf(paragraph.charAt(index)) + paragraph.charAt(index + 1);
                if (imgTag.equals("](")) {
                    findTag = "](";
                }
            }
            if (findTag.equals("](")) {
                String imgTag = String.valueOf(paragraph.charAt(index));
                if (imgTag.equals(")")) {
                    start = index;
                    return parseImage(htmlParagraph);
                }
            }
            maybeMarkdownTag = getSpecialCharacter(maybeMarkdownTag);
            htmlParagraph.append(maybeMarkdownTag);
        }
        start = -1;
        return htmlParagraph;
    }

    private StringBuilder getHtmlParagraph(StringBuilder paragraph) {
        StringBuilder htmlParagraph;
        String paragraphTag = "p";
        int len = paragraph.length();
        int countOctothorpe = 0;
        start = 0;
        while (start < len) {
            if (paragraph.charAt(start) != '#') {
                if (isSpace(paragraph.charAt(start))) {
                    start++;
                } else {
                    countOctothorpe = 0;
                }
                break;
            }
            countOctothorpe += 1;
            start++;
        }
        if (countOctothorpe != 0) {
            paragraphTag = "h" + countOctothorpe;
        } else {
            start = 0;
        }
        htmlParagraph = parseParagraph(paragraph, "");
        htmlParagraph.insert(0, getStartTag(paragraphTag));
        htmlParagraph.append(getEndTag(paragraphTag));
        htmlParagraph.append(System.lineSeparator());
        return htmlParagraph;
    }

    public void toHtml(String outputFileName) {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFileName),
                StandardCharsets.UTF_8
        ))) {
            StringBuilder paragraph = getParagraph();
            while (!paragraph.isEmpty()) {
                StringBuilder htmlParagraph = getHtmlParagraph(paragraph);
                out.write(htmlParagraph.toString());
                paragraph = getParagraph();
            }
        } catch (IOException e) {
            System.out.println("Output file error:" + e.getMessage());
        }
        try {
            in.close();
        } catch (IOException e) {
            System.out.println("Input file error" + e.getMessage());
        }
    }
}
