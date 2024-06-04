package markup;

import java.util.List;

public class Strong extends AbstractHtmlElement{
    public Strong(List<AbstractHtmlElement> list) {
        super(list);
    }

    @Override
    public String getHtmlTag() {
        return "strong";
    }
}
