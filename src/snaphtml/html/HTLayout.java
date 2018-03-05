package snaphtml.html;
import java.util.*;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to layout an element.
 */
public class HTLayout {

/**
 * Override.
 */
protected static double getPrefWidth(HTElement aPar, double aH)
{
    if(aPar.isHorizontal() && hasBreakTag(aPar)) {
        Insets ins = aPar.getInsetsAll();
        View children[][] = getBreakChildren(aPar);
        double bw = 0; for(View[] childs : children)
            bw = Math.max(bw, RowView.getPrefWidth(aPar, childs, 0, aH)); // Need to add Insets.EMTPY
        return bw + ins.getWidth();
    }
    
    // Do BoxView version
    if(aPar.isHorizontal()) return RowView.getPrefWidth(aPar, null, aPar.getSpacing(), aH);
    return ColView.getPrefWidth(aPar, null, aH);
}

/**
 * Override.
 */
protected static double getPrefHeight(HTElement aPar, double aW)
{
    if(aPar.isHorizontal() && hasBreakTag(aPar)) {
        Insets ins = aPar.getInsetsAll();
        View children[][] = getBreakChildren(aPar);
        double bh = 0; for(View[] childs : children)
            bh += ColView.getPrefHeight(aPar, childs, 0, aW);
        return bh + ins.getHeight();
    }
    
    // Do BoxView version
    if(aPar.isHorizontal()) return RowView.getPrefHeight(aPar, null, aW);
    return ColView.getPrefHeight(aPar, null, aPar.getSpacing(), aW);
}

/**
 * Override.
 */
protected static void layout(HTElement aPar)
{
    if(aPar.isHorizontal() && hasBreakTag(aPar)) {
        Insets ins = aPar.getInsetsAll().clone();
        View children[][] = getBreakChildren(aPar);
        for(View[] childs : children) {
            RowView.layout(aPar, childs, ins, false, false, 0);
            double maxy = 0; for(int i=0;i<childs.length;i++) maxy = Math.max(maxy, childs[i].getMaxY());
            ins.top = maxy;
        }
    }
    
    // Do BoxView version
    else if(aPar.isHorizontal()) RowView.layout(aPar, null, null, false, false, aPar.getSpacing());
    else ColView.layout(aPar, null, null, false, false, aPar.getSpacing());
}

/**
 * Returns whether tag is a break tag.
 */
static boolean isBreakTag(String aStr)
{
    return aStr.equals("p") || aStr.equals("br") || aStr.equals("hr") || aStr.equals("div") ||
        aStr.equals("ol") || aStr.equals("ul");
}

/**
 * Returns whether child has break tag.
 */
public static boolean hasBreakTag(HTElement aPar)
{
    for(int i=1,iMax=aPar.getChildCount();i<iMax;i++) { HTElement emt = (HTElement)aPar.getChild(i);
        if(isBreakTag(emt.getTagName()))
            return true; }
    return false;
}

/**
 * Returns whether child has break tag.
 */
public static View[][] getBreakChildren(HTElement aPar)
{
    List <View[]> top = new ArrayList();
    List <View> curr = new ArrayList();
    for(View child : aPar.getChildren()) { HTElement emt = (HTElement)child;
        if(isBreakTag(emt.getTagName())) {
            top.add(curr.toArray(new View[0])); curr.clear(); }
        curr.add(child);
    }
    top.add(curr.toArray(new View[0]));
    return top.toArray(new View[0][]);
}

}