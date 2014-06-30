package FriendsGraph.figures;

import org.jhotdraw.draw.connector.Connector;
import java.awt.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;

public class FriendshipFigure extends LineConnectionFigure 
{
	/** Creates a new instance. */
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
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) 
    {
        if ((start.getOwner() instanceof PersonFigure) && (end.getOwner() instanceof PersonFigure)) 
        {
            PersonFigure sf = (PersonFigure) start.getOwner();
            PersonFigure ef = (PersonFigure) end.getOwner();

            // Disallow multiple connections to same person
            if (ef.getPredecessors().contains(sf))
                return false;

            // Disallow cyclic connections
            return !sf.isFriendOf(ef);
        }

        return false;
    }

    @Override
    public boolean canConnect(Connector start) 
    {
        return (start.getOwner() instanceof PersonFigure);
    }

    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleDisconnect(Connector start, Connector end) 
    {
        PersonFigure sf = (PersonFigure) start.getOwner();
        PersonFigure ef = (PersonFigure) end.getOwner();

        sf.removeFriendship(this);
        ef.removeFriendship(this);
    }

    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) 
    {
        PersonFigure sf = (PersonFigure) start.getOwner();
        PersonFigure ef = (PersonFigure) end.getOwner();

        sf.addFriendship(this);
        ef.addFriendship(this);
    }

    @Override
    public FriendshipFigure clone() 
    {
        FriendshipFigure that = (FriendshipFigure) super.clone();

        return that;
    }

    @Override
    public int getLayer() 
    {
        return 1;
    }

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
