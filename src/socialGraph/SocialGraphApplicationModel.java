package socialGraph;

import java.util.*;

import javax.swing.*;

import edu.umd.cs.findbugs.annotations.Nullable;

import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.view.ViewPropertyAction;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.util.*;

import socialGraph.figures.*;

/**
 * An {@link ApplicationModel} which creates a the set of {@code Action}s available in the {@code SocialGraphApplication}.
 */
public class SocialGraphApplicationModel extends DefaultApplicationModel
{
	private static final long serialVersionUID = 3721219497169399164L;
	private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
    private DefaultDrawingEditor sharedEditor;

    /**
     * Creates a new instance. 
     */
    public SocialGraphApplicationModel() 
    { 
    	sharedEditor = new DefaultDrawingEditor();
    	
        setName("Social Graph");
        setVersion(Main.class.getPackage().getImplementationVersion());
        setCopyright("Created by group 22:\n\tGhadh Altaiari,\n\tFelix Held,\n\tFrank Müller");
        setViewClassName("socialGraph.SocialGraphView");
    }

    /**
     * Returns an {@code ActionMap} with the set of actions available in the {@code SocialGraphApplication}.
     */
    @Override
    public ActionMap createActionMap(Application application, @Nullable View view) 
    {
    	//Build an empty action map
        ActionMap actionMap = new ActionMap();
        actionMap.put(NewFileAction.ID, new NewFileAction(application));
        actionMap.put(CloseFileAction.ID, new CloseFileAction(application, view));
 
        //Get the resource bundle containing the labels for our actions
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

        //Add an action for the grid style toggle feature
        ToggleViewPropertyAction toggleViewPropertyAction = new ToggleViewPropertyAction(application, view, SocialGraphView.GRID_VISIBLE_PROPERTY);
        actionMap.put("view.toggleGrid", toggleViewPropertyAction);
        drawLabels.configureAction(toggleViewPropertyAction, "view.toggleGrid");

        //Add actions for the zoom feature
        for (double scaleFactor : scaleFactors)
        {
        	ViewPropertyAction viewPropertyAction = new ViewPropertyAction(application, view, DrawingView.SCALE_FACTOR_PROPERTY, Double.TYPE, new Double(scaleFactor));
            actionMap.put((int) (scaleFactor * 100) + "%", viewPropertyAction);
            viewPropertyAction.putValue(Action.NAME, (int) (scaleFactor * 100) + " %");
        }
        
        return actionMap;
    }

    @Override
    public void initView(Application application, @Nullable View view) 
    {
        if (application.isSharingToolsAmongViews())
        	((SocialGraphView) view).setEditor(sharedEditor);
    }

    /**
     * Creates the tool bars for the application.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public java.util.List<JToolBar> createToolBars(Application application, @Nullable View view) 
    {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("Labels");
        SocialGraphView socialGraphView = (SocialGraphView) view;

        DrawingEditor editor;
        if (socialGraphView == null)
            editor = sharedEditor;
        else 
            editor = socialGraphView.getEditor();

        //Create a new tool bar
        JToolBar editToolBar = new JToolBar();
        editToolBar.setName(labels.getString("window.editToolBar.title"));
        
        //Add the buttons to the tool bar
        ButtonFactory.addSelectionToolTo(editToolBar, editor);
        editToolBar.addSeparator();
        ButtonFactory.addToolTo(editToolBar, editor, new CreationTool(new PersonFigure(), new HashMap<AttributeKey, Object>()), "edit.createPerson", labels);
        ButtonFactory.addToolTo(editToolBar, editor, new ConnectionTool(new FriendshipFigure(), new HashMap<AttributeKey, Object>()), "edit.createFriendship", labels);
        
        //Create a list of tool bars and add our newly created one to the list
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        list.add(editToolBar);
        
        return list;
    }

    /** Creates the MenuBuilder. */
    @Override
    protected MenuBuilder createMenuBuilder() 
    {
        return new DefaultMenuBuilder() 
        {
            @Override
            public void addOtherViewItems(JMenu menu, Application application, @Nullable View view) 
            {
                ActionMap actionMap = application.getActionMap(view);
                
                //Create a menu item for the grid style
                JCheckBoxMenuItem toggleGridMenuItem = new JCheckBoxMenuItem(actionMap.get("view.toggleGrid"));
                ActionUtil.configureJCheckBoxMenuItem(toggleGridMenuItem, actionMap.get("view.toggleGrid"));
                menu.add(toggleGridMenuItem);
                
                //Create a menu for the zoom factor with sub items for the supported zoom factors
                JMenu zoomMenu = new JMenu("Zoom");
                for (double scaleFactor : scaleFactors) 
                {
                    String id = (int) (scaleFactor * 100) + "%";
                    JCheckBoxMenuItem zoomFactorMenuItem = new JCheckBoxMenuItem(actionMap.get(id));
                    ActionUtil.configureJCheckBoxMenuItem(zoomFactorMenuItem, actionMap.get(id));
                    zoomMenu.add(zoomFactorMenuItem);
                }
                menu.add(zoomMenu);
            }
        };
    }
}