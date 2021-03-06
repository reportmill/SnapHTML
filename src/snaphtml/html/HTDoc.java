package snaphtml.html;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import snap.gfx.Color;
import snap.util.*;
import snap.view.View;
import snap.web.*;

/**
 * A view for displaying an HTML document.
 */
public class HTDoc extends HTElement {

    // The source URL
    WebURL      _srcURL;
    
    // The title
    String      _title;
    
    // The Head
    HTHead      _head;
    
    // The body
    HTBody      _body;
    
    // The HTML text
    String      _htmText;
    
/**
 * Creates a new HTMLDoc.
 */
public HTDoc()
{
    setFill(Color.WHITE);
    setVertical(true);
}

/**
 * Creates a new HTMLDoc for given source.
 */
public HTDoc(Object aSource)
{
    this();
    WebURL surl = _srcURL = WebURL.getURL(aSource);
    
    //XMLElement xml = null;
    //try { xml = new HTParser().parseXML(surl); }
    //catch(Exception e) { throw new RuntimeException(e); }
    //readHTML(xml, this);
    
    WebURL url = WebURL.getURL(aSource);
    String text = url.getText();
    Document doc = Jsoup.parse(text); //doc = new Cleaner(Whitelist.basic()).clean(doc);
    readHTML(doc, this);
    
    setFill(Color.WHITE);
    
    getBody().applyStyles();
}

/**
 * Returns the tag name.
 */
public String getTagName()  { return "html"; }

/**
 * Override to return JSoup as Document.
 */
public Document getSoup()  { return (Document)super.getSoup(); }

/**
 * Returns the document source URL.
 */
public WebURL getSourceURL()  { return _srcURL; }

/**
 * Sets the document source URL.
 */
public void setSourceURL(WebURL aURL)  { _srcURL = aURL; }

/**
 * Returns the title.
 */
public String getTitle()  { return _title; }

/**
 * Sets the title.
 */
public void setTitle(String aTitle)  { _title = aTitle; }

/**
 * Returns the head.
 */
public HTHead getHead()
{
    if(_head!=null) return _head;
    for(View c : getChildren()) if(c instanceof HTHead) return _head = (HTHead)c;
    return null;
}

/**
 * Returns the body.
 */
public HTBody getBody()
{
    if(_body!=null) return _body;
    for(View c : getChildren()) if(c instanceof HTBody) return _body = (HTBody)c;
    return null;
}

/**
 * Returns the CSSStyles.
 */
public CSSStyles getStyles()  { return getHead().getStyles(); }

/**
 * Returns the source URL for given string path.
 */
public WebURL getSourceURL(String aPath)
{
    if(_srcURL==null) return null;
    if(StringUtils.startsWithIC(aPath,"http"))
        return WebURL.getURL(aPath);
    String spath = _srcURL.getPath();
    String path = FilePathUtils.getPeer(spath, aPath);
    WebSite site = _srcURL.getSite();
    return site.getURL(path);
}

/**
 * Returns the HTML text.
 */
public String getHtmlText()  { return getHtmlText(true); }

/**
 * Returns the HTML text with option to use cached version (otherwise, recache).
 */
public String getHtmlText(boolean useCache)
{
    if(_htmText!=null && useCache) return _htmText;
    _htmText = getSoup().outerHtml();
    setCharIndexes(this, _htmText, 0);
    _charEnd = _htmText.length();
    return _htmText;
}

/**
 * Sets the char indexes for HTML text.
 */
int setCharIndexes(HTElement anEmt, String aStr, int aStart)
{
    String tag = anEmt.getTagName(); anEmt._charEnd = -1;
    int start2 = anEmt._charStart = aStr.indexOf('<' + tag, aStart); start2 += tag.length() + 1;
    for(View child : anEmt.getChildren()) {
        HTElement emt = child instanceof HTElement? (HTElement)child : null; if(emt==null) continue;
        start2 = setCharIndexes(emt, aStr, start2);
    }
    return start2;
}

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Get Head and Title
    Document doc = (Document)aJSoup; _jsoup = doc;
    Element head = doc.head();
    Element title = doc.getElementsByTag("title").first();
    if(title!=null) {
        String titleStr = title.text();
        setTitle(titleStr);
    }
    
    // Do normal version
    Element html = doc.children().first();
    super.readHTML(html, aDoc);
}

/**
 * Returns an HTML doc for given source object.
 */
public static HTDoc getDoc(Object aSource)
{
    if(aSource instanceof HTDoc)
        return (HTDoc)aSource;
    try { return new HTDoc(aSource); }
    //catch(Exception e) { System.err.println("HTMLDoc.getDoc: Error reading source: " + e); return null; }
    catch(Exception e) { e.printStackTrace(); return null; }
}

}