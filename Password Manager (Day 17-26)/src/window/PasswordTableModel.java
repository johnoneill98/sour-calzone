package window;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PasswordTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -5490939282473098979L;

	// The alternate constructor
	public PasswordTableModel(Object[] columns, int rowCount) {
		super(columns, rowCount);
	}

	// Disable editing of cells
	public boolean isCellEditable(int row, int column){  
		return false;
	}

	// Return the listener for revealing a password
	public static MouseAdapter getMouseListenerPressed(JTable table, String[][] tableEntries, JLabel message) {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				// The row and column clicked
				int row = table.rowAtPoint(me.getPoint());
				int correctedRow = 0;

				// Right click, copy password
				if(SwingUtilities.isRightMouseButton(me)) {
					// Find the proper row in tableEntries
					while(!(tableEntries[correctedRow][0].equals(table.getValueAt(row, 0)) && tableEntries[correctedRow][1].equals(table.getValueAt(row, 1))))
						correctedRow++;

					// Copy password
					StringSelection stringSelection = new StringSelection(tableEntries[correctedRow][2]);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
					
					// Tell the user
					message.setText(tableEntries[correctedRow][1]+"'s password for "+tableEntries[correctedRow][0]+" copied to clipboard.");
				}

				// Left click on password column, reveal password
				else if(SwingUtilities.isLeftMouseButton(me)) {
					try {
						// Find the proper row in tableEntries
						while(!tableEntries[correctedRow][0].equals(table.getValueAt(row, 0)) || !tableEntries[correctedRow][1].equals(table.getValueAt(row, 1)))
							correctedRow++;

						// Change the value
						table.setValueAt(tableEntries[correctedRow][2], row, 2);
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("--Could not find password--");
					}
				} 
			}
		};
	}

	// Return the listener for hiding the passwords
	public static MouseAdapter getMouseListenerReleased(JTable table, String[][] tableEntries, char hiddenChar) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				// Create the string
				String hidden = "";
				for(int j = 0; j < 10; j++)
					hidden = hidden + hiddenChar+"";
				
				// Hide every password
				for(int i = 0; i < table.getRowCount(); i++)
					table.setValueAt(hidden, i, 2);

				// Deselect the cell
				table.clearSelection();
			}
		};
	}

	// Return the listener for populating text fields with the selected row
	public static MouseAdapter fillTextFieldListener(JTable table, JTextField title, JTextField username, JTextField password) {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				// The row and column clicked
				int row = table.rowAtPoint(me.getPoint());

				// Left click on password column, reveal password
				if(SwingUtilities.isLeftMouseButton(me)) {
					title.setText((String) table.getValueAt(row, 0));
					username.setText((String) table.getValueAt(row, 1));
					password.setText((String) table.getValueAt(row, 2));
				} 
			}
		};
	}
}
