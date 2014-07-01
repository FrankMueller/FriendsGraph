package socialGraph;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;

import socialGraph.figures.*;

public class SocialGraphFactory extends DefaultDOMFactory
{
    private final static Object[][] classTagArray = 
    {
        { DefaultDrawing.class, "SocialGraphDiagram" },
        { PersonFigure.class, "person" },
        { FriendshipFigure.class, "friendship" },
    };
    
    /** Creates a new instance. */
    @SuppressWarnings("rawtypes")
	public SocialGraphFactory() 
    {
        for (Object[] object : classTagArray)
            addStorableClass((String) object[1], (Class) object[0]);
    }
}
