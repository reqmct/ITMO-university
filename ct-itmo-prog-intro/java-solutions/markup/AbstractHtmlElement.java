package markup;

import java.util.List;

public abstract class AbstractHtmlElement extends AbstractHtmlText {
    private List<AbstractHtmlElement> list;
    protected String text;

    public AbstractHtmlElement(String text) {
        this.text = text;
    }

    public AbstractHtmlElement(List<AbstractHtmlElement> list) {
        this.list = list;
    }

    @Override
    public void toHtml(StringBuilder elements) {
        elements.append(getStartTag());
        for (AbstractHtmlElement element : list) {
            element.toHtml(elements);
        }
        elements.append(getEndTag());
    }
}
