package snaphtml.html;
import snap.util.XMLElement;
import snap.view.View;

/**
 * A HTElement subclass for HTML list item.
 */
public class HTListItem extends HTElement {


/**
 * Reads HTML.
 */
public void readHTML(XMLElement aXML, HTDoc aDoc)
{
    // Do normal version
    super.readHTML(aXML, aDoc);
    
    // Add bullet
    for(View child : getChildren()) {
        if(child instanceof HTText) { HTText text = (HTText)child;
            text.setText("\u2022 " + text.getText()); return; }
    }
}

}