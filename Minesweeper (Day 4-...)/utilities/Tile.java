package utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class Tile extends JButton {
	private static final long serialVersionUID = 7037386768292692522L;
	private boolean hasMine;
	private boolean isCleared;
	private boolean isFlagged;
	private int value;
	private Color unrevealedColor;
	private Color revealedColor;
	private Color textColor;

	// Alternate constructor
	public Tile (boolean hasMine, boolean isClear, int value, boolean isFlagged) {
		super();
		this.hasMine=hasMine;
		this.isCleared=isClear;
		this.value=value;
		this.isFlagged=isFlagged;
		this.unrevealedColor=new Color(200, 200, 200);
		this.revealedColor=new Color (255, 255, 255);
		this.textColor=new Color(25, 25, 25);
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
		this.isCleared=isClear;
	}
	// Set the color for an unrevealed tile
	public void setUnrevealedColor(Color c) {
		unrevealedColor=c;
	}
	// Set the color for a revealed tile
	public void setRevealedColor(Color c) {
		revealedColor=c;
	}
	// Set the color of the text
	public void setTextColor(Color c) {
		textColor=c;
	}
	// Set the flagged state
	public void setFlagged(boolean b) {
		isFlagged=b;
	}
	
	// Get if the tile has a mine
	public boolean hasMine() {
		return hasMine;
	}
	// Get if the tile has been cleared
	public boolean isCleared() {
		return isCleared;
	}
	// Get the value of the tile
	public int getValue() {
		return value;
	}
	// Get the color for an unrevealed tile
	public Color getUnrevealedColor() {
		return unrevealedColor;
	}
	// Get the color for a revealed tile
	public Color getRevealedColor() {
		return revealedColor;
	}
	// Get the color of the text
	public Color getTextColor() {
		return textColor;
	}
	// Get if a tile is flagged
	public boolean isFlagged() {
		return isFlagged;
	}
	
	// Reveal a tile
	public void revealTile() {
		if(!isFlagged) {
			isCleared=true;
			setText(getClickedText());
			setBackground(revealedColor);
		}
	}

	/*
	 * Helper Methods
	 */

	// Return the listener necessary for clicking
	private MouseAdapter getMouseAdapater() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// The user left clicked
				if(e.getButton()==1)
					revealTile();
				// The user right clicked
				if(e.getButton()==3)
					doFlagging();
			}
		};
	}

	protected void doFlagging() {
		if(!isCleared && !isFlagged) {
			this.setText("F");
			isFlagged=true;
		}
		else if(!isCleared && isFlagged) {
			this.setText(null);
			isFlagged=false;
		}
	}

	// Apply the stylistic changes necessary
	private void updateStyle() {
		setFont(new Font("Consolas", Font.BOLD, 15));
		setMargin(new Insets(0, 0, 0, 0));
		setFocusable(false);
		setVerticalAlignment(SwingConstants.TOP); 
		setBackground(unrevealedColor);
		setForeground(textColor);
	}
}
