package snaphtml.app;
import snap.gfx.Color;
import snap.view.*;
import snaphtml.html.HTUtils;

/**
 * A custom class.
 */
public class GalleryView extends ParentView {
    
    // The GalleryPane
    GalleryPane       _galleryPane;
    
    // The Height of a GalleryItem
    double ITEM_HEIGHT = 60;

/**
 * Creates new GalleryView.
 */
public GalleryView()
{
    // Basic tags
    String tags[] = { "p", "div", "a", "img", "hr", "br", "ul", "ol", "li", "table", "tr", "td" };
    
    for(String tag : tags)
        addChild(new ItemView(tag));
}
    
/**
 * Override to return preferred height of content.
 */
protected double getPrefHeightImpl(double aW)
{
    int colCount = (int)aW/150, childCount = getChildCount(); if(colCount==0) return 0;
    int rowCount = childCount/colCount + (childCount%colCount>0? 1 : 0);
    return rowCount*ITEM_HEIGHT;
}

/**
 * Actual method to layout children.
 */
protected void layoutImpl()
{
    double w = getWidth(), h = getHeight();
    
    int colCount = (int)w/150;
    double cw = Math.floor(w/colCount);
    
    
    double x = 0, y = 0;
    for(int i=0,iMax=getChildCount();i<iMax;i++) { View child = getChild(i);
        child.setBounds(x,y,cw,ITEM_HEIGHT);
        x += cw; if(x+cw>w) { x = 0; y += ITEM_HEIGHT; }
    }
}


/**
 * A class to show individual item.
 */
public class ItemView extends BoxView {
    
    /** Create new ItemView. */
    public ItemView(String aTag)
    {
        // Configure this view
        setBorder(Color.LIGHTGRAY,1); setVertical(true);
        enableEvents(MousePress);
        
        // Create item view, configure and add
        View fname = new Label(HTUtils.getTagFullName(aTag)); fname.setMinSize(24,12); fname.setPickable(false);
        View tag = new Label('<' + aTag + '>'); tag.setMinSize(24,12); tag.setPickable(false);
        setChildren(fname, tag);
        setName(aTag);
    }
    
    /** Handle events. */
    protected void processEvent(ViewEvent anEvent)
    {
        if(anEvent.isMousePress())
            _galleryPane._epane.getEditor().addTag(getName());
    }
}

}