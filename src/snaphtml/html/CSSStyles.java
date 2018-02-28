package snaphtml.html;
import java.util.*;

/**
 * A class to manage the CSS styles for an document.
 */
public class CSSStyles {
    
    // The map of selector to styles map
    Map <String,Map<String,String>>  _styles;

/**
 * Creates a CSSStyles for given Map of selector to style map.
 */
public CSSStyles(Map <String,Map<String,String>> aMap)
{
    _styles = aMap;
}

/**
 * Returns the CSSStyle for given HTElement.
 */
public CSSStyle getStyle(HTElement anEmt)
{
    CSSStyle style = anEmt.getStyle();
    Map <String,String> smap = new HashMap();
    
    // Add styles for tag
    String tag = anEmt.getTagName();
    Map <String,String> tmap = _styles.get(tag);
    if(tmap!=null) smap.putAll(tmap);
    
    // Add styles for id
    String id = anEmt.getId();
    if(id!=null) {
        Map <String,String> imap = _styles.get('#' + id);
        if(imap!=null) smap.putAll(imap);
    }
    
    // Add styles for classes
    String classes[] = anEmt.getClasses();
    for(String cls : classes) {
        Map <String,String> cmap = _styles.get('.' + cls);
        if(cmap!=null) smap.putAll(cmap);
    }
    
    // Add styles from inline
    smap.putAll(style._styles);
    style._styles = smap;
    return style;
}

}