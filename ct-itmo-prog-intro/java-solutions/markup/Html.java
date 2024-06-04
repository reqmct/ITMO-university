package markup;

public interface Html {
    String getHtmlTag();

    void toHtml(StringBuilder elements);
}
