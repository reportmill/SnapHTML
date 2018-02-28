package snaphtml.html;
import snap.view.*;

/**
 * A viewer for HTML documents.
 */
public class HTViewer extends ViewOwner {
    
    // The source
    Object       _src;
    
    // The document
    HTDoc        _doc;
    
    // The document box
    BoxView          _docBox = new BoxView();

/**
 * Creates a new HTMLViewer.
 */
public HTViewer(Object aSource)
{
    HTDoc doc = HTDoc.getDoc(aSource);
    setDoc(doc);
}

/**
 * Creates the UI.
 */
protected View createUI()  { return _docBox; }

/**
 * Returns the document.
 */
public HTDoc getDoc()  { return _doc; }

/**
 * Sets the document.
 */
public void setDoc(HTDoc aDoc)
{
    _docBox.setContent(_doc = aDoc);
}

/**
 * Standard main method.
 */
public static void main(String args[])
{
    HTDoc doc = new HTDoc("/Temp/ReportMill!/index2.html");
    HTViewer viewer = new HTViewer(doc);
    viewer.getWindow().setTitle(viewer.getDoc().getTitle());
    viewer.setWindowVisible(true);
}

}