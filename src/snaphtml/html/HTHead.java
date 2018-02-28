package snaphtml.html;
import snap.view.View;

/**
 * A HTElement for HTML <head> tag.
 */
public class HTHead extends HTElement {

/**
 * Create HTHead.
 */
public HTHead()
{
    setVisible(false);
    setPrefHeight(2);
    setGrowWidth(true);
}

/**
 * Returns the CSSStyles.
 */
public CSSStyles getStyles()
{
    HTStyle style = getStyleView();
    return style!=null? style.getStyles() : null;
}

/**
 * Returns the HTStyle.
 */
public HTStyle getStyleView()
{
    for(View child : getChildren())
        if(child instanceof HTStyle)
            return (HTStyle)child;
    return null;
}

}