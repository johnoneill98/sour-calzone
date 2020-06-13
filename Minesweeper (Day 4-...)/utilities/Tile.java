package utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Tile extends JButton {
	boolean hasMine;
	boolean isClear;
	int value;
	
	// Default constructor
	public Tile () {
		super();
		updateStyle();
		addMouseListener(getMouseAdapater());
	}
	
	// Alternate constructor
	public Tile (boolean hasMine, boolean isClear, int value) {
		super();
		this.hasMine=hasMine;
		this.isClear=isClear;
		this.value=value;
		updateStyle();
		addMouseListener(getMouseAdapater());
	}
	
	// Return what the text of the tile should be
	public String getClickedText() {
		if(hasMine)
			return "*";
		else if(value==0)
			return "";
		else
			return ""+value;
		
	}
	
	// Set the value of the tile
	public void setValue(int value) {
		this.value=value;
	}
	// Place a mine in the tile
	public void placeMine() {
		hasMine=true;
	}
	// Remove a mine from the tile
	public void removeMine() {
		hasMine=false;
	}
	// Set the tile's cleared state
	public void setCleared(boolean isClear) {
		this.isClear=isClear;
	}
	
	// Get if the tile has a mine
	public boolean hasMine() {
		return hasMine;
	}
	// Get if the tile has been cleared
	public boolean isClear() {
		return isClear;
	}
	// Get the value of the tile
	public int getValue() {
		return value;
	}
	
	/*
	 * Helper Methods
	 */
	
	// Return the listener necessary for clicking
 	private MouseAdapter getMouseAdapater() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!isClear) {
					revealTile();
				}
			}
		};
	}
	
	// Apply the stylistic changes necessary
	private void updateStyle() {
		setFont(new Font("Consolas", Font.BOLD, 15));
		setMargin(new Insets(0, 0, 0, 0));
		setFocusable(false);
		setVerticalAlignment(SwingConstants.TOP); 
		setBackground(new Color(200, 200, 200));
	}

	// Reveal a tile - Will probably be recursive once the cascading is implemented
	private void revealTile() {
		isClear=true;
		setText(getClickedText());
		setBackground(new Color(255, 255, 255));
	}
}
