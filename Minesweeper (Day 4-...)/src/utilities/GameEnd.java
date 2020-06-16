package utilities;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class GameEnd {
	private static final int buttons = JOptionPane.CANCEL_OPTION;
	private static final int messageType = JOptionPane.INFORMATION_MESSAGE;
	private static final Object[] options = { "Play Again" };
	
	public static void display(Component contentPane, Object message, String title, String iconPath, Color buttonBack, Color foreground, Color window) {
		// Deal with the colors
		UIManager.put("Button.background", buttonBack);
		UIManager.put("Button.foreground", foreground);
		UIManager.put("Panel.background", window);
		UIManager.put("OptionPane.background", window);
		UIManager.put("OptionPane.messageForeground", foreground);
		
		// Get the icon
		ImageIcon icon = new ImageIcon(iconPath);
		
		JOptionPane.showOptionDialog(contentPane, message, title, buttons, messageType, icon, options, null);
		System.out.println("Starting new game.");
	}
}
