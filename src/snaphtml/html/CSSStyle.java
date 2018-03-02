package snaphtml.html;
import java.util.*;
import snap.gfx.*;
import snap.util.*;

/**
 * A class to manage the CSS style for an element.
 */
public class CSSStyle {
    
    // The Element
    HTElement    _emt;
    
    // The map holding the actual style entries
    Map <String,String> _styles = new HashMap();
    
/**
 * Sets a property.
 */
public void setProperty(String aKey, String aVal)  { _styles.put(aKey, aVal); }

/**
 * Sets a property.
 */
public void addAll(Map <String,String> theProps)
{
    for(Map.Entry<String,String> entry : theProps.entrySet())
        setProperty(entry.getKey(), entry.getValue());
}

/**
 * Applies the styles to given element.
 */
public void applyStyles()
{
    // Do font stuff first
    String fontKeys[] = { "font-family", "font-size", "font-weight" };
    for(String key : fontKeys) { String val = _styles.get(key); if(val==null) continue;
        switch(key) {
            case "font-family": applyFontFamily(val); break;
            case "font-size": applyFontSize(val); break;
            case "font-weight": applyFontWeight(val); break;
        }
    }
    
    // Iterate over keys
    for(String key : _styles.keySet()) {
        if(key.startsWith("font-")) continue;
        applyStyle(key, _styles.get(key));
    }
}

/**
 * Applies the style to given element.
 */
public void applyStyle(String key, String val)
{
    
    switch(key) {
        case "margin": applyMargin(val); break;
        case "margin-top": applyMarginPart(val, 0); break;
        case "margin-right": applyMarginPart(val, 1); break;
        case "margin-bottom": applyMarginPart(val, 2); break;
        case "margin-left": applyMarginPart(val, 3); break;
        case "padding": applyMargin(val); break;
        case "padding-top": applyMarginPart(val, 0); break;
        case "padding-right": applyMarginPart(val, 1); break;
        case "padding-bottom": applyMarginPart(val, 2); break;
        case "padding-left": applyMarginPart(val, 3); break;
    }
}

/**
 * Applies the font family.
 */
public void applyFontFamily(String aVal)
{
    String fams[] = aVal.split(",");
    for(String fam : fams) {
        Font font = _emt.getFont();
        Font font2 = new Font(fam, font.getSize());
        _emt.setFont(font2);
    }
}

/**
 * Applies the font size.
 */
public void applyFontSize(String aVal)
{
    double size = StringUtils.doubleValue(aVal);
    _emt.setFont(_emt.getFont().deriveFont(size));
}

/**
 * Applies the font weight.
 */
public void applyFontWeight(String aVal)
{
    switch(aVal) {
        case "bold": _emt.setFont(_emt.getFont().getBold()); break;
    }
}

/**
 * Applies the margin.
 */
public void applyMargin(String aVal)
{
    // Get margin parts (just return if empty)
    String parts[] = aVal.split("\\s+"); if(parts.length==0) return;
    
    // Convert string parts to length values
    double vals[] = new double[4]; for(int i=0;i<parts.length;i++) vals[i] = getSize(parts[i].toLowerCase(), i);
    
    // Fill in any missing values: 1 val = apply to all, 2 vals = ver x hor, 3 vals = tp + w + bt
    if(parts.length==1) { vals[1] = vals[2] = vals[3] = vals[0]; }
    else if(parts.length==2) { vals[2] = vals[0]; vals[3] = vals[1]; }
    else if(parts.length==3) { vals[3] = vals[1]; }
    
    // Apply padding
    _emt.setPadding(vals[0], vals[1], vals[2], vals[3]);
}

/**
 * Applies the margin.
 */
public void applyMarginPart(String aVal, int anInd)
{
    // Convert string parts to length values
    double val = getSize(aVal, anInd);
    Insets ins = _emt.getPadding().clone();
    
    // Update val
    if(anInd==0) ins.top = val;
    else if(anInd==1) ins.right = val;
    else if(anInd==2) ins.bottom = val;
    else ins.left = val;
    
    // Apply padding
    _emt.setPadding(ins);
}

/**
 * Returns a size for given string with num + unit, e.g.: 1px, 2pt, .1em.
 */
public double getSize(String aStr, int ind)
{
    double val = SnapUtils.doubleValue(aStr), conv = 1;
    if(aStr.contains("px")) conv = 72/96d;
    else if(aStr.contains("%")) conv = ind==1 || ind==3? _emt.getWidth()/100 : _emt.getHeight()/100;
    else if(aStr.contains("pt")) conv = 1;
    else if(aStr.contains("em")) conv = _emt.getFont().getSize();
    else System.out.println("Unknown unit: " + aStr);
    return Math.round(val * conv);
}

}