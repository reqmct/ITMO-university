package markup;

public class Text extends AbstractHtmlElement {
    public Text(String text) {
        super(text);
    }

    @Override
    public void toHtml(StringBuilder elements) {
        elements.append(text);
    }

    @Override
    public String getHtmlTag() {
        return "";
    }
}
