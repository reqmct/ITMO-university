package markup;

import java.util.List;

public class Emphasis extends AbstractHtmlElement{
    public Emphasis(List<AbstractHtmlElement> list) {
        super(list);
    }

    @Override
    public String getHtmlTag() {
        return "em";
    }
}
