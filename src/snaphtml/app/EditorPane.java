package snaphtml.app;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.view.*;
import snap.viewx.*;
import snap.web.WebURL;
import snaphtml.apptools.*;
import snaphtml.html.*;

/**
 * A class to manage the Editor and controls.
 */
public class EditorPane extends ViewOwner {
    
    // The Transform Pane
    TransitionPane   _transPane;
    
    // The Editor SplitView
    SplitView        _editorSplit;
    
    // The Editor
    Editor           _editor, _realEditor;
    
    // The HTM TextView
    TextView         _htmText;
    
    // A box to hold selection path
    RowView          _selPathBox;
    
    // The deepest view to show in the SelPathBox
    View             _selPathDeep;
    
    // The ViewTree
    TreeView <View>  _viewTree;
    
    // The TabView
    TabView          _tabView;
    
    // The GalleryPane
    GalleryPane      _gallery = new GalleryPane(this);
    
    // The ViewTool
    ViewTool         _viewTool = new ViewToolImpl();

/**
 * Creates a new EditorPane.
 */
public EditorPane()
{
    getUI();
    _viewTool._epane = this;
}

/**
 * Returns the editor.
 */
public Editor getEditor()  { return _editor; }

/**
 * Sets the viewer for this viewer pane.
 */
protected void setEditor(Editor anEditor)
{
    // Stop listening to PropChanges on old
    //if(_editor!=null) _editor.removePropChangeListener(_editerLsnr);
    
    // Set Viewer
    _editor = anEditor;
    ScrollView scroll = getView("EditorScrollView", ScrollView.class);
    scroll.setContent(_editor);
    
    // Start listening to PropChanges
    //_editor.addPropChangeListener(_editerLsnr);
}

/**
 * Returns the document source.
 */
protected WebURL getSourceURL()  { return getEditor().getSourceURL(); }

/**
 * Sets the source URL.
 */
public void setSourceURL(WebURL aURL)  { getEditor().setSourceURL(aURL); }

/**
 * Returns the Editor.Content.
 */
public HTDoc getDoc()  { return _editor.getDoc(); }

/**
 * Returns the Editor.SelView.
 */
public View getSelView()  { return _editor.getSelView(); }

/**
 * Sets the Editor.SelView.
 */
public void setSelView(View aView)  { _editor.setSelView(aView); }

/**
 * Called when SelPath is clicked.
 */
protected void setSelViewKeepPath(View aView)
{
    // Get whether given view is in current path
    boolean inPath = false;
    for(View v=_selPathDeep;v!=null && v!=getEditor().getContentBox();v=v.getParent()) if(v==aView) inPath = true;
    
    // Set SelView and restore SelPathDeep if view was in path
    View deep = _selPathDeep;
    getEditor().setSelView(aView);
    if(inPath) _selPathDeep = deep;
}

/**
 * Returns the selected element.
 */
public HTElement getSelEmt()  { return _editor.getSelEmt(); }

/**
 * Creates a new default editor pane.
 */
public EditorPane newDocument()
{
    HTDoc doc = new HTDoc();
    getEditor().setDoc(doc); setSelView(doc);
    return this;
}

/**
 * Creates a new editor window from an open panel.
 */
public EditorPane showOpenPanel(View aView)
{
    // Get path from open panel for supported file extensions
    String path = FilePanel.showOpenPanel(aView, "HTML File", "html");
    return open(path);
}

/**
 * Creates a new editor window by opening the document from the given source.
 */
public EditorPane open(Object aSource)
{
    // If source is already opened, return editor pane
    WebURL url = WebURL.getURL(aSource);
    //if(!SnapUtils.equals(url, getSourceURL())) {
    //    EditorPane epanes[] = WindowView.getOpenWindowOwners(EditorPane.class);
    //    for(EditorPane epane : epanes) if(SnapUtils.equals(url, epane.getSourceURL())) return epane; }
    
    // Load document (if not found, just return)
    HTDoc doc = getHTDoc(aSource); if(doc==null) return null;

    // Set document
    getEditor().setDoc(doc);
    getEditor()._url = url;
    
    // If source is string, add to recent files menu
    //if(url!=null) RecentFilesPanel.addRecentFile(url.getString());
    
    // Return the editor
    return this;
}

/**
 * Creates a getHTMLDoc from given source.
 */
protected HTDoc getHTDoc(Object aSource)
{
    // If document source is null, just return null
    if(aSource==null || aSource instanceof HTDoc) return (HTDoc)aSource;
    
    // Load document
    HTDoc doc = null; try { doc = HTDoc.getDoc(aSource); }
    
    // If there was an XML parse error loading aSource, show error dialog
    catch(Exception e) {
        e.printStackTrace();
        String msg = StringUtils.wrap("Error reading file:\n" + e.getMessage(), 40);
        runLater(() -> {
            DialogBox dbox = new DialogBox("Error Reading File"); dbox.setErrorMessage(msg);
            dbox.showMessageDialog(getUI()); });
    }
    return doc;
}

/**
 * Saves the current editor document, running the save panel.
 */
public void saveAs()
{
    // Make sure editor isn't previewing
    //setEditing(true);
    
    // Get extensions - if there is an existing extension, make sure it's first in the exts array
    String exts[] = { "snp" };
    if(getSourceURL()!=null && FilePathUtils.getExtension(getSourceURL().getPath())!=null) {
        List ex = new ArrayList(Arrays.asList(exts));
        ex.add(0, "." + FilePathUtils.getExtension(getSourceURL().getPath()));
        exts = (String[])ex.toArray(new String[ex.size()]);
    }
    
    // Run save panel, set Document.Source to path and re-save (or just return if cancelled)
    String path = FilePanel.showSavePanel(getEditor(), "SnapKit UI file", exts); if(path==null) return;
    setSourceURL(WebURL.getURL(path));
    save();
}

/**
 * Saves the current editor document, running the save panel if needed.
 */
public void save()
{
    // If can't save to current source, do SaveAs instead
    if(getSourceURL()==null) { saveAs(); return; }
    
    // Make sure editor isn't previewing
    //setEditing(true);
    
    // Do actual save - if exception, print stack trace and set error string
    try { getEditor().save(); }
    catch(Throwable e) {
        e.printStackTrace();
        String msg = "The file " + getSourceURL().getPath() + " could not be saved (" + e + ").";
        DialogBox dbox = new DialogBox("Error on Save"); dbox.setErrorMessage(msg);
        dbox.showMessageDialog(getUI());
        return;
    }
    
    // Add URL.String to RecentFilesMenu and reset UI
    //if(getSourceURL()!=null) RecentFilesPanel.addRecentFile(getSourceURL().getString());
    resetLater();
}

/**
 * Reloads the current editor document from the last saved version.
 */
public void revert()
{
    // Get filename (just return if null)
    WebURL surl = getSourceURL(); if(surl==null) return;

    // Run option panel for revert confirmation (just return if denied)
    String msg = "Revert to saved version of " + surl.getPathName() + "?";
    DialogBox dbox = new DialogBox("Revert to Saved"); dbox.setQuestionMessage(msg);
    if(!dbox.showConfirmDialog(getUI())) return;
        
    // Re-open filename
    getSourceURL().getFile().reload();
    open(getSourceURL());
}

/**
 * Closes this editor pane
 */
public boolean close()
{
    // Make sure editor isn't previewing
    //setEditing(true);
    
    // If unsaved changes, run panel to request save
    /*if(getEditor().undoerHasUndos()) {
        String filename = getSourceURL()==null? "untitled document" : getSourceURL().getPathName();
        DialogBox dbox = new DialogBox("Unsaved Changes");
        dbox.setWarningMessage("Save changes to " + filename + "?"); dbox.setOptions("Save", "Don't Save", "Cancel");
        switch(dbox.showOptionDialog(getUI(), "Save")) {
            case 0: save();
            case 1: break;
            default: return false;
        }
    }*/
    
    // Deactive current tool, so it doesn't reference this editor
    //getEditor().getCurrentTool().deactivateTool();
    
    // Close window, called EditorClosed and return true to indicate we closed the window
    getWindow().hide();
    editorClosed();
    return true;
}

/**
 * Called when editor is closed.
 */
protected void editorClosed()
{
    // If another open editor is available focus on it, otherwise run WelcomePanel
    EditorPane epane = WindowView.getOpenWindowOwner(EditorPane.class);
    if(epane!=null)
        epane.getEditor().requestFocus();
    else WelcomePanel.getShared().showPanel();
}

/**
 * Returns whether editor is really doing editing.
 */
public boolean isEditing()  { return _realEditor==null; } //getEditor().isEditing(); }

/**
 * Sets whether editor is really doing editing.
 */
public void setEditing(boolean aValue)
{
    // If already set, just return
    if(aValue==isEditing()) return;
    
    // If not yet previewing, store current template then generate report and swap it in
    if(!aValue) {
                
        // Cache current editor and flush any current editing
        _realEditor = getEditor(); //_realEditor.flushEditingChanges();
        
        // Reload content
        HTDoc doc = new ViewArchiver().copy(getDoc());
        
        // Create new editor, set editing to false and set report document
        Editor editor = new Editor(); editor.setEditing(false);
        editor.setDoc(doc);
        editor.setSize(_realEditor.getSize());
        
        // Set new editor
        setEditor(editor);
    }

    // If turning preview off, restore real editor
    else { setEditor(_realEditor); _realEditor = null; }
    
    // Focus on editor
    requestFocus(getEditor());
    resetLater();
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Get TransPane
    _transPane = getView("TransPane", TransitionPane.class);
    
    // Get editor
    _editor = getView("Editor", Editor.class);
    _editor.addPropChangeListener(pce -> editorSelViewChange(), Editor.SelView_Prop);
    
    // Get Editor SplitView and add to TransPane
    _editorSplit = getView("SplitView", SplitView.class);
    _editorSplit.setBorder(null);
    _transPane.setContent(_editorSplit);
    
    // Get/configure ViewTree
    _viewTree = getView("ViewTree", TreeView.class);
    _viewTree.setResolver(new ViewTreeResolver());
    _viewTree.setOwner(this);
    _editorSplit.removeItem(_viewTree);
    
    // Create HTMText
    _htmText = new TextView(); _htmText.setName("HTMText"); _htmText.setFont(Font.Arial14);
    _htmText.getTextArea().addPropChangeListener(pc -> htmTextSelDidChange(), TextView.Selection_Prop);
    
    // Get SelPathBox
    _selPathBox = getView("SelPathBox", RowView.class);
    updateSelPathBox();
    
    // Add action for ESCAPE key to pop selection
    addKeyActionFilter("EscapeAction", "ESCAPE");
    
    // Set Toolbar images
    getView("SaveButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/File_Save.png"));
    getView("CutButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Cut.png"));
    getView("CopyButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Copy.png"));
    getView("PasteButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Paste.png"));
    getView("DeleteButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Delete.png"));
    getView("UndoButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Undo.png"));
    getView("RedoButton", ButtonBase.class).setImage(Image.get(TextPane.class, "pkg.images/Edit_Redo.png"));
    
    // Configure window
    WindowView win = getWindow();
    enableEvents(win, WinClose);
    
    // Add GalleryPane
    _tabView = getView("MainTabView", TabView.class);
    _tabView.addTab(" Add Views ", _gallery.getUI(), 0);
    _tabView.addTab(" Tag Props ", _viewTool.getUI(), 1);
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    // Update UndoButton, RedoButton
    Undoer undoer = _editor.getUndoer();
    setViewEnabled("UndoButton", undoer!=null && undoer.hasUndos()); //undoer.getUndoSetLast()!=null
    setViewEnabled("RedoButton", undoer!=null && undoer.hasRedos()); //undoer.getRedoSetLast()!=null
    
    // Update SelPathBox
    updateSelPathBox();
    
    // Update ViewTree
    if(_viewTree.isShowing()) {
        _viewTree.setItems(getDoc());
        _viewTree.setSelectedItem(null);
        //_viewTree.collapseItem(getDoc());
        _viewTree.expandItem(getDoc());
        for(View v=getSelView();v!=getDoc();v=v.getParent())
            _viewTree.expandItem(v);
        _viewTree.setSelectedItem(getSelView());
    }
    
    // If title has changed, update window title
    String title = getWindowTitle();
    getWindow().setTitle(title);
    getWindow().setDocURL(getSourceURL());
    
    // Reset TabView
    resetTabView();
}

/**
 * Resets the tab view.
 */
protected void resetTabView()
{
    // Get sel view and tools list
    View selView = getSelView();
    List <ViewTool> tools = new ArrayList();
    for(Class c=selView.getClass();c!=View.class;c=c.getSuperclass()) {
        ViewTool tool = getTool(c);
        if(tool!=null) tools.add(0, tool);
    }
    
    // Iterate over tabs and update if not matching
    int buildInCount = 2;
    for(int i=0;i<tools.size();i++) { ViewTool tool = tools.get(i);
        if(i+buildInCount>=_tabView.getTabCount())
            _tabView.addTab(tool.getName(), tool.getUI());
        else if(_tabView.getTabContent(i+buildInCount)!=tool.getUI()) {
            _tabView.addTab(tool.getName(), tool.getUI(), i+2);
            _tabView.removeTab(i+2+1);
        }
    }
    
    // Remove extra tabs
    while(_tabView.getTabCount()>tools.size()+2) _tabView.removeTab(_tabView.getTabCount()-1);
    
    // Update current tab
    _tabView.getContent().getOwner().resetLater();
}

    // Map of tools
    Map <Class,ViewTool>  _tools = new HashMap();

/**
 * Returns the tool for given class.
 */
public ViewTool getTool(Class aClass)
{
    ViewTool tool = _tools.get(aClass);
    if(tool==null) { _tools.put(aClass, tool=createTool(aClass)); tool._epane = this; }
    return tool.getClass()!=ViewTool.class? tool : null;
}

/**
 * Creates the tool for given class.
 */
protected ViewTool createTool(Class aClass)
{
    if(aClass==ButtonBase.class) return new ButtonBaseTool();
    if(aClass==Label.class) return new LabelTool();
    return new ViewTool();
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle Edit CutButton, CopyButton, PasteButton, DeleteButton
    if(anEvent.equals("CutButton")) _editor.cut();
    if(anEvent.equals("CopyButton")) _editor.copy();
    if(anEvent.equals("PasteButton")) _editor.paste();
    if(anEvent.equals("DeleteButton")) _editor.delete();
    
    // Handle Edit UndoButton, RedoButton
    if(anEvent.equals("UndoButton")) _editor.undo();
    if(anEvent.equals("RedoButton")) _editor.redo();
    
    // Handle EditButton
    if(anEvent.equals("EditButton")) {
        _transPane.setTransition(TransitionPane.MoveLeft);
        _transPane.setContent(_editorSplit);
    }
    
    // Handle HTMButton
    if(anEvent.equals("HTMButton")) {
        if(_transPane.getContent()==_editorSplit) _transPane.setTransition(TransitionPane.MoveRight);
        else _transPane.setTransition(TransitionPane.MoveLeft);
        _transPane.setContent(_htmText);
        updateHtmText();
     }

    // Handle PreviewButton
    if(anEvent.equals("PreviewButton")) {
        
        // Create copy of content
        HTDoc doc = new ViewArchiver().copy(getDoc());
        if(doc.getFill()==null) doc.setFill(Color.WHITE);
        
        // Create BoxView
        BoxView box = new BoxView(doc, false, false); box.setFill(ViewUtils.getBackDarkFill());
        
        // Add to TransPane
        _transPane.setTransition(TransitionPane.MoveRight);
        _transPane.setContent(box);
    }

    // Handle ShowViewTreeButton
    if(anEvent.equals("ShowViewTreeButton"))
        toggleShowViewTree();
        
    // Handle AddRowButton, AddColButton
    if(anEvent.equals("AddRowButton")) addRowView();
    if(anEvent.equals("AddColButton")) addColView();

    // Handle ViewTree
    if(anEvent.equals(_viewTree)) {
        View view = _viewTree.getSelectedItem();
        getEditor().setSelView(view);
    }
    
    // Handle EscapeAction
    if(anEvent.equals("EscapeAction")) {
        View sview = getSelView(), par = sview.getParent();
        if(sview!=getDoc())
            setSelView(par);
        else beep();
    }
    
    // Handle SaveMenuItem, SaveButton, SaveAsMenuItem, SaveAsPDFMenuItem, RevertMenuItem
    if(anEvent.equals("SaveMenuItem") || anEvent.equals("SaveButton")) save();
    if(anEvent.equals("SaveAsMenuItem")) saveAs();
    if(anEvent.equals("RevertMenuItem")) revert();
    
    // Handle WinClosing
    if(anEvent.isWinClose()) {
        close(); anEvent.consume(); }
}

/**
 * Called to update SelPathBox.
 */
protected void updateSelPathBox()
{
    _selPathBox.removeChildren(); if(_selPathDeep==null) _selPathDeep = getSelView();
    View sview = getSelView(), cview = getDoc(), view = _selPathDeep;
    while(view!=null) { View view2 = view;
        String name = view instanceof HTElement? '<' + ((HTElement)view).getTagName() + '>' :
            view.getClass().getSimpleName();
        Label label = new Label(name); label.setPadding(2,2,2,2);
        if(view==sview) label.setFill(Color.LIGHTGRAY);
        label.addEventHandler(e -> selPathItemClicked(view2), MouseRelease);
        _selPathBox.addChild(label, 0);
        if(_selPathBox.getChildCount()>1) _selPathBox.addChild(new Label(" \u2022 "),1);
        if(view==cview) break; view = view.getParent();
    }
}

/**
 * Updates the HTMText TextView.
 */
protected void updateHtmText()
{
    // If not showing, just return
    if(!_htmText.isShowing()) return;
    
    // Get View
    HTDoc doc = getDoc();
    String text = doc.getHtmlText(false);
    _htmText.setText(text);
    runLaterDelayed(200, () -> updateHtmTextSel());
}

/**
 * Updates the HTMText TextView.
 */
protected void updateHtmTextSel()
{
    // If not showing, just return
    if(!_htmText.isShowing() || _htmTextSelChanging) return;
    
    // Get View
    HTElement selEmt = getSelEmt(); if(selEmt==null) { _htmText.setSel(0); return; }
    int start = selEmt.getCharStart();
    int end = selEmt.getCharEnd();
    _htmText.setSel(start, end);
}

/**
 * Updates the HTMText TextView.
 */
protected void htmTextSelDidChange()
{
    int start = _htmText.getSelStart(), end = _htmText.getSelEnd();
    HTElement emt = getDoc().getEmtInCharRange(start, end);
    _htmTextSelChanging = true;
    setSelViewKeepPath(emt);
    _htmTextSelChanging = false;
}

// 
boolean _htmTextSelChanging;

/**
 * Called when Editor.SelView changes.
 */
protected void editorSelViewChange()
{
    _selPathDeep = getSelView();
    updateHtmTextSel();
    resetLater();
}

/**
 * Called when SelPath is clicked.
 */
protected void selPathItemClicked(View aView)  { setSelViewKeepPath(aView); }

/**
 * Shows/Hides ViewTree.
 */
protected void toggleShowViewTree()
{
    if(_viewTree.getParent()==null)
        _editorSplit.addItemWithAnim(_viewTree, 160, 0);
    else _editorSplit.removeItemWithAnim(_viewTree);
}
    
/**
 * Adds a new row view.
 */
public void addRowView()
{
    // Create/configure RowView and get selected view
    RowView newRow = new RowView(); ViewHpr.getHpr(newRow).configure(newRow);
    View view = getSelView();
    
    // Handle special cases: Empty TitleView, Empty ScrollView, TabView, SplitView
    if(ViewHpr.getHpr(view).wantsView(view, newRow)) {
        ((HostView)view).addGuest(newRow); setSelView(newRow); return; }
    
    // Get parent ColView
    ColView colView = null;
    while(colView==null) {
        if(view.getParent() instanceof ColView) colView = (ColView)view.getParent();
        else if(view==getDoc()) return;
        else view = view.getParent();
    }
    
    // Add new row to ColView and select it
    colView.addChild(newRow, view!=null? view.indexInParent()+1 : colView.getChildCount());
    setSelView(newRow);
}
    
/**
 * Adds a new col view.
 */
public void addColView()
{
    // Create/configure RowView and get selected view
    ColView newCol = new ColView(); ViewHpr.getHpr(newCol).configure(newCol);
    View view = getSelView();
    
    // Handle special cases: Empty TitleView, Empty ScrollView, TabView, SplitView
    if(ViewHpr.getHpr(view).wantsView(view, newCol)) {
        ((HostView)view).addGuest(newCol); setSelView(newCol); return; }
    
    // Get parent RowView
    RowView rowView = null;
    while(rowView==null) {
        if(view==getDoc()) return;
        if(view.getParent() instanceof RowView) rowView = (RowView)view.getParent();
        else view = view.getParent();
    }
    
    // Add new col to RowView and select it
    rowView.addChild(newCol, view!=null? view.indexInParent()+1 : rowView.getChildCount());
    setSelView(newCol);
}
    
/**
 * Returns the window title.
 */
public String getWindowTitle()
{
    // Get window title: Basic filename + optional "Doc edited asterisk + optional "Doc Scaled"
    String title = getSourceURL()!=null? getSourceURL().getPath() : null; if(title==null) title = "Untitled";

    // If has undos, add asterisk. If zoomed, add ZoomFactor
    if(!isEditing()) title += "(preview)";
    else if(getEditor().getUndoer()!=null && getEditor().getUndoer().hasUndos()) title = "* " + title;
    return title;
}

/**
 * A resolver for Views.
 */
public class ViewTreeResolver extends TreeResolver <View> {
    
    /** Returns the parent of given item. */
    public View getParent(View anItem)  { return anItem!=getDoc()? anItem.getParent() : null; }

    /** Whether given object is a parent (has children). */
    public boolean isParent(View anItem)
    {
        if(!(anItem instanceof ParentView)) return false;
        if(anItem instanceof Label || anItem instanceof ButtonBase || anItem instanceof Spinner ||
            anItem instanceof TextField) return false;
        if(anItem instanceof ComboBox || anItem instanceof ListView) return false;
        return ((ParentView)anItem).getChildCount()>0;
    }

    /** Returns the children. */
    public View[] getChildren(View aParent)
    {
        ParentView par = (ParentView)aParent;
        if(par instanceof ScrollView) { ScrollView sp = (ScrollView)par;
            return sp.getContent()!=null? new View[] { sp.getContent() } : new View[0]; }
        return par.getChildren();
    }

    /** Returns the text to be used for given item. */
    public String getText(View anItem)
    {
        String str = anItem.getClass().getSimpleName();
        if(anItem instanceof HTElement) str = '<' + ((HTElement)anItem).getTagName() + '>';
        return str;
    }

    /** Return the image to be used for given item. */
    public View getGraphic(View anItem)  { return null; }
}

}