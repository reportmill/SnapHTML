package snaphtml.html;
import snap.gfx.Color;

/**
 * A HTElement subclass for HTML table.
 */
public class HTTable extends HTElement {

/**
 * Creates a HTMLTable.
 */
public HTTable()
{
    setBorder(Color.LIGHTBLUE.brighter().brighter().brighter(),1);
    setVertical(true);
}

}