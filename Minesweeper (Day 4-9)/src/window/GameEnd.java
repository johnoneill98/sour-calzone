package window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class GameEnd {

	private static Object[] options = new Object[] { "Play Again" };
	private final static Font messageFont = new Font("Consolas", Font.BOLD, 18);
	private final static Font statsFont = new Font("Consolas",Font.PLAIN, 13);
	
	
	public static void display(Component contentPane, Object message, String title, String iconPath, Color[] colors, int[] stats, String[] statNames) {		
		// Deal with the colors
		UIManager.put("Button.background", colors[0]);
		UIManager.put("Button.foreground", colors[1]);
		UIManager.put("Panel.background", colors[2]);
		UIManager.put("OptionPane.background", colors[2]);

		// Get the icon
		ImageIcon icon = new ImageIcon(iconPath);

		// Create a panel
		JPanel panel = new JPanel();
		panel.setLayout(null);

		// Create a label with the message
		JLabel messageLabel = new JLabel((String)message);
		messageLabel.setForeground(colors[1]);
		messageLabel.setBounds(0, 0, 500, 25);
		messageLabel.setFont(messageFont);
		panel.add(messageLabel);
		
		// Create the statistics labels
		JLabel[] statLabels = new JLabel[stats.length];
		for(int i=0;i<statLabels.length;i++) {
			statLabels[i] = new JLabel(statNames[i]+": "+stats[i]);
			statLabels[i].setForeground(colors[1]);
			statLabels[i].setFont(statsFont);
			statLabels[i].setBounds(0, 15*(i+1), 500, 25);
			panel.add(statLabels[i]);
		}
		statLabels[4].setText(statLabels[4].getText()+"%");
		

		// Create the pane
		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, icon, options, null);

		// Create the dialog
		JDialog dialog = new JDialog(new JFrame(), title, true);
		dialog.setContentPane(pane);
		dialog.setBounds(800, 400, 365, 200);
		dialog.setIconImage(new ImageIcon("resources\\windowImage.png").getImage());

		// Add the listener so the button works
		pane.addPropertyChangeListener(new PropertyChangeListener () {
			public void propertyChange(PropertyChangeEvent e) {
				dialog.setVisible(false);
			}
		});
		
		dialog.setVisible(true);

		System.out.println("Starting new game.");
	}
}
