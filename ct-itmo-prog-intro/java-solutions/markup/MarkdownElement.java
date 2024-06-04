package markup;

import java.util.List;

public abstract class MarkdownElement implements Markdown {
    private List<MarkdownElement> list;

    public MarkdownElement(List<MarkdownElement> list) {
        this.list = list;
    }

    public MarkdownElement() {
    }

    @Override
    public void toMarkdown(StringBuilder elements) {
        elements.append(getTag());
        for (Markdown element : list) {
            element.toMarkdown(elements);
        }
        elements.append(getTag());
    }
}
