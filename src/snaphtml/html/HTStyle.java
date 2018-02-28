package snaphtml.html;
import java.util.Map;
import org.jsoup.nodes.Element;

/**
 * A HTElement subclass to handle <style> element.
 */
public class HTStyle extends HTElement {
    
    // The CSSStyles object
    CSSStyles      _styles;

/**
 * Returns the CSSStyles.
 */
public CSSStyles getStyles()  { return _styles; }

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    String text = aJSoup.data();
    Map <String,Map<String,String>> stylesMap = new CSSParser().parseCSS(text);
    _styles = new CSSStyles(stylesMap);
}

}