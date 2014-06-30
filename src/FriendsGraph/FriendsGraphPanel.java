package FriendsGraph;

import org.jhotdraw.draw.*;
import javax.swing.*;

public class FriendsGraphPanel extends JPanel  {
    private DrawingEditor editor;
    
    /** Creates new instance. */
    public FriendsGraphPanel() {
        initComponents();
        editor = new DefaultDrawingEditor();
        editor.add(view);
        
        DefaultDrawing drawing = new DefaultDrawing();
        view.setDrawing(drawing);
    }
    
    public void setDrawing(Drawing d) {
        view.setDrawing(d);
    }
    public Drawing getDrawing() {
        return view.getDrawing();
    }
    public DrawingView getView() {
        return view;
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    
    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setViewportView(view);
        add(scrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());
        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }    
    
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
}
