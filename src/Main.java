import ui.HospitalGUI;

import javax.swing.*;

/**
 * Application entry point.
 *
 * Launches the Hospital Management System GUI on the Event Dispatch Thread (EDT),
 * as required by Swing's single-threaded model.
 */
public class Main {

    public static void main(String[] args) {
        // Enable system look and feel for best OS integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing L&F — not a fatal error
        }

        // Customise JOptionPane background/foreground to match the new
        // violet / cyan "glass" dark theme used throughout the GUI.
        UIManager.put("OptionPane.background",        new java.awt.Color(0x161F35));
        UIManager.put("Panel.background",              new java.awt.Color(0x161F35));
        UIManager.put("OptionPane.messageForeground",  java.awt.Color.WHITE);
        UIManager.put("Button.background",             new java.awt.Color(0x6C5CE7));
        UIManager.put("Button.foreground",              java.awt.Color.WHITE);

        // Launch on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new HospitalGUI());
    }
}
