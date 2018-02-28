package snaphtml.html;
import java.util.*;

/**
 * A class to parse CSS.
 */
public class CSSParser extends Object {

/**
 * Parses a given string.
 */
public Map <String,Map<String,String>> parseCSS(String aStr)
{
    // Create map of maps
    Map <String,Map<String,String>> css = new HashMap();
    
    // Iterate over string    
    int start = 0, open = aStr.indexOf("{");
    while(open>0) {
        
        // Parse selectors
        String selsStr = aStr.substring(start, open).trim();
        String sels[] = parseSelectors(selsStr);
        
        // Find next close
        int close = aStr.indexOf("}", open+1); if(close<open) break;
        
        // Get style contents
        String style = aStr.substring(open+1, close).trim();
        Map <String,String> styles = parseStyle(style);
        
        // Add contents to selectors
        for(String sel : sels) {
            Map <String,String> selMap = css.get(sel);
            if(selMap==null) css.put(sel, selMap = new HashMap());
            selMap.putAll(styles);
        }
        
        // Update start, open
        start = close + 1; open = aStr.indexOf("{", start);
    }
    
    // Return CSS map
    return css;
}

/**
 * Parses a string to return selectors.
 */
String[] parseSelectors(String aStr)
{
    String sels[] = aStr.split(",");
    for(int i=0;i<sels.length;i++) sels[i] = sels[i].trim();
    return sels;
}

/**
 * Parses a style.
 */
public Map <String,String> parseStyle(String aStr)
{
    // Split style string into entries
    String entries[] = aStr.split(";");
    Map <String,String> styles = new HashMap();
    
    // Iterate over entries to add to styles map
    for(String entry : entries) {
        String parts[] = entry.split(":");
        if(parts.length==2)
            styles.put(parts[0].trim(), parts[1].trim());
        else System.out.println("CSSParser: bogus style entry: " + entry);
    }
    
    // Return styles
    return styles;
}

}