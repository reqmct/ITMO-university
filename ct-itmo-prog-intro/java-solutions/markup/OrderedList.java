package markup;

import java.util.List;

public class OrderedList extends AbstractHtmlList{
    public OrderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    public String getHtmlTag() {
        return "ol";
    }
}
