package snaphtml.html;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.view.*;
import org.jsoup.nodes.*;

/**
 * A view to represent an HTML Element.
 */
public class HTElement extends BoxView {
    
    // The JSoup Element
    Element     _jsoup;

/**
 * Returns the doc.
 */
public HTDoc getDoc()  { return getParent(HTDoc.class); }

/**
 * Returns the soup.
 */
public Element getSoup()  { return _jsoup; }

/**
 * Returns the tag name.
 */
public String getTagName()
{
    return _jsoup!=null? _jsoup.tagName() : getClass().getSimpleName().substring(2).toLowerCase();
}

/**
 * Returns the tag name, uppercase.
 */
public String getTagNameUC()  { return getTagName().toUpperCase(); }

/**
 * Returns the id.
 */
public String getId()  { return _jsoup.attributes().get("id"); }

/**
 * Returns the class.
 */
public String[] getClasses()
{
    String cls = _jsoup.attributes().get("class"); if(cls==null || cls.length()==0) return new String[0];
    String classes[] = cls.split("\\s+");
    return classes;
}

/**
 * Returns the inline style for this element.
 */
public CSSStyle getStyleDefault()  { CSSStyle cs = new CSSStyle(); cs._emt = this; return cs; }

/**
 * Returns the inline style for this element.
 */
public CSSStyle getStyle()
{
    String style = _jsoup.attributes().get("style"); if(style==null || style.length()==0) return null;
    Map <String,String> styles = new CSSParser().parseStyle(style);
    CSSStyle cs = new CSSStyle(); cs._emt = this; cs.addAll(styles);
    return cs;
}

/**
 * Returns the styles for this element in this doc.
 */
public CSSStyle getStyleAll()
{
    CSSStyles styles = getDoc().getStyles();
    if(styles!=null)
        return styles.getStyle(this);
        
    CSSStyle cs = getStyleDefault();
    CSSStyle cs2 = getStyle(); if(cs2!=null) cs.addAll(cs2._styles);
    return cs;
}

/**
 * Applies the styles.
 */
public void applyStyles()
{
    // Apply styles  - HTText is bogus
    if(_jsoup!=null)
       getStyleAll().applyStyles();
       
    // Recurse for children
    for(View c : getChildren())
        if(c instanceof HTElement)
            ((HTElement)c).applyStyles();
}

/**
 * Returns the default alignment.
 */    
public Pos getDefaultAlign()  { return Pos.TOP_LEFT; }

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLWidth(double aVal)  { setMinWidth(aVal); }

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLHeight(double aVal)  { setMinHeight(aVal); }

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLWidth(String aVal)
{
    if(aVal.contains("%")) return;
    double val = SnapUtils.doubleValue(aVal);
    setHTMLWidth(val);
}

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLHeight(String aVal)
{
    if(aVal.contains("%")) return;
    double val = SnapUtils.doubleValue(aVal);
    setMinHeight(val);
}

/** Override to use HTLayout. */
protected double getPrefWidthImpl(double aH)  { return HTLayout.getPrefWidth(this, aH); }

/** Override to use HTLayout. */
protected double getPrefHeightImpl(double aW)  { return HTLayout.getPrefHeight(this, aW); }

/** Override to use HTLayout. */
protected void layoutImpl()  { HTLayout.layout(this); }

/**
 * Reads HTML.
 */
public void readHTML(XMLElement aXML, HTDoc aDoc)  { }

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Read attributes
    Attributes attrs = aJSoup.attributes();
    if(attrs!=null)
    for(Attribute attr : attrs) {
        String name = attr.getKey();
        String val = attr.getValue();
        switch(name) {
            case "width": setHTMLWidth(val); break;
            case "height": setHTMLHeight(val); break;
            case "bgcolor": setFill(Color.get(val)); break;
        }
    }
    
    // Read children
    readHTMLChildren(aJSoup, aDoc);
}

/**
 * Reads HTML.
 */
public void readHTMLChildren(Element aJSoup, HTDoc aDoc)
{
    // Iterate over child elements to create child views
    for(Node node : aJSoup.childNodes()) {
        
        // Handle Element: Create child, read and add
        if(node instanceof Element) { Element emt = (Element)node;
            HTElement child = createHTML(emt, aDoc);
            child._jsoup = emt;
            child.readHTML(emt, aDoc);
            addChild(child);
        }
        
        // Handle TextNode
        else if(node instanceof TextNode) { TextNode tnode = (TextNode)node;
            String str = tnode.text().trim();
            if(str!=null && str.length()>0) {
                HTText text = new HTText(); text.setText(str);
                addChild(text);
            }
        }
    }
}

/**
 * Creates an HTML element for given XML.
 */
public static HTElement createHTML(Element aJSoup, HTDoc aDoc)
{
    String name = aJSoup.tagName();
    switch(name) {
        case "a": return new HTLink();
        case "body": return new HTBody();
        case "head": return new HTHead();
        case "html": return new HTDoc();
        case "img": return new HTImage();
        case "li": return new HTListItem();
        case "ol": return new HTList();
        case "p": return new HTParagraph();
        case "style": return new HTStyle();
        case "table": return new HTTable();
        case "td": return new HTTableData();
        case "tr": return new HTTableRow();
        case "ul": return new HTList();
        default: return new HTElement();
    }
}

/**
 * Standard toString implementation.
 */
public String toString()
{
    String str = getClass().getSimpleName();
    str += ' ' + getTagName();
    return str;
}

}