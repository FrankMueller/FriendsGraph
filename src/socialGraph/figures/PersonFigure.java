package socialGraph.figures;

import java.util.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.ConnectorHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.util.*;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * A figure which represents a person in the friends graph.
 */
public class PersonFigure extends GraphicalCompositeFigure 
{
	private static final long serialVersionUID = -7525019931278322586L;
	private static final int coverageDepthLimit = 1;
	private HashSet<FriendshipFigure> friendships;  
    
    private static class RejectEmptyNameAdapter extends FigureAdapter
    {
        private PersonFigure target;

        public RejectEmptyNameAdapter(PersonFigure target)
        {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent e) 
        {        	
        	//If the name was changed to an empty string, then reset the name to the value before the edit
        	String newValue = (String)e.getNewValue();
        	newValue = newValue.trim();
System.out.println("'" + newValue + "'");
        	if (newValue.isEmpty())
        		 target.setLastName((String)e.getOldValue());
        }
    }

	
    /**
     * Initializes a new instance.
     */
    public PersonFigure() 
    {
        super(new RoundRectangleFigure());

        setLayouter(new VerticalLayouter());

        RectangleFigure nameCompartmentPF = new RectangleFigure();
        nameCompartmentPF.set(STROKE_COLOR, null);
        nameCompartmentPF.setAttributeEnabled(STROKE_COLOR, false);
        nameCompartmentPF.set(FILL_COLOR, null);
        nameCompartmentPF.setAttributeEnabled(FILL_COLOR, false);
        ListFigure nameCompartment = new ListFigure(nameCompartmentPF);
        ListFigure attributeCompartment = new ListFigure();
        SeparatorLineFigure separator1 = new SeparatorLineFigure();

        add(nameCompartment);
        add(separator1);
        add(attributeCompartment);

        Insets2D.Double insets = new Insets2D.Double(4, 8, 4, 8);
        nameCompartment.set(LAYOUT_INSETS, insets);
        attributeCompartment.set(LAYOUT_INSETS, insets);

        TextFigure firstNameFigure;
        nameCompartment.add(firstNameFigure = new TextFigure());
        firstNameFigure.set(FONT_BOLD, false);
        firstNameFigure.setAttributeEnabled(FONT_BOLD, false);

        TextFigure lastNameFigure;
        nameCompartment.add(lastNameFigure = new TextFigure());
        lastNameFigure.set(FONT_BOLD, true);
        lastNameFigure.setAttributeEnabled(FONT_BOLD, false);
        lastNameFigure.addFigureListener(new RejectEmptyNameAdapter(this));

        TextFigure friendCountFigure;
        attributeCompartment.add(friendCountFigure = new TextFigure());
        friendCountFigure.set(FONT_BOLD, true);
        friendCountFigure.setEditable(false);
        friendCountFigure.setText("0");
        friendCountFigure.setAttributeEnabled(FONT_BOLD, false);
        
        TextFigure coverageFigure;
        attributeCompartment.add(coverageFigure = new TextFigure());
        coverageFigure.setEditable(false);
        coverageFigure.setText("0");
        coverageFigure.setAttributeEnabled(FONT_BOLD, false);

        setAttributeEnabled(STROKE_DASHES, false);

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("Labels");

        setFirstName(labels.getString("SocialGraph.person.defaultFirstName"));
        setLastName(labels.getString("SocialGraph.person.defaultLastName"));

        friendships = new HashSet<FriendshipFigure>();
    }

    /** 
     * @see org.jhotdraw.draw.GraphicalCompositeFigure#createHandles(int)
     */
    @Override
    public Collection<Handle> createHandles(int detailLevel) 
    {
        java.util.List<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) 
        {
            case -1:
                handles.add(new BoundsOutlineHandle(getPresentationFigure(), false, true));
                break;
            case 0:
                handles.add(new MoveHandle(this, RelativeLocator.northWest()));
                handles.add(new MoveHandle(this, RelativeLocator.northEast()));
                handles.add(new MoveHandle(this, RelativeLocator.southWest()));
                handles.add(new MoveHandle(this, RelativeLocator.southEast()));
                ConnectorHandle ch;
                handles.add(ch = new ConnectorHandle(new LocatorConnector(this, RelativeLocator.east()), new FriendshipFigure()));
                ch.setToolTipText("Drag the connector to a person.");
                break;
        }
        return handles;
    }

    /**
     * Sets the first name of the person represented by this instance.
     * 
     * @param The first name.
     */
    public void setFirstName(String newValue) 
    {
        getFirstNameFigure().setText(newValue);
    }

    /**
     * Gets the first name of the person represented by this instance.
     * 
     * @return The first name.
     */
    public String getFirstName() 
    {
        return getFirstNameFigure().getText();
    }

    /**
     * Sets the last name of the person represented by this instance.
     * 
     * @param The last name.
     */
    public void setLastName(String newValue) 
    {
        getLastNameFigure().setText(newValue);
    }

    /**
     * Gets the last name of the person represented by this instance.
     * 
     * @return The last name.
     */
    public String getLastName() 
    {
        return getLastNameFigure().getText();
    }

    /**
     * Sets the number of friends of the person represented by this instance and recomputes
     * the coverage of this instance and all persons within coverage.
     * 
     * @param The number of friends.
     */
    private void setFriendCount(int newValue)
    {
    	if (getFriendCount() != newValue)
    	{
    		//Set the new value
    		willChange();
    		getFriendCountFigure().setText(Integer.toString(newValue));
    		changed();

    		//Update the displayed coverage
    		recomputeCoverage();
    		
    		//Update the displayed coverage of all 
            List<PersonFigure> coverageList = new LinkedList<PersonFigure>();
            appendFriendsWithinCoverageToList(coverageList, 0);
    		for	(PersonFigure friend : coverageList)
    			friend.recomputeCoverage();
    	}
    }
    
    /**
     * Gets the number of friends of the person represented by this instance.
     * 
     * @return The number of friends.
     */
    public int getFriendCount() 
    {
        try 
        {
            return Integer.valueOf(getFriendCountFigure().getText());
        }
        catch (NumberFormatException e) 
        {
            return 0;
        }
    }

    /**
     * Sets the coverage of the person represented by this instance.
     * 
     * @param The coverage.
     */
    private void setCoverage(int newValue) 
    {
    	if (getCoverage() != newValue)
    	{
    		willChange();
    		getCoverageFigure().setText(Integer.toString(newValue));
    		changed();
    	}
    }

    /**
     * Gets the coverage of the person represented by this instance.
     * 
     * @return The coverage.
     */
    public int getCoverage() 
    {
        try 
        {
            return Integer.valueOf(getCoverageFigure().getText());
        }
        catch (NumberFormatException e) 
        {
            return 0;
        }
    }

    /**
     * Recomputes the coverage and updates the displayed coverage value.
     */
    private void recomputeCoverage() 
    {
        List<PersonFigure> coverageList = new LinkedList<PersonFigure>();
        appendFriendsWithinCoverageToList(coverageList, 0);
        coverageList.remove(this);
        
        setCoverage(coverageList.size());
    }
    
    /**
     * Appends all friends of the person represented by this instance to the specified list of person 
     * {@code PersonFigure} who are within the coverage range starting from the current coverage level.
     * 
     * @param The list of {@code PersonFigure} to append person within the coverage range to
     * @param The current depth of the coverage search (recursion depth)
     */
    private void appendFriendsWithinCoverageToList(List<PersonFigure> coverageList, int coverageDepth)
    {
        for (PersonFigure friend : getFriends())
        {
        	//Add friends which are not already in the list
        	if (!coverageList.contains(friend))
    			coverageList.add(friend);

        	//If the next hierarchy level is still within coverage range then append the friends of the current friend too 
        	if (coverageDepth < coverageDepthLimit)
        		friend.appendFriendsWithinCoverageToList(coverageList, coverageDepth + 1);
        }    
    }

    /**
     * Gets the TextFigure which displays the first name of the person represented by this instance.
     *  
     * @return The TextFigure which displays the first name of the person.
     */
    private TextFigure getFirstNameFigure() 
    {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }

    /**
     * Gets the TextFigure which displays the last name of the person represented by this instance.
     *  
     * @return The TextFigure which displays the last name of the person.
     */
    private TextFigure getLastNameFigure() 
    {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(1);
    }

    /**
     * Gets the TextFigure which displays the number of friends of the person represented by this instance.
     *  
     * @return The TextFigure which displays the number of friends of the person.
     */
    private TextFigure getFriendCountFigure()
    {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(0);
    }

    /**
     * Gets the TextFigure which displays the coverage of the person represented by this instance.
     *  
     * @return The TextFigure which displays the coverage of the person.
     */
    private TextFigure getCoverageFigure()
    {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(1);
    }

    /**
     * @see org.jhotdraw.draw.GraphicalCompositeFigure#clone()
     */
    @Override
    public PersonFigure clone() 
    {
        PersonFigure clone = (PersonFigure) super.clone();
        clone.friendships = new HashSet<FriendshipFigure>();
        clone.getLastNameFigure().addFigureListener(new RejectEmptyNameAdapter(clone));

        return clone;
    }

    /**
     * @see org.jhotdraw.draw.AbstractFigure#getLayer()
     */
    @Override
    public int getLayer() 
    {
        return 0;
    }

    /**
     * Gets the friendships of the person represented by this instance.
     * 
     * @return An unmodifiable set containing the friendships of this person.
     */
    public Set<FriendshipFigure> getFriendships() 
    {
        return Collections.unmodifiableSet(friendships);
    }

    /**
     * Adds a friendship to the friendship collection of the person represented by this instance.
     * 
     * @param The friendship to add.
     */
    public void addFriendship(FriendshipFigure friendship) 
    {
    	//Add the friendship
        friendships.add(friendship);
        
        //Update the friend count property of this person (This updates the coverage too - indirectly)
        setFriendCount(getFriendships().size());
    }

    /**
     * Removes a friendship from the friendship collection of the person represented by this instance.
     * 
     * @param The friendship to remove.
     */
    public void removeFriendship(FriendshipFigure friendship) 
    {
    	//Remove the friendship
        friendships.remove(friendship);
        
        //Update the friend count property of this person (This updates the coverage too - indirectly)
        setFriendCount(getFriendships().size());
    }

    /**
     * Returns persons which are directly connected via a friendship to the person represented by this instance.
     */
    public List<PersonFigure> getFriends() 
    {
    	//Initialize a list where we can put the friends into
        LinkedList<PersonFigure> friendsList = new LinkedList<PersonFigure>();
        
        //Get the friend on the other side of the friendship connection and add it to the friends list
        for (FriendshipFigure friendShip : getFriendships())       
        {
            if (friendShip.getStartFigure() == this) 
                friendsList.add((PersonFigure) friendShip.getEndFigure());
            else
            	friendsList.add((PersonFigure) friendShip.getStartFigure());
        }

        return friendsList;
    }

    /**
     * Gets a value indicating whether the specified person is a direct or indirect friend of the person represented by this instance.
     * 
     * @param The person to check if it relates to this person.
     */
    public boolean isFriendOf(PersonFigure person)
    {
    	//If the specified person is this person then return true 
        if (this == person)
            return true;

        //Check the the person is a direct or indirect friend recursively
        for (PersonFigure friend : getFriends()) 
        {       	
            if (friend == person && friend.isFriendOf(person))
                return true;
        }
        
        return false;
    }

    /** 
	 * Returns a string representing this instance.
	 * 
	 * @return The string representation.
     */
    @Override
    public String toString()
    {
        return "PersonFigure#" + hashCode() + " " + getFirstName() + " " + getLastName() + " " + getFriendCount() + " " + getCoverage();
    }
}

