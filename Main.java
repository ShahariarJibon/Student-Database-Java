import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry point for the Student Information Management System.
 * 
 * @author JIBON
 * @version 1.0
 */
public class Main {
    
    /**
     * Main method - entry point of the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainGUI();
        });
    }
}