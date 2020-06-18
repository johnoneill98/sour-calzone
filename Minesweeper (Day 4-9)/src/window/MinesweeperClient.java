package window;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Random;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import utilities.Field;
import utilities.InputValidation;

public class MinesweeperClient extends JFrame {
	private static final long serialVersionUID = 7522875941920357118L;
	private static MinesweeperClient frame;

	// Some important numbers
	public static final int TILE_SIZE = 24;
	int fieldSize;
	int howManyMines;
	int windowSize = ((fieldSize+1)*TILE_SIZE);

	// Some data that needs to exist through multiple methods
	private ImageIcon[] icons;
	private JPanel contentPane;

	// Launch the application
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new MinesweeperClient();
					frame.setIconImage(new ImageIcon("resources\\windowImage.png").getImage());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Create the frame
	public MinesweeperClient() {
		// The icons
		icons = getIcons("resources\\tileImages\\black\\");
		System.out.println("Default icons created.");

		// The colors
		Color[] classicColors = { new Color(200, 200, 200), new Color(255, 255, 255), new Color(75, 75, 75) };
		Color[] darkColors = { new Color(50, 50, 50), new Color(105, 105, 105), new Color(255, 255, 255) };
		Color[] desertColors = { new Color(200, 150, 0), new Color(130, 130, 0), new Color(0, 0, 0) };
		Color[] paigeColors = { new Color(18, 159, 153), new Color(224,  60, 126), new Color(0, 0, 0)};
		Color[] customColors = {null, null, null};
		System.out.println("Colors created.");

		// The fonts
		Font menuFont = new Font("Consolas", Font.BOLD, 15);
		Font subMenuFont = new Font("Consolas", Font.BOLD, 12);
		System.out.println("Fonts created.\n");

		// Prompt the user for input on the field size and density
		boolean gaveInput=false;
		do {
			gaveInput = getFieldInput(this, desertColors, true);
		} while(!gaveInput);

		// Create the frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(450, 0, 20+windowSize, 22+windowSize + 2*TILE_SIZE);
		setTitle("Minesweeper");

		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		System.out.println("Menu bar created/added.");

		// Create the content pane
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(desertColors[0].brighter());
		setContentPane(contentPane);
		System.out.println("Window created.");

		// Create the field
		Field field = new Field(fieldSize, TILE_SIZE, howManyMines, menuBar.getHeight(), contentPane, icons);
		field.setUnrevealedColor(desertColors[0]);
		field.setRevealedColor(desertColors[1]);
		field.setTextColor(desertColors[2]);
		System.out.println("Field created.\n");

		// Create a menu for the colors
		JMenu displayMenu = createMenu(menuFont, "Display", 0);
		menuBar.add(displayMenu);
		System.out.println("Display menu created.");

		// Create a sub-menu for tile colors
		JMenu tileColors = createMenu(subMenuFont, "Tile Colors", 0);
		displayMenu.add(tileColors);
		System.out.println("Tile Color sub-menu created.");

		// Classic colors
		tileColors.add(createTileColorOption(contentPane, field, classicColors, subMenuFont, "Classic"));
		System.out.println("\tClassic colors added.");
		// Dark mode colors
		tileColors.add(createTileColorOption(contentPane, field, darkColors, subMenuFont, "Dark Mode"));
		System.out.println("\tDark mode colors added.");
		// Desert colors
		tileColors.add(createTileColorOption(contentPane, field, desertColors, subMenuFont, "Desert"));
		System.out.println("\tDesert colors added.");
		// Paige colors
		tileColors.add(createTileColorOption(contentPane, field, paigeColors, subMenuFont, "Paige"));
		System.out.println("\tDesert colors added.");
		// Random colors
		tileColors.add(createTileColorOption(contentPane, field, new Color[] {null, null, null}, subMenuFont, "Random"));
		System.out.println("\tRandom colors added.\n");
		
		// Create a sub-menu for custom colors
		JMenu custom = createMenu(subMenuFont, "Custom", 0);
		tileColors.add(custom);
		System.out.println("\tCustom color sub-sub menu created");
		
		// Unrevealed custom color
		custom.add(createCustomColor(contentPane, field, customColors, subMenuFont, "Unrevealed Tile", 0));
		System.out.println("\t\tCustom unrevealed tile color added.");
		// Revealed custom color
		custom.add(createCustomColor(contentPane, field, customColors, subMenuFont, "Revealed Tile", 1));
		System.out.println("\t\tCustom revealed tile color added.");
		// Unrevealed custom color
		custom.add(createCustomColor(contentPane, field, customColors, subMenuFont, "Text Tile", 2));
		System.out.println("\t\tCustom text color added.");

		// Create a sub-menu for icon colors
		JMenu iconColors = createMenu(subMenuFont, "Icon Colors", 1);
		displayMenu.add(iconColors);
		System.out.println("Icon Color sub-menu created.");

		// Black icon colors
		iconColors.add(createIconColorOption(field, "resources\\tileImages\\black\\", subMenuFont, "Black"));
		System.out.println("\tBlack icons added.");
		// White icon colors
		iconColors.add(createIconColorOption(field, "resources\\tileImages\\white\\", subMenuFont, "White"));
		System.out.println("\tWhite icons added.\n");

		// Display option to toggle the tile decorations
		JMenuItem tileDecoration = new JMenuItem("Toggle Icons/Numbers");
		tileDecoration.setFont(subMenuFont);
		tileDecoration.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.print("Setting tile decoration to ");
				if(field.getIcons() == null) {
					System.out.println("icons.\n");
					field.setIcons(icons);
				}
				else {
					System.out.println("text.\n");
					field.setIcons(null);
				}
			}
		});
		displayMenu.add(tileDecoration);
		System.out.println("Toggle tile decoration option added.\n");

		// Create a menu for the game actions
		JMenu gameActions = createMenu(menuFont, "Actions", 1);
		menuBar.add(gameActions);
		System.out.println("Actions menu created.");

		// Action to reveal a blank tile
		JMenuItem revealBlank = new JMenuItem("Reveal Blank Tile");
		revealBlank.setFont(subMenuFont);
		revealBlank.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Revealing random tile.\n");
				field.revealRandomTile();
			}
		});
		gameActions.add(revealBlank);
		System.out.println("\tReveal random tile action added.");

		// Action to resize the field
		JMenuItem resizeField = new JMenuItem("Resize Field");
		resizeField.setFont(subMenuFont);
		resizeField.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Resizing.");
				if(getFieldInput(frame, new Color[] {field.getUnrevealedColor(), field.getRevealedColor(), field.getTextColor()}, false)) {
					field.setFieldSize(fieldSize);
					field.setHowManyMines(howManyMines);
					field.refreshColors();
					setBounds(450, 0, 20+windowSize, 22+windowSize + 2*TILE_SIZE);
				}

			}
		});
		gameActions.add(resizeField);
		System.out.println("\tResize action added.\n");

		// Action to reset the field
		JMenuItem restart = new JMenuItem("Restart");
		restart.setFont(subMenuFont);
		restart.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Restarting.\n");
				field.reset();
			}
		});
		gameActions.add(restart);
		System.out.println("\tRestart action added.");

		// Action to exit the game
		JMenuItem exitGame = new JMenuItem("Exit Game");
		exitGame.setFont(subMenuFont);
		exitGame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Exitting.\n");
				dispose();
			}
		});
		gameActions.add(exitGame);
		System.out.println("\tExit action added.\n");
	}

	// Create a menu on a menu bar
	public JMenu createMenu(Font font, String name, int pos) {
		JMenu menu = new JMenu(name);
		menu.setFont(font);
		return menu;
	}

	// Create tile color option for a menu
	public JMenuItem createTileColorOption(JPanel contentPane, Field field, Color[] colorsValues, Font font, String name) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setFont(font);
		menuItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying "+name+" tile colors.");
				if(name=="Random") {
					Random rand = new Random();
					colorsValues[0] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					colorsValues[1] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					colorsValues[2] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					System.out.printf("\t%10s RGB = %3d %3d %3d\n", "Unrevealed", colorsValues[0].getRed(), colorsValues[0].getGreen(), colorsValues[0].getBlue());
					System.out.printf("\t%10s RGB = %3d %3d %3d\n", "Revealed", colorsValues[1].getRed(), colorsValues[1].getGreen(), colorsValues[1].getBlue());
					System.out.printf("\t%10s RGB = %3d %3d %3d\n\n", "Text", colorsValues[2].getRed(), colorsValues[2].getGreen(), colorsValues[2].getBlue());
				}
				else
					System.out.println();
				field.setUnrevealedColor(colorsValues[0]);
				field.setRevealedColor(colorsValues[1]);
				field.setTextColor(colorsValues[2]);
				contentPane.setBackground(colorsValues[0].brighter());
			}
		});
		return menuItem;
	}
	
	// Create custom color option for a menu
	public JMenuItem createCustomColor(JPanel contentPane, Field field, Color[] colorsValues, Font font, String name, int whichColor) {
		UIManager.put("Panel.background", new Color(238, 238, 238));
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setFont(font);
		menuItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Copy the current colors on the field
				colorsValues[0] = field.getUnrevealedColor();
				colorsValues[1] = field.getRevealedColor();
				colorsValues[2] = field.getTextColor();
				
				// Allow user to choose a color to replace the predetermined color
				Color newColor = JColorChooser.showDialog(null, "Custom Color Picker", colorsValues[whichColor]);
				if(newColor != null)
					colorsValues[whichColor] = newColor;
				
				// Apply the colors to the field
				field.setUnrevealedColor(colorsValues[0]);
				field.setRevealedColor(colorsValues[1]);
				field.setTextColor(colorsValues[2]);
				contentPane.setBackground(colorsValues[0].brighter());
			}
		});
		return menuItem;
	}

	// Create icon color option for a menu
	public JMenuItem createIconColorOption(Field field, String path, Font font, String name) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setFont(font);
		menuItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying "+name+" icon colors.\n");
				field.setIcons(getIcons(path));
				icons = getIcons(path);
			}
		});
		return menuItem;
	}

	// Get the icons given a path
	public ImageIcon[] getIcons(String path) {
		ImageIcon[] icons = new ImageIcon[10];
		for(int i=0;i<8;i++)
			icons[i] = new ImageIcon(path+(i+1)+".png");
		icons[8] = new ImageIcon(path+"flag.png");
		icons[9] = new ImageIcon(path+"mine.png");
		return icons;
	}

	// Get input for field size and density
	public boolean getFieldInput(JFrame frame, Color[] colors, boolean isFirstWindow) {
		Font labelFont = new Font("Consolas", Font.BOLD, 15);
		Font textFieldFont = new Font("Consolas",Font.PLAIN, 13);
		
		String[] difficultyOptions = {"Easy", "Normal", "Hard", "Impossible", "Custom"};
		Object[] buttonOptions = {"Create Field", "Cancel"};
		if(isFirstWindow)
			buttonOptions[1] = "Exit Game";
		
		int sizeInput=0, minesInput=0;
		boolean isGoodNumeric;

		// Create the size components
		JTextField sizeField = new JTextField("16", 5);
		sizeField.setFocusable(false);
		sizeField.setForeground(Color.black);
		sizeField.setFont(textFieldFont);
		JLabel sizeLabel = new JLabel("Size", SwingConstants.CENTER);
		sizeLabel.setForeground(Color.black);
		sizeLabel.setFont(labelFont);

		// Create the mines components
		JTextField howManyMinesField = new JTextField("40", 5);
		howManyMinesField.setFocusable(false);
		howManyMinesField.setForeground(Color.black);
		howManyMinesField.setFont(textFieldFont);
		JLabel minesLabel = new JLabel("Mines", SwingConstants.CENTER);
		minesLabel.setForeground(Color.black);
		minesLabel.setFont(labelFont);

		// Create the combo box of preset difficulties
		JComboBox<String> presets = new JComboBox<String>(difficultyOptions);
		presets.setBackground(Color.white);
		presets.setForeground(Color.black);
		presets.setFocusable(false);
		presets.setSelectedIndex(1);
		presets.setFont(textFieldFont);
		presets.addActionListener(getFieldInputComboBarListener(presets, sizeField, howManyMinesField));

		// Create the error message
		JLabel errorLabel = new JLabel("ERROR", SwingConstants.CENTER);
		errorLabel.setForeground(Color.red.darker());
		errorLabel.setFont(labelFont);

		// Deal with the colors
		UIManager.put("Button.background", colors[1]);
		UIManager.put("Button.foreground", colors[2]);
		UIManager.put("Panel.background", colors[0]);
		UIManager.put("OptionPane.background", colors[0]);
		UIManager.put("OptionPane.messageForeground", colors[2]);

		// Create the contents of the window
		JPanel message = new JPanel();
		message.setLayout(null);

		// Add the size components
		message.add(sizeLabel);
		message.add(sizeField);
		sizeLabel.setBounds(0, 0, 100, 25);
		sizeField.setBounds(0, 25, 100, 25);
		message.add(Box.createHorizontalStrut(15));

		// Add the mine components
		message.add(minesLabel);
		message.add(howManyMinesField);
		minesLabel.setBounds(125, 0, 100, 25);
		howManyMinesField.setBounds(125, 25, 100, 25);

		// Add the error label, but don't show it yet
		message.add(errorLabel);
		errorLabel.setBounds(0, 60, 225, 25);
		errorLabel.setVisible(false);

		// Add the preset components
		message.add(presets);
		presets.setBounds(0, 85, 225, 25);

		// Create the pane
		JOptionPane pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, buttonOptions, buttonOptions[0]);

		// Create the dialog
		JDialog dialog = new JDialog(frame, "Field Specifications", true);
		dialog.setContentPane(pane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setBounds(800, 400, 261, 210);
		frame.setIconImage(new ImageIcon("resources\\windowImage.png").getImage());

		// Create the listener for the buttons
		pane.addPropertyChangeListener(getFieldInputButtonListener(dialog, buttonOptions, sizeField, howManyMinesField, isFirstWindow));
		
		// Create the listener for the window
		dialog.addWindowListener(getFieldInputWindowAdapter(dialog, sizeField, howManyMinesField, isFirstWindow));

		// Check the state of the window
		do {
			// Show the panel
			dialog.setVisible(true);

			// Make sure the information entered was a number
			isGoodNumeric = (InputValidation.isNumber(sizeField.getText(), false, false) && InputValidation.isNumber(howManyMinesField.getText(), false, false));
			if(isGoodNumeric) {
				try {
					sizeInput = Integer.parseInt(sizeField.getText());
					minesInput = Integer.parseInt(howManyMinesField.getText());
					// Display error message for size being too large
					if(sizeInput > 40) {
						System.out.println("Showing size error.\n");
						errorLabel.setText("ERROR: Maximum size = 40");
						errorLabel.setVisible(true);
						isGoodNumeric = false;
						pane.setValue(null);
					}
					// Display the error message for too many mines
					else if(minesInput > (sizeInput*sizeInput)) {
						System.out.println("Showing mines error.\n");
						errorLabel.setText("ERROR: Maximum mines = "+(sizeInput*sizeInput));
						errorLabel.setVisible(true);
						isGoodNumeric = false;
						pane.setValue(null);
					}
					// Update information
					else {
						fieldSize=sizeInput;
						howManyMines=minesInput;
						windowSize = ((fieldSize+1)*TILE_SIZE);
					}
				}
				catch(Exception e) {
					System.out.println("Showing generic error.\n");
					errorLabel.setText("ERROR: Invalid input");
					isGoodNumeric = false;
					pane.setValue(null);
				}
			}

			// The user wants to cancel
			else if(sizeField.getText().equals("Alex") && howManyMinesField.getText().equals("Benlolo")) {
				System.out.println("Closing window.\n");
				return false;
			}

			// User tried to enter something that is bad
			else if(!isGoodNumeric) {
				System.out.println("Showing non-positive number error.\n");
				errorLabel.setText("ERROR: Not a postive number");
				errorLabel.setVisible(true);
				pane.setValue(null);
			}
		} while(!isGoodNumeric);

		// The user wants to go through with the update
		System.out.println("Setting field size to "+fieldSize+" with "+howManyMines+" mines.\n");
		return true;
	}

	// Return the listener for when the resize window is closed
	public WindowAdapter getFieldInputWindowAdapter(JDialog dialog, JTextField size, JTextField mines, boolean showExit) {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if(showExit) {
					System.out.println("Exitting.");
					System.exit(0);
				}
				else {
					System.out.println("Cancelling resizing through window.");
					dialog.setVisible(false);
					size.setText("Alex");
					mines.setText("Benlolo");
				}
			}
		};
	}

	// Return the listener for the buttons of the resize window
	public PropertyChangeListener getFieldInputButtonListener(JDialog dialog, Object[] options, JTextField size, JTextField mines, boolean showExit) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				// User wants to submit input
				if(e.getNewValue() == options[0])
					System.out.println("Attempting to create field.");
				
				// User wants to exit
				else if (e.getNewValue() == options[1] && showExit) {
					System.out.println("Exitting.");
					System.exit(0);
				}
				
				// User wants to cancel
				else if (e.getNewValue() == options[1]) {
					System.out.println("Cancelling resizing through button.");
					size.setText("Alex");
					mines.setText("Benlolo");
				}
				
				dialog.setVisible(false);
			}
		};
	}

	// Return the listener for the preset difficulties combo box
	public ActionListener getFieldInputComboBarListener(JComboBox<String> comboBox, JTextField size, JTextField mines) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getSelectedIndex() == 0) {
					System.out.println("Updating Text Boxes to Easy.\n");
					size.setFocusable(false);
					mines.setFocusable(false);
					size.setText("9");
					mines.setText("10");
				}
				else if (comboBox.getSelectedIndex() == 1) {
					System.out.println("Updating Text Boxes to Normal.\n");
					size.setFocusable(false);
					mines.setFocusable(false);
					size.setText("16");
					mines.setText("40");
				}
				else if (comboBox.getSelectedIndex() == 2) {
					System.out.println("Updating Text Boxes to Hard.\n");
					size.setFocusable(false);
					mines.setFocusable(false);
					size.setText("22");
					mines.setText("99");
				}
				else if(comboBox.getSelectedIndex() == 3) {
					System.out.println("Updating Text Boxes to Impossible.\n");
					size.setFocusable(false);
					mines.setFocusable(false);
					size.setText("40");
					mines.setText("300");
				}
				else if(comboBox.getSelectedIndex() == 4) {
					System.out.println("Waiting for user input.\n");
					size.setFocusable(true);
					mines.setFocusable(true);
					size.setText(null);
					mines.setText(null);
					size.grabFocus();
				}
			}
		};
	}
}
