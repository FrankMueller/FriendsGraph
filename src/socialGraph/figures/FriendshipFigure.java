package socialGraph.figures;

import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.Connector;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * A figure which represents a friendship in the friends graph.
 */
public class FriendshipFigure extends LineConnectionFigure 
{
	private static final long serialVersionUID = 748604626072325745L;

	/**
	 *  Creates a new instance. 
	 */
    public FriendshipFigure() 
    {
        set(STROKE_COLOR, new Color(0x000000));
        set(STROKE_WIDTH, 1d);

        setAttributeEnabled(END_DECORATION, false);
        setAttributeEnabled(START_DECORATION, false);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINE, false);
    }

    /**
     * Checks if a friendship can be created between the two specified <code>Connector</code>s.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) 
    {
    	//If both connectors refer to a PersonFigure then check if both persons can be connected otherwise a connection is not valid
        if ((start.getOwner() instanceof PersonFigure) && (end.getOwner() instanceof PersonFigure)) 
        {
        	//Get the two persons to connect
            PersonFigure personA = (PersonFigure) start.getOwner();
            PersonFigure personB = (PersonFigure) end.getOwner();

            //If the persons are not already connected then allow the connection otherwise not
            return !personB.getFriends().contains(personA);
        }
        else
        	return false;
    }

    /**
     * Checks if a friendship can be created for the specified <code>Connector</code>.
     */
    @Override
    public boolean canConnect(Connector start) 
    {
        return (start.getOwner() instanceof PersonFigure);
    }

    /**
     * Handles the disconnection of a connection.
     */
    @Override
    protected void handleDisconnect(Connector start, Connector end) 
    {
        PersonFigure personA = (PersonFigure) start.getOwner();
        PersonFigure personB = (PersonFigure) end.getOwner();

        personA.removeFriendship(this);
        personB.removeFriendship(this);
    }

    /**
     * Handles the connection of a connection.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) 
    {
        PersonFigure personA = (PersonFigure) start.getOwner();
        PersonFigure personB = (PersonFigure) end.getOwner();

        personA.addFriendship(this);
        personB.addFriendship(this);
    }

    /**
     * @see org.jhotdraw.draw.LineConnectionFigure#clone()
     */
    @Override
    public FriendshipFigure clone() 
    {
        return (FriendshipFigure) super.clone();
    }

    /**
     * @see org.jhotdraw.draw.AbstractFigure#getLayer()
     */
    @Override
    public int getLayer() 
    {
        return 1;
    }

    /**
     * @see org.jhotdraw.draw.LineConnectionFigure#removeNotify(org.jhotdraw.draw.Drawing)
     */
    @Override
    public void removeNotify(Drawing d) 
    {
        if (getStartFigure() != null)
            ((PersonFigure) getStartFigure()).removeFriendship(this);
        
        if (getEndFigure() != null)
            ((PersonFigure) getEndFigure()).removeFriendship(this);

        super.removeNotify(d);
    }
}
