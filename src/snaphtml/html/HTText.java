package snaphtml.html;
import snap.gfx.*;
import snap.view.*;

/**
 * A HTElement subclass for HTML text.
 */
public class HTText extends HTElement {

/**
 * Creates a HTText.
 */
public HTText()
{
    TextArea tarea = new TextArea();
    addChild(tarea);
}

/**
 * Returns the text.
 */
public String getText()
{
    TextArea tview = getTextArea();
    return tview!=null? tview.getText() : null;
}

/**
 * Sets the text.
 */
public void setText(String aStr)
{
    TextArea tview = getTextArea();
    if(tview!=null) tview.setText(aStr);
}

/**
 * Returns the text fill.
 */
public Paint getTextFill()
{
    TextArea tview = getTextArea();
    return tview!=null? tview.getTextFill() : null;
}

/**
 * Sets the text fill.
 */
public void setTextFill(Paint aFill)
{
    TextArea tview = getTextArea();
    if(tview!=null) tview.setTextFill(aFill);
}

/**
 * Returns whether text underlined.
 */
public boolean isTextUnderlined()
{
    TextArea tview = getTextArea();
    return tview!=null? tview.isUnderlined() : null;
}

/**
 * Sets whether text underlined.
 */
public void setTextUnderlined(boolean aValue)
{
    TextArea tview = getTextArea();
    if(tview!=null) tview.setUnderlined(aValue);
}

/**
 * Override to propagate to TextArea.
 */
public void setFont(Font aFont)
{
    super.setFont(aFont);
    getTextArea().setFont(aFont);
}

/**
 * Returns the text view.
 */
public TextArea getTextArea()  { return getChildCount()>0? (TextArea)getChild(0) : null; }

}