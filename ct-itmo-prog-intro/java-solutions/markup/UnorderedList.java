package markup;

import java.util.List;

public class UnorderedList extends AbstractHtmlList {
    public UnorderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    public String getHtmlTag() {
        return "ul";
    }
}
