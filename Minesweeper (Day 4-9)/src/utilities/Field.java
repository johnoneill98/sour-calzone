package utilities;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import window.GameEnd;

public class Field {
	private Tile[][] tiles;
	private ImageIcon[] icons;
	private JPanel contentPane;
	private Color unrevealedColor;
	private Color revealedColor;
	private Color textColor;
	private int fieldSize;
	private int tileSize;
	private int howManyMines;
	private int startingHeight;

	// Alternate constructor
	public Field(int fieldSize, int tileSize, int howManyMines, int startingHeight, JPanel contentPane, ImageIcon[] icons) {
		this.fieldSize = fieldSize;
		this.tileSize = tileSize;
		this.howManyMines = howManyMines;
		this.startingHeight = startingHeight;
		this.contentPane = contentPane;
		this.unrevealedColor = new Color(200, 200, 200);
		this.revealedColor = new Color(255, 255, 255);
		if(icons!=null)
			copyIconArray(icons);
		tiles = new Tile[fieldSize][fieldSize];
		initialize();
	}

	// Set the color for an unrevealed tile
	public void setUnrevealedColor(Color c) {
		unrevealedColor = c;
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				tiles[i][j].setUnrevealedColor(c);
				if (!tiles[i][j].isCleared())
					tiles[i][j].setBackground(c);
			}
		}
	}
	// Set the color for a revealed tile
	public void setRevealedColor(Color c) {
		revealedColor = c;
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				tiles[i][j].setRevealedColor(c);
				if (tiles[i][j].isCleared())
					tiles[i][j].setBackground(c);
			}
		}
	}
	// Set the color of the text
	public void setTextColor(Color c) {
		textColor = c;
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				tiles[i][j].setTextColor(c);
				tiles[i][j].setForeground(c);
			}
		}
	}
	// Set the icons
	public void setIcons(ImageIcon[] icons) {
		// Copy the icons into the array for field, or make them null
		if(icons == null)
			this.icons = null;
		else
			copyIconArray(icons);

		// For every tile on the field
		for(int i=0;i<fieldSize;i++) {
			for(int j=0;j<fieldSize;j++) {
				// Remove any current tile decoration
				tiles[i][j].setIcon(null);
				tiles[i][j].setText(null);

				// Set this tile's icons
				tiles[i][j].setIcons(icons);

				// Refresh any previously cleared tile
				if(tiles[i][j].isCleared())
					tiles[i][j].revealTile();
			}
		}
	}
	// Set the size of the field
	public void setFieldSize(int fieldSize) {
		// Clear the current field
		contentPane.removeAll();
		contentPane.repaint();
		contentPane.validate();

		// Update the data
		this.fieldSize = fieldSize;
		tiles = new Tile[fieldSize][fieldSize];
		for(int i=0;i<fieldSize;i++)
			for(int j=0;j<fieldSize;j++)
				tiles[i][j] = new Tile(false, false, 0, false, tileSize, icons);

		// Apply the colors
		setUnrevealedColor(unrevealedColor);
		setRevealedColor(revealedColor);
		setTextColor(textColor);
	}
	// Set the amount of mines on the field
	public void setHowManyMines(int howManyMines) {
		this.howManyMines = howManyMines;
		initialize();
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
	// Get the icons
	public ImageIcon[] getIcons() {
		if(icons == null)
			return null;
		ImageIcon[] tmp = new ImageIcon[icons.length];
		for(int i=0;i<tmp.length;i++)
			tmp[i]=icons[i];
		return tmp;
	}

	// Refresh the colors to be correct
	public void refreshColors() {
		// Work with each individual tile
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				// Set the colors
				tiles[i][j].setUnrevealedColor(unrevealedColor);
				tiles[i][j].setRevealedColor(revealedColor);
				tiles[i][j].setTextColor(textColor);

				// Set the background
				if(tiles[i][j].isCleared())
					tiles[i][j].setBackground(revealedColor);
				else
					tiles[i][j].setBackground(unrevealedColor);
			}
		}
		System.out.println("Colors refreshed.\n");
	}

	// Reset the field
	public void reset() {
		if(icons != null)
			randomizeIcons();
		clear();
		placeMines();
		countMines();
		System.out.println("Field Reset.\n");
	}

	// Reveal a randomly chosen tile
	public void revealRandomTile() {
		Random rand = new Random();

		// Choose a random spot on the field
		int row = rand.nextInt(fieldSize);
		int col = rand.nextInt(fieldSize);

		// Search to the left of that spot
		for (int i = row; i < fieldSize; i++) {
			for (int j = row; j < fieldSize; j++) {
				if (!tiles[i][j].hasMine() && tiles[i][j].getValue() == 0 && !tiles[i][j].isCleared()) {
					revealTile(i, j);
					return;
				}
			}
		}

		// Search to the right of that spot
		for (int i = row - 1; i >= 0; i--) {
			for (int j = col - 1; j >= 0; j--) {
				if (!tiles[i][j].hasMine() && tiles[i][j].getValue() == 0 && !tiles[i][j].isCleared()) {
					revealTile(i, j);
					return;
				}
			}
		}
	}

	// Get some statistics of this field in its current state...
	// {fieldSize, howManyMines, flaggedMines, falseFlags, percentCompleted}
	public int[] getStats(boolean overridePercentageCompleted) {
		int[] tmp = new int[5];
		
		tmp[0] = fieldSize*fieldSize;
		tmp[1] = howManyMines;
		
		for(int i=0;i<fieldSize;i++) {
			for(int j=0;j<fieldSize;j++) {
				// Determine how to count the flags
				if(tiles[i][j].hasMine() && tiles[i][j].isFlagged()) {
					tmp[4]++;
					tmp[2]++;
				}
				else if(tiles[i][j].isFlagged())
					tmp[3]++;
				
				// Determine the total tiles that were done
				if(tiles[i][j].isCleared())
					tmp[4]++;
				
			}
		}
		
		// Correct the percentageCompleted
		if(overridePercentageCompleted)
			tmp[4]=100;
		else
			tmp[4]=(int)(((double)tmp[4]/tmp[0])*100);
		
		return tmp;
	}
	
	/*
	 * Helper Methods
	 */

	// Cascade through revealing tiles
 	private void cascade(int i, int j) {
		boolean canGoNorth = true, canGoEast = true, canGoSouth = true, canGoWest = true;
		if (i == 0)
			canGoNorth = false;
		if (j == fieldSize - 1)
			canGoEast = false;
		if (i == fieldSize - 1)
			canGoSouth = false;
		if (j == 0)
			canGoWest = false;

		// Check north
		if (canGoNorth && !tiles[i - 1][j].isCleared())
			revealTile(i - 1, j);

		// Check north-east
		if (canGoNorth && canGoEast && !tiles[i - 1][j + 1].isCleared())
			revealTile(i - 1, j + 1);

		// Check east
		if (canGoEast && !tiles[i][j + 1].isCleared())
			revealTile(i, j + 1);

		// Check south-east
		if (canGoSouth && canGoEast && !tiles[i + 1][j + 1].isCleared())
			revealTile(i + 1, j + 1);

		// Check south
		if (canGoSouth && !tiles[i + 1][j].isCleared())
			revealTile(i + 1, j);

		// Check south-west
		if (canGoSouth && canGoWest && !tiles[i + 1][j - 1].isCleared())
			revealTile(i + 1, j - 1);

		// Check west
		if (canGoWest && !tiles[i][j - 1].isCleared())
			revealTile(i, j - 1);

		// Check north-west
		if (canGoNorth && canGoWest && !tiles[i - 1][j - 1].isCleared())
			revealTile(i - 1, j - 1);
	}

	// Place the mines
	private void placeMines() {
		Random rand = new Random();
		int row, col;
		for (int i = 0; i < howManyMines; i++) {
			do {
				row = rand.nextInt(fieldSize);
				col = rand.nextInt(fieldSize);
			} while (tiles[row][col].hasMine());
			tiles[row][col].placeMine();
		}
	}

	// Count the mines
	private void countMines() {
		int counter = 0;
		boolean canGoNorth, canGoEast, canGoSouth, canGoWest;

		// For each tile
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				// Initialize the data
				canGoNorth = true;
				canGoEast = true;
				canGoSouth = true;
				canGoWest = true;
				counter = 0;

				// Determine any edge positioning
				if (i == 0)
					canGoNorth = false;
				if (j == fieldSize - 1)
					canGoEast = false;
				if (i == fieldSize - 1)
					canGoSouth = false;
				if (j == 0)
					canGoWest = false;

				// Check north
				if (canGoNorth && tiles[i - 1][j].hasMine())
					counter++;

				// Check north-east
				if (canGoNorth && canGoEast && tiles[i - 1][j + 1].hasMine())
					counter++;

				// Check east
				if (canGoEast && tiles[i][j + 1].hasMine())
					counter++;

				// Check south-east
				if (canGoSouth && canGoEast && tiles[i + 1][j + 1].hasMine())
					counter++;

				// Check south
				if (canGoSouth && tiles[i + 1][j].hasMine())
					counter++;

				// Check south-west
				if (canGoSouth && canGoWest && tiles[i + 1][j - 1].hasMine())
					counter++;

				// Check west
				if (canGoWest && tiles[i][j - 1].hasMine())
					counter++;

				// Check north-west
				if (canGoNorth && canGoWest && tiles[i - 1][j - 1].hasMine())
					counter++;

				// Assign the number of discovered adjacent mines to the tile
				tiles[i][j].setValue(counter);
			}
		}
	}

	// Reveal the mines
	private void revealMines() {
		for(int i=0;i<fieldSize;i++) {
			for(int j=0;j<fieldSize;j++){
				// If the tile is flagged and has a mine, make it brighter
				if(tiles[i][j].isFlagged() && tiles[i][j].hasMine())
					tiles[i][j].setBackground(tiles[i][j].getBackground().brighter().brighter());

				// If the tile is flagged but does not have a mine, make it red
				else if(tiles[i][j].isFlagged() && !tiles[i][j].hasMine())
					tiles[i][j].setBackground(tiles[i][j].getBackground().darker().darker());
					
				// If the tile is not flagged, reveal and correct color
				else if(tiles[i][j].hasMine()) {
					revealTile(i, j);
					tiles[i][j].setBackground(unrevealedColor);
					tiles[i][j].setCleared(false);
				}
				
				// Reset the cleared state
				if(tiles[i][j].hasMine())
					tiles[i][j].setCleared(false);
			}
		}
	}

	// Check if there are any more valid moves
	private boolean checkForVictory() {
		boolean victoryByFlags=true;

		// Search the tiles for all mines flagged and only mines flagged
		for(int i = 0 ; i < fieldSize; i++)
			for(int j = 0; j < fieldSize; j++)
				if((tiles[i][j].isFlagged() && !tiles[i][j].hasMine()) || (!tiles[i][j].isFlagged() && tiles[i][j].hasMine()))
					victoryByFlags = false;

		// If all tiles passed the test, return true
		if(victoryByFlags)
			return true;

		// Search the tiles, returning false if any are hidden but do not have a mine
		for (int i = 0; i < fieldSize; i++)
			for (int j = 0; j < fieldSize; j++)
				if (!tiles[i][j].isCleared() && !tiles[i][j].hasMine())
					return false;

		// If you get here, then the only hidden tiles also have mines
		return true;
	}

	// Reveal a tile
	private boolean revealTile(int i, int j) {
		// Reveal the individual tile
		tiles[i][j].revealTile();

		// Cascade as necessary
		if (!tiles[i][j].hasMine() && tiles[i][j].getValue() == 0)
			cascade(i, j);

		// Return if this tile contained a mine
		return tiles[i][j].hasMine();
	}

	// Remove the mines and counts
	private void clear() {
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				tiles[i][j].removeMine();
				tiles[i][j].setValue(0);
				tiles[i][j].setCleared(false);
				tiles[i][j].setFlagged(false);
				tiles[i][j].setBackground(unrevealedColor);
				tiles[i][j].setText(null);
				tiles[i][j].setIcon(null);
			}
		}
	}

	// Rearrange the icons to be random
	private void randomizeIcons() {
		if (icons == null)
			return;

		System.out.println("Randomizing Icons");
		int newSlot=0, size=icons.length-2;
		Random rand = new Random();
		ImageIcon[] tmp = new ImageIcon[size];

		// Swap up the icon locations in a temporary array
		for(int i=0;i<size;i++) {
			do {
				newSlot = rand.nextInt(size);
			} while(tmp[newSlot]!=null);
			tmp[newSlot]=icons[i];
		}

		// Move the new icons into the actual array
		for(int i=0;i<size;i++)
			icons[i]=tmp[i];

		// Update the icons for each tile
		for(int i=0;i<tiles.length;i++)
			for(int j=0;j<tiles.length;j++)
				tiles[i][j].setIcons(icons);
	}

	// Copy and ImageIcon array
	private void copyIconArray(ImageIcon[] a) {
		icons = new ImageIcon[a.length];
		for(int i=0;i<icons.length;i++)
			icons[i]=a[i];
	}

	// Initialize the field
	private void initialize() {
		// Work with each individual tile
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				// Create the tile
				tiles[i][j] = new Tile(false, false, 0, false, tileSize, icons);

				// Set the bounds of the tile
				tiles[i][j].setBounds(10 + (i * tileSize), 11 + (j * tileSize) + startingHeight, tileSize, tileSize);

				// Set the colors
				tiles[i][j].setUnrevealedColor(unrevealedColor);
				tiles[i][j].setRevealedColor(revealedColor);
				tiles[i][j].setTextColor(textColor);

				// Add the mouse listener
				tiles[i][j].addMouseListener(getMouseAdapater(i, j));

				// Add the tile to the content pane
				contentPane.add(tiles[i][j]);
			}
		}

		// Deal with the mines and icons
		placeMines();
		countMines();

		// Randomize the icons
		randomizeIcons();
	}

	// Return the listener necessary for clicking
	private MouseAdapter getMouseAdapater(int i, int j) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				// The user left clicked
				if (e.getButton() == 1 && !tiles[i][j].isFlagged()) {
					// The user revealed a mine
					if (revealTile(i, j)) {
						// Reveal how to player did
						revealMines();

						// Display the message
						GameEnd.display(contentPane, "You awoke an evil spirit!", "Failure", "resources\\endGameImages\\failure.png",
								new Color[] {revealedColor, textColor, unrevealedColor}, getStats(false),
								new String[] {"Total tiles", "Total mines", "Flagged mines", "False flags", "Percentage Completed"});

						// Restart the game
						reset();
					}
				}

				// The user has won
				if(checkForVictory()) {
					GameEnd.display(contentPane, "You cleared the field!", "victory", "resources\\endGameImages\\victory.png",
							new Color[] {revealedColor, textColor, unrevealedColor}, getStats(true),
							new String[] {"Total tiles", "Total mines", "Flagged mines", "False flags", "Percentage Completed"});
					reset();
				}
			}
		};
	}
}
