package markup;

import java.util.List;

public class Strikeout extends AbstractHtmlElement{
    public Strikeout(List<AbstractHtmlElement> list) {
        super(list);
    }

    @Override
    public String getHtmlTag() {
        return "s";
    }
}
