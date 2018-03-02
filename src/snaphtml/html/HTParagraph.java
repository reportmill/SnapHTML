package snaphtml.html;

/**
 * A HTElement subclass for HTML paragraph tag.
 */
public class HTParagraph extends HTElement {

/**
 * Returns the inline style for this element.
 */
public CSSStyle getStyleDefault()
{
    CSSStyle cs = super.getStyleDefault();
    cs.setProperty("margin-top", "1em");
    cs.setProperty("margin-bottom", "1em");
    return cs;
}

}