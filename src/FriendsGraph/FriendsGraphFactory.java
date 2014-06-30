package FriendsGraph;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
import FriendsGraph.figures.*;

public class FriendsGraphFactory extends DefaultDOMFactory
{
    private final static Object[][] classTagArray = 
    {
        { DefaultDrawing.class, "FriendsGraphDiagram" },
        { PersonFigure.class, "person" },
        { FriendshipFigure.class, "friendship" },
    };
    
    /** Creates a new instance. */
    public FriendsGraphFactory() 
    {
        for (Object[] o : classTagArray)
            addStorableClass((String) o[1], (Class) o[0]);
    }
}
