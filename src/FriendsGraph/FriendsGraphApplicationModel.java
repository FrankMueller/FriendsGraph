package FriendsGraph;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jhotdraw.app.action.view.ViewPropertyAction;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.*;
import FriendsGraph.figures.*;

public class FriendsGraphApplicationModel extends DefaultApplicationModel
{
    private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};

    private static class ToolButtonListener implements ItemListener 
    {
        private Tool tool;
        private DrawingEditor editor;

        public ToolButtonListener(Tool t, DrawingEditor editor) 
        {
            this.tool = t;
            this.editor = editor;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) 
        {
            if (evt.getStateChange() == ItemEvent.SELECTED)
                editor.setTool(tool);
        }
    }
    
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;
    private HashMap<String, Action> actions;

    /** Creates a new instance. */
    public FriendsGraphApplicationModel() 
    { }

    @Override
    public ActionMap createActionMap(Application a, @Nullable View v) 
    {
        ActionMap m = super.createActionMap(a, v);
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        AbstractAction aa;

        m.put(ExportFileAction.ID, new ExportFileAction(a, v));
        m.put("view.toggleGrid", aa = new ToggleViewPropertyAction(a, v, FriendsGraphView.GRID_VISIBLE_PROPERTY));
        drawLabels.configureAction(aa, "view.toggleGrid");
        for (double sf : scaleFactors) 
        {
            m.put((int) (sf * 100) + "%", aa = new ViewPropertyAction(a, v, DrawingView.SCALE_FACTOR_PROPERTY, Double.TYPE, new Double(sf)));
            aa.putValue(Action.NAME, (int) (sf * 100) + " %");

        }
        return m;
    }

    public DefaultDrawingEditor getSharedEditor() 
    {
        if (sharedEditor == null)
            sharedEditor = new DefaultDrawingEditor();

        return sharedEditor;
    }

    @Override
    public void initView(Application a, @Nullable View p) 
    {
        if (a.isSharingToolsAmongViews())
        	((FriendsGraphView) p).setEditor(getSharedEditor());
    }

    private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) 
    {
        // AttributeKeys for the entity sets
        HashMap<AttributeKey, Object> attributes;

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("Labels");

        ButtonFactory.addSelectionToolTo(tb, editor);
        tb.addSeparator();

        attributes = new HashMap<AttributeKey, Object>();
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new PersonFigure(), attributes), "edit.createPerson", labels);

        attributes = new HashMap<AttributeKey, Object>();
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(new FriendshipFigure(), attributes), "edit.createFriendship", labels);
    }

    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    @Override
    public java.util.List<JToolBar> createToolBars(Application a, @Nullable View pr) 
    {
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("Labels");
        FriendsGraphView p = (FriendsGraphView) pr;

        DrawingEditor editor;
        if (p == null)
            editor = getSharedEditor();
        else 
            editor = p.getEditor();

        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
        tb.setName(drawLabels.getString("window.drawToolBar.title"));
        list.add(tb);
        return list;
    }

    /** Creates the MenuBuilder. */
    @Override
    protected MenuBuilder createMenuBuilder() 
    {
        return new DefaultMenuBuilder() 
        {
            @Override
            public void addOtherViewItems(JMenu m, Application app, @Nullable View v) 
            {
                ActionMap am = app.getActionMap(v);
                JCheckBoxMenuItem cbmi;
                cbmi = new JCheckBoxMenuItem(am.get("view.toggleGrid"));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get("view.toggleGrid"));
                m.add(cbmi);
                JMenu m2 = new JMenu("Zoom");
                for (double sf : scaleFactors) 
                {
                    String id = (int) (sf * 100) + "%";
                    cbmi = new JCheckBoxMenuItem(am.get(id));
                    ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(id));
                    m2.add(cbmi);
                }
                m.add(m2);
            }
        };
    }

    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) 
    {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("FriendsGraph Diagram", "xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v)
    {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("FriendsGraph Diagram", "xml"));
        return c;
    }
}