package snaphtml.html;
import org.jsoup.nodes.Element;
import snap.gfx.Color;
import snap.view.*;
import snap.web.WebURL;

/**
 * A HTElement subclass for HTML link tag.
 */
public class HTLink extends HTElement {
    
    // The HRef
    String           _href;

/**
 * Creates a new HTMLLink.
 */
public HTLink()
{
    enableEvents(MouseRelease);
    setCursor(Cursor.HAND);
}

/**
 * Handle event.
 */
protected void processEvent(ViewEvent anEvent)
{
    if(anEvent.isMouseRelease()) {
        HTDoc doc = getDoc();
        View viewerUI = doc.getParent();
        HTViewer viewer = (HTViewer)viewerUI.getOwner();
        WebURL url = doc.getSourceURL(getHRef());
        HTDoc doc2 = HTDoc.getDoc(url);
        viewer.setDoc(doc2);
    }
}

/**
 * Returns the HRef.
 */
public String getHRef()  { return _href; }

/**
 * Sets the HRef.
 */
public void setHRef(String aValue)  { _href = aValue; }

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Do normal version
    super.readHTML(aJSoup, aDoc);
    
    // Set HRef
    String href = aJSoup.attributes().get("href");
    setHRef(href);
    
    // Set all child text to red
    for(View child : getChildren()) {
        child.setPickable(false);
        if(child instanceof HTText) { HTText text = (HTText)child;
            text.setTextFill(Color.RED);
            text.setTextUnderlined(true);
        }
    }
}

}