package snaphtml.html;
import org.jsoup.nodes.Element;
import snap.view.TextArea;

/**
 * A HTElement subclass for HTML list item.
 */
public class HTListItem extends HTElement {

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Do normal version
    super.readHTML(aJSoup, aDoc);
    
    // Add bullet text
    TextArea text = new TextArea(); text.setText("\u2022 ");
    addChild(text, 0);
}

}