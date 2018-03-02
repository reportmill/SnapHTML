package snaphtml.html;
import org.jsoup.nodes.Element;

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
    
    // Add bullet
    HTText text = new HTText(); text.setText("\u2022 ");
    addChild(text, 0);
    
    // Add bullet
    //for(View child : getChildren()) { if(child instanceof HTText) { HTText text = (HTText)child;
    //        text.setText("\u2022 " + text.getText()); return; }  }
}

}