package snaphtml.html;
import org.jsoup.nodes.Element;
import snap.util.StringUtils;
import snap.view.ImageView;
import snap.gfx.Image;
import snap.web.WebURL;

/**
 * A HTElement subclass for HTML image.
 */
public class HTImage extends HTElement {

    // The image source
    String          _src;
    
    // The image
    Image           _img;

/**
 * Returns the image source.
 */
public String getSource()  { return _src; }

/**
 * Sets the image source.
 */
public void setSource(String aSrc)
{
    _src = aSrc;
}

/**
 * Returns the image.
 */
public Image getImage()  { return _img; }

/**
 * Sets the image.
 */
public void setImage(Image anImage)
{
    _img = anImage;
    ImageView iview = getChildCount()>0? (ImageView)getChild(0) : null;
    if(iview==null)
        addChild(iview = new ImageView(anImage));
    else iview.setImage(anImage);
}

/**
 * Loads the image.
 */
protected void loadImage(HTDoc aDoc)
{
    WebURL surl = aDoc.getSourceURL(_src);
    if(surl==null) { System.err.println("HTMLImage.loadImage: Can't find image for source: " + _src); return; }
    Image img = Image.get(surl);
    if(img!=null)
        setImage(img);
}

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLWidth(String aVal)
{
    setPrefWidth(StringUtils.doubleValue(aVal));
}

/**
 * Sets the HTML width for this view, which usually means min.
 */
public void setHTMLHeight(String aVal)
{
    setPrefHeight(StringUtils.doubleValue(aVal));
}

/**
 * Reads HTML.
 */
public void readHTML(Element aJSoup, HTDoc aDoc)
{
    // Do normal version
    super.readHTML(aJSoup, aDoc);
    
    // Read src
    String src = aJSoup.attributes().get("src");
    if(src!=null) {
        setSource(src);
        loadImage(aDoc);
    }
}

}