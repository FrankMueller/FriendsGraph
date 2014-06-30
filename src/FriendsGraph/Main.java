package FriendsGraph;

import org.jhotdraw.app.*;

public class Main
{    
    /** Creates a new instance. */
    public static void main(String[] args)
    {
        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac"))
            app = new OSXApplication();
        else
            app = new SDIApplication();
        
        DefaultApplicationModel model = new FriendsGraphApplicationModel();
        model.setName("JHotDraw FriendsGraph");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2010 (c) by the authors of JHotDraw and all its contributors.\n" +
                "This software is licensed under LGPL and Creative Commons 3.0 Attribution.");
        model.setViewClassName("FriendsGraph.FriendsGraphView");
        app.setModel(model);
        app.launch(args);
    }
    
}
