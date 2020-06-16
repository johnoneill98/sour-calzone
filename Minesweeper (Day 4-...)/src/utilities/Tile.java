package utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class Tile extends JButton {
	private static final long serialVersionUID = 7037386768292692522L;
	private ImageIcon[] icons; 
	private Color unrevealedColor;
	private Color revealedColor;
	private Color textColor;
	private int value;
	private int fontSize;
	private boolean hasMine;
	private boolean isCleared;
	private boolean isFlagged;

	// Alternate constructor
	public Tile(boolean hasMine, boolean isClear, int value, boolean isFlagged, int fontSize, ImageIcon[] icons) {
		super();
		this.hasMine = hasMine;
		this.isCleared = isClear;
		this.value = value;
		this.isFlagged = isFlagged;
		this.unrevealedColor = new Color(200, 200, 200);
		this.revealedColor = new Color(255, 255, 255);
		this.textColor = new Color(25, 25, 25);
		this.fontSize = fontSize;
		if(icons!=null)
			copyIconArray(icons);
		updateStyle();
		addMouseListener(getMouseAdapater());
	}

	// Return what the text of the tile should be
	public String getClickedText() {
		if (hasMine)
			return "*";
		else if (value == 0)
			return "";
		else
			return "" + value;

	}	
	// Return what the image of the tile should be
	public ImageIcon getClickedIcon() {
		if (hasMine)
			return icons[9];
		else if (value == 0)
			return null;
		else
			return icons[value-1];
	}

	// Set the value of the tile
	public void setValue(int value) {
		this.value = value;
	}
	// Place a mine in the tile
	public void placeMine() {
		hasMine = true;
	}
	// Remove a mine from the tile
	public void removeMine() {
		hasMine = false;
	}
	// Set the tile's cleared state
	public void setCleared(boolean isClear) {
		this.isCleared = isClear;
	}
	// Set the color for an unrevealed tile
	public void setUnrevealedColor(Color c) {
		unrevealedColor = c;
	}
	// Set the color for a revealed tile
	public void setRevealedColor(Color c) {
		revealedColor = c;
	}
	// Set the color of the text
	public void setTextColor(Color c) {
		textColor = c;
	}
	// Set the flagged state
	public void setFlagged(boolean b) {
		isFlagged = b;
	}
	// Set the size of the font
	public void setFontSize(int fs) {
		fontSize=fs;
	}
	// Set the icons
	public void setIcons(ImageIcon[] i) {
		copyIconArray(i);
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
	// Get the size of the font
	public int getFontSize() {
		return fontSize;
	}
	// Get the icon array
	public ImageIcon[] getIcons() {
		ImageIcon[] tmp = new ImageIcon[icons.length];
		for(int i=0;i<tmp.length;i++)
			tmp[i]=icons[i];
		return tmp;
	}
	
	// Reveal a tile
	public void revealTile() {
		if (!isFlagged) {
			isCleared = true;
			if(icons != null)
				setIcon(getClickedIcon());
			else
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
				if (e.getButton() == 1)
					revealTile();
				// The user right clicked
				if (e.getButton() == 3)
					doFlagging();
			}
		};
	}

	// Remove or place a flag on a tile
	private void doFlagging() {
		if (!isCleared && !isFlagged) {
			if(icons!=null)
				setIcon(icons[8]);
			else
				setText("F");
			isFlagged = true;
		} else if (!isCleared && isFlagged) {
			setIcon(null);
			setText(null);
			isFlagged = false;
		}
	}

	// Apply the stylistic changes necessary
	private void updateStyle() {
		setFont(new Font("Consolas", Font.BOLD, fontSize));
		setMargin(new Insets(0, 0, 0, 0));
		setFocusable(false);
		setVerticalAlignment(SwingConstants.TOP);
		setBackground(unrevealedColor);
		setForeground(textColor);
	}

	// Copy and ImageIcon array
	private void copyIconArray(ImageIcon[] a) {
		icons = new ImageIcon[a.length];
		for(int i=0;i<icons.length;i++)
			icons[i]=a[i];
	}
}
