package markup;

import java.util.List;

public class ListItem extends AbstractHtmlText{
    List<AbstractHtmlList> list;
    public ListItem(List<AbstractHtmlList> list) {
        this.list = list;
    }

    @Override
    public String getHtmlTag() {
        return "li";
    }

    @Override
    public void toHtml(StringBuilder elements) {
        elements.append(getStartTag());
        for (AbstractHtmlList element : list) {
            element.toHtml(elements);
        }
        elements.append(getEndTag());
    }
}
