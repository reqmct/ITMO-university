package markup;

import java.util.List;

public abstract class AbstractHtmlList extends AbstractHtmlText {
    private List<ListItem> list;

    public AbstractHtmlList() {

    }
    public AbstractHtmlList(List<ListItem> list) {
        this.list = list;
    }


    @Override
    public void toHtml(StringBuilder elements) {
        elements.append(getStartTag());
        for (ListItem element : list) {
            element.toHtml(elements);
        }
        elements.append(getEndTag());
    }
}
