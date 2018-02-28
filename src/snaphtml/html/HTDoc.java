package snaphtml.html;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import snap.gfx.Color;
import snap.util.*;
import snap.web.*;

/**
 * A view for displaying an HTML document.
 */
public class HTDoc extends HTElement {

    // The source URL
    WebURL      _srcURL;
    
    // The title
    String      _title;
    
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
    
    setPrefSize(800,800);
    setFill(Color.WHITE);
}

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
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Get Head and Title
    Document doc = (Document)aJSoup;
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
    catch(Exception e) { System.err.println("HTMLDoc.getDoc: Error reading source: " + e); return null; }
}

}