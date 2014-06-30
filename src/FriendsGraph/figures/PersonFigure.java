package FriendsGraph.figures;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.handle.ConnectorHandle;

import java.io.IOException;
import java.awt.geom.*;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.util.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;

public class PersonFigure extends GraphicalCompositeFigure {

    private HashSet<FriendshipFigure> friendships;

    private static class NameAdapter extends FigureAdapter {

        private PersonFigure target;

        public NameAdapter(PersonFigure target)
        {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent e)
        {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("name", e.getOldValue(), e.getNewValue());
        }
    }

    private static class DurationAdapter extends FigureAdapter
    {
        private PersonFigure target;

        public DurationAdapter(PersonFigure target) 
        {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent evt) 
        {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
            for (PersonFigure succ : target.getSuccessors())
                succ.updateStartTime();
        }
    }

    /** Creates a new instance. */
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

        TextFigure nameFigure;
        nameCompartment.add(nameFigure = new TextFigure());
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);

        TextFigure durationFigure;
        attributeCompartment.add(durationFigure = new TextFigure());
        durationFigure.set(FONT_BOLD, true);
        durationFigure.setText("0");
        durationFigure.setAttributeEnabled(FONT_BOLD, false);

        TextFigure startTimeFigure;
        attributeCompartment.add(startTimeFigure = new TextFigure());
        startTimeFigure.setEditable(false);
        startTimeFigure.setText("0");
        startTimeFigure.setAttributeEnabled(FONT_BOLD, false);

        setAttributeEnabled(STROKE_DASHES, false);

        ResourceBundleUtil labels =
                ResourceBundleUtil.getBundle("Labels");

        setName(labels.getString("FriendsGraph.person.defaultName"));
        setDuration(0);

        friendships = new HashSet<FriendshipFigure>();
        nameFigure.addFigureListener(new NameAdapter(this));
        durationFigure.addFigureListener(new DurationAdapter(this));
    }

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
                ch.setToolTipText("Drag the connector to a friend.");
                break;
        }
        return handles;
    }

    public void setName(String newValue) 
    {
        getNameFigure().setText(newValue);
    }

    public String getName() 
    {
        return getNameFigure().getText();
    }

    public void setDuration(int newValue) 
    {
        int oldValue = getDuration();
        getDurationFigure().setText(Integer.toString(newValue));
        if (oldValue != newValue) 
        {
            for (PersonFigure succ : getSuccessors())
                succ.updateStartTime();
        }
    }

    public int getDuration() 
    {
        try 
        {
            return Integer.valueOf(getDurationFigure().getText());
        }
        catch (NumberFormatException e) 
        {
            return 0;
        }
    }

    public void updateStartTime() 
    {
        willChange();
        int oldValue = getStartTime();
        int newValue = 0;
        for (PersonFigure pre : getPredecessors()) 
            newValue = Math.max(newValue, pre.getStartTime() + pre.getDuration());

        getStartTimeFigure().setText(Integer.toString(newValue));
        if (newValue != oldValue) 
        {
            for (PersonFigure succ : getSuccessors()) 
            {
                // The if-statement here guards against cyclic friendships. 
                if (!this.isFriendOf(succ))
                    succ.updateStartTime();
            }
        }
        changed();
    }

    public int getStartTime() 
    {
        try 
        {
            return Integer.valueOf(getStartTimeFigure().getText());
        }
        catch (NumberFormatException e) 
        {
            return 0;
        }
    }

    private TextFigure getNameFigure() 
    {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }

    private TextFigure getDurationFigure()
    {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(0);
    }

    private TextFigure getStartTimeFigure()
    {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(1);
    }

    @Override
    public PersonFigure clone() 
    {
        PersonFigure that = (PersonFigure) super.clone();
        that.friendships = new HashSet<FriendshipFigure>();
        that.getNameFigure().addFigureListener(new NameAdapter(that));
        that.getDurationFigure().addFigureListener(new DurationAdapter(that));
        that.updateStartTime();
        return that;
    }

    @Override
    public void read(DOMInput in) throws IOException 
    {
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        in.openElement("model");
        in.openElement("name");
        setName((String) in.readObject());
        in.closeElement();
        in.openElement("duration");
        setDuration((Integer) in.readObject());
        in.closeElement();
        in.closeElement();
    }

    @Override
    public void write(DOMOutput out) throws IOException 
    {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        writeAttributes(out);
        out.openElement("model");
        out.openElement("name");
        out.writeObject(getName());
        out.closeElement();
        out.openElement("duration");
        out.writeObject(getDuration());
        out.closeElement();
        out.closeElement();
    }

    @Override
    public int getLayer() 
    {
        return 0;
    }

    public Set<FriendshipFigure> getFriendships() 
    {
        return Collections.unmodifiableSet(friendships);
    }

    public void addFriendship(FriendshipFigure f) 
    {
        friendships.add(f);
        updateStartTime();
    }

    public void removeFriendship(FriendshipFigure f) 
    {
        friendships.remove(f);
        updateStartTime();
    }

    /**
     * Returns persons which are directly connected via a friendship to this person.
     */
    public List<PersonFigure> getSuccessors() 
    {
        LinkedList<PersonFigure> list = new LinkedList<PersonFigure>();
        for (FriendshipFigure c : getFriendships())       
        {
            if (c.getStartFigure() == this) 
                list.add((PersonFigure) c.getEndFigure());
        }

        return list;
    }

    /**
     * Returns predecessor person which are directly connected via a
     * friendship to this person.
     */
    public List<PersonFigure> getPredecessors() 
    {
        LinkedList<PersonFigure> list = new LinkedList<PersonFigure>();
        for (FriendshipFigure c : getFriendships()) 
        {
            if (c.getEndFigure() == this)
                list.add((PersonFigure) c.getStartFigure());
        }

        return list;
    }

    /**
     * Returns true, if the current person is a direct or indirect friend of the specified person.
     * If the dependency is cyclic, then this method returns true
     * if <code>this</code> is passed as a parameter and for every other
     * task in the cycle.
     */
    public boolean isFriendOf(PersonFigure person)
    {
        if (this == person)
            return true;

        for (PersonFigure pre : getPredecessors()) 
        {
            if (pre.isFriendOf(person))
                return true;
        }
        
        return false;
    }

    @Override
    public String toString()
    {
        return "PersonFigure#" + hashCode() + " " + getName() + " " + getDuration() + " " + getStartTime();
    }
}

