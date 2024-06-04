package markup;

import java.util.List;

public class Paragraph extends AbstractHtmlList {
    List<AbstractHtmlElement> list;
    public Paragraph(List<AbstractHtmlElement> list) {
        this.list = list;
    }

    @Override
    public String getHtmlTag() {
        return "";
    }

    @Override
    public void toHtml(StringBuilder elements) {
        for (AbstractHtmlElement element : list) {
            element.toHtml(elements);
        }
    }
}
