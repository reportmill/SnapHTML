package snaphtml.html;
import org.jsoup.nodes.Element;
import snap.gfx.*;
import snap.view.View;

/**
 * A HTElement subclass for HTML table data.
 */
public class HTTableData extends HTElement {

/**
 * Creates a new HTMLTableData.
 */
public HTTableData()
{
    //setBorder(Color.PINK.brighter().brighter(),1);
    setGrowWidth(true);
}

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Do normal version
    super.readHTML(aJSoup, aDoc);
    
    // Handle align
    if(aJSoup.attributes().hasKey("align")) {
        String str = aJSoup.attributes().get("align").toUpperCase();
        HPos align = HPos.valueOf(str);
        setAlign(align);
        for(View child : getChildren()) child.setAlign(align);
    }
}

}