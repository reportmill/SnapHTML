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
    
    // The char start/end index of this element in doc HTML text
    int         _charStart, _charEnd;

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

/**
 * Override to apply to HTText.
 */
public void setFont(Font aFont)
{
    // Do normal version
    super.setFont(aFont);
    
    // Fowrard to text
    for(View child : getChildren())
        if(child instanceof TextArea)
            child.setFont(getFont());
}

/** Override to use HTLayout. */
protected double getPrefWidthImpl(double aH)  { return HTLayout.getPrefWidth(this, aH); }

/** Override to use HTLayout. */
protected double getPrefHeightImpl(double aW)  { return HTLayout.getPrefHeight(this, aW); }

/** Override to use HTLayout. */
protected void layoutImpl()  { HTLayout.layout(this); }

/**
 * Called to configure new for designer.
 */
public void configureNew()  { }

/**
 * Adds text to this element.
 */
public void addText(String aStr)
{
    HTText text = new HTText(); text.setText(aStr);
    addChild(text);
}

/**
 * Returns the parent element.
 */
public HTElement getParentEmt()  { View par = getParent(); return par instanceof HTElement? (HTElement)par : null; }

/**
 * Returns the index of this element in parent.
 */
public int getChildEmtCount()  { int ec = 0; for(View c : getChildren()) if(c instanceof HTElement) ec++; return ec; }

/**
 * Returns the index of this element in parent.
 */
public HTElement getChildEmt(int anIndex)
{
    for(int i=0,iMax=getChildCount(),j=0;i<iMax;i++) { View child = getChild(i);
        if(j==anIndex && child instanceof HTElement) return (HTElement)child;
        if(child instanceof HTElement) j++;
    }
    throw new IndexOutOfBoundsException("HTElement.getChildElm: " + anIndex + " beyond " + getChildEmtCount());
}

/**
 * Returns the index of this element in parent.
 */
public HTElement getChildEmtLast()
{
    HTElement ce = null; for(View c : getChildren()) if(c instanceof HTElement) ce = (HTElement)c; return ce;
}

/**
 * Returns the index of this element in parent.
 */
public HTElement getChildEmtLastDeep()
{
    HTElement ce = getChildEmtLast();
    HTElement ce2 = ce!=null? ce.getChildEmtLastDeep() : null;
    return ce2!=null? ce2 : ce;
}

/**
 * Returns the index of this element in parent.
 */
public int indexInParentEmt()
{
    HTElement par = getParentEmt(); if(par==null) return -1;
    for(int i=0,iMax=par.getChildCount(),j=0;i<iMax;i++) { View child = getChild(i);
        if(child==this) return j;
        if(child instanceof HTElement) j++;
    }
    return -1;
}

/**
 * Returns the previous element.
 */
public HTElement getEmtPrev()
{
    int ind = indexInParentEmt(); if(ind<=0) return getParentEmt();
    HTElement par = getParentEmt();
    HTElement ep = par.getChildEmt(ind-1);
    HTElement epl = ep.getChildEmtLastDeep();
    return epl!=null? epl : ep;
}

/**
 * Returns the character index of this element in document HTML text.
 */
public int getCharStart()  { return _charStart; }

/**
 * Returns the character index of this element in document HTML text.
 */
public int getCharEnd()
{
    // If already set, just return
    if(_charEnd>=0) return _charEnd;
    
    // Get doc text
    String str = getDoc().getHtmlText();
    
    // Get tail text for this element from last whitespace to end
    String str2 = getSoup().outerHtml();
    int end2 = str2.length(); while(end2>0 && str2.charAt(end2-1)!='<') end2--;
    str2 = str2.substring(end2);
    
    // Get search start from end of open tag or end of last child
    int start = _charStart;
    HTElement lastChild = getChildEmtLast();
    if(lastChild!=null)
        start = lastChild.getCharEnd();

    // Get end by searching for tail text starting from
    _charEnd = str.indexOf(str2, start) + str2.length();
    return _charEnd;
}

/**
 * Returns the HTElement in given char range.
 */
public HTElement getEmtInCharRange(int aStart, int aEnd)
{
    for(View child : getChildren()) {
        HTElement emt = child instanceof HTElement? (HTElement)child : null; if(emt==null) continue;
        HTElement emt2 = emt.getEmtInCharRange(aStart, aEnd);
        if(emt2!=null)
            return emt2;
    }
    
    int cstart = getCharStart(), cend = getCharEnd();
    if(cstart<=aStart && aEnd<=cend)
        return this;
    return null;
}

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
            String tag = emt.tagName();
            HTElement child = createHTML(tag);
            child._jsoup = emt;
            child.readHTML(emt, aDoc);
            addChild(child);
        }
        
        // Handle TextNode
        else if(node instanceof TextNode) { TextNode tnode = (TextNode)node;
            String str = tnode.text().trim();
            if(str!=null && str.length()>0)
                addText(str);
        }
    }
}

/**
 * Creates an HTML element for given tag.
 */
public static HTElement createHTML(String aTag)
{
    switch(aTag) {
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
 * Creates an HTElement for given HTElement and tag.
 */
public static HTElement createHTML(HTElement anEmt, String aTag)
{
    HTElement emt = createHTML(aTag);
    Document jsoupDoc = anEmt.getDoc().getSoup();
    Element jsoup = jsoupDoc.createElement(aTag);
    emt._jsoup = jsoup;
    return emt;
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