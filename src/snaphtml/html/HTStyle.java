package snaphtml.html;
import snap.util.*;

/**
 * A HTElement subclass to handle <style> element.
 */
public class HTStyle extends HTElement {

/**
 * Reads HTML.
 */
public void readHTML(XMLElement aXML, HTDoc aDoc)
{
    HTText text = new HTText(); text.setText("<Style>");
    addChild(text);
}

}