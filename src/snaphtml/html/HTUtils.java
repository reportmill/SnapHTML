package snaphtml.html;
import java.util.Arrays;

/**
 * HTML Utility methods.
 */
public class HTUtils {

    // Empty tags
    static String EMPTY_TAGS[] = { "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta",
        "param", "source", "track", "wbr"};
    
/**
 * Returns whether tag is empty.
 */
public static boolean isEmptyTag(String aName)
{
    String name = aName.toLowerCase();
    return Arrays.binarySearch(EMPTY_TAGS, name)>=0;
}

/**
 * Returns a full name for tag.
 */
public static String getTagFullName(String aTag)
{
    switch(aTag) {
        case "a": return "Anchor";
        case "br": return "Line Break";
        case "div": return "Division";
        case "hr": return "Horizontal Rule";
        case "img": return "Image";
        case "li": return "List Item";
        case "ol": return "Ordered List";
        case "p": return "Paragraph";
        case "table": return "Table";
        case "td": return "Table Data";
        case "tr": return "Table Row";
        case "ul": return "Unordered List";
        default: return aTag;
    }
}


}