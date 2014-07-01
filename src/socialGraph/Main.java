package socialGraph;

import org.jhotdraw.app.*;

public class Main
{    
    /** Creates a new instance. */
    public static void main(String[] args)
    {
        Application application;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac"))
            application = new OSXApplication();
        else
            application = new SDIApplication();
        
        DefaultApplicationModel model = new SocialGraphApplicationModel();
        application.setModel(model);
        application.launch(args);
    }
    
}
