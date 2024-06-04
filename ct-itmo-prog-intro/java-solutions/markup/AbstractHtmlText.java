package markup;

import java.util.List;
import java.util.Objects;

public abstract class AbstractHtmlText implements Html {

    protected String getStartTag() {
        return "<" + getHtmlTag() + ">";
    }

    protected String getEndTag() {
        return "</" + getHtmlTag() + ">";
    }
}
