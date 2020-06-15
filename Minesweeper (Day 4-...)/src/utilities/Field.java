package utilities;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;

public class Field {
	private Tile[][] tiles;
	private JPanel contentPane;
	private Color unrevealedColor;
	private Color revealedColor;
	private Color textColor;
	private int fieldSize;
	private int tileSize;
	private int howManyMines;
	private int startingHeight;

	// Alternate constructor
	public Field(int fieldSize, int tileSize, int howManyMines, int startingHeight, JPanel contentPane) {
		this.fieldSize = fieldSize;
		this.tileSize = tileSize;
		this.howManyMines = howManyMines;
		this.startingHeight = startingHeight;
		this.contentPane = contentPane;
		this.unrevealedColor = new Color(200, 200, 200);
		this.revealedColor = new Color(255, 255, 255);
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

	// Reset the field
	public void reset() {
		clear();
		placeMines();
		countMines();
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

	// Check if there are any more valid moves
	private boolean checkForVictory() {
		// Linear search the tiles, returning false if any are hidden but do not have a
		// mine
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
				tiles[i][j].setBackground(unrevealedColor);
				tiles[i][j].setText(null);
			}
		}
	}

	// Initialize the field
	public void initialize() {
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				// Create final variables
				final int iFinal = i;
				final int jFinal = j;

				// Create the tile
				tiles[i][j] = new Tile(false, false, 0, false);

				// Set the bounds of the tile
				tiles[i][j].setBounds(10 + (i * tileSize), 11 + (j * tileSize) + startingHeight, tileSize, tileSize);

				// Set the colors
				tiles[i][j].setUnrevealedColor(unrevealedColor);
				tiles[i][j].setRevealedColor(revealedColor);
				tiles[i][j].setTextColor(textColor);

				// Add the mouse listener
				tiles[i][j].addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						// The user left clicked
						if (e.getButton() == 1 && !tiles[iFinal][jFinal].isFlagged()) {
							// If the user revealed a mine, tell them and reset.
							if (revealTile(iFinal, jFinal)) {
								GameEnd.display(contentPane, "You awoke and evil spirit!", "Failure", "resources\\failureImg.png", revealedColor, textColor, unrevealedColor);
								reset();
							}

							// If the only hidden tiles remaining are mines, tell the user they won and
							// reset
							if (checkForVictory()) {
								GameEnd.display(contentPane, "You cleared the field!", "victory", "resources\\victoryImg.png", revealedColor, textColor, unrevealedColor);
								reset();
							}

						}
					}
				});

				// Add the tile to the content pane
				contentPane.add(tiles[i][j]);
			}
		}

		// Deal with the mines
		placeMines();
		countMines();
	}
}
