package md2html;

import java.io.IOException;

public class Md2Html {
    public static void main(String[] args) {
        try {
            MarkdownParser p = new MarkdownParser(args[0]);
            p.toHtml(args[1]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
