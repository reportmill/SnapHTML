package snaphtml.html;
import java.util.Map;
import snap.util.StringUtils;

/**
 * A class to manage the CSS style for an element.
 */
public class CSSStyle {
    
    // The Element
    HTElement    _emt;
    
    // The map holding the actual style entries
    Map <String,String> _styles;
    
    
/**
 * Creates a CSSStyle for given map.
 */
public CSSStyle(Map<String,String> aMap)
{
    _styles = aMap;
}

/**
 * Applies the styles to given element.
 */
public void applyStyles()
{
    for(String key : _styles.keySet())
        applyStyle(key, _styles.get(key));
}

/**
 * Applies the style to given element.
 */
public void applyStyle(String key, String val)
{
    switch(key) {
        case "font-size": applyFontSize(val);
    }
}

/**
 * Applies the font size.
 */
public void applyFontSize(String aVal)
{
    double size = StringUtils.doubleValue(aVal);
    _emt.setFont(_emt.getFont().deriveFont(size));
}


}