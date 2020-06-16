package window;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utilities.Field;

public class MinesweeperClient extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	// Some important numbers
	int fieldSize = 40;
	int tileSize = 24;
	int windowSize = ((fieldSize+1)*tileSize);
	int howManyMines = 150;

	// Launch the application
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MinesweeperClient frame = new MinesweeperClient();
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
		ImageIcon[] icons = {
				new ImageIcon("resources\\tileImg1.png"),
				new ImageIcon("resources\\tileImg2.png"),
				new ImageIcon("resources\\tileImg3.png"),
				new ImageIcon("resources\\tileImg4.png"),
				new ImageIcon("resources\\tileImg5.png"),
				new ImageIcon("resources\\tileImg6.png"),
				new ImageIcon("resources\\tileImg7.png"),
				new ImageIcon("resources\\tileImg8.png"),
				new ImageIcon("resources\\tileImgFlag.png"),
				new ImageIcon("resources\\tileImgMine.png")
		};
		
		// The colors
		Color[] classicColors = { new Color(200, 200, 200), new Color(255, 255, 255), new Color(75, 75, 75) };
		Color[] darkColors = { new Color(50, 50, 50), new Color(105, 105, 105), new Color(255, 255, 255) };
		Color[] desertColors = { new Color(200, 150, 0), new Color(130, 130, 0), new Color(0, 0, 0) };
		Color[] grassColors = { new Color(0, 100, 0), new Color(55, 46, 14), new Color(255, 255, 255) };
		Random rand = new Random();
		System.out.println("Colors created.");

		// The fonts
		Font menuFont = new Font("Consolas", Font.BOLD, 15);
		Font subMenuFont = new Font("Consolas", Font.BOLD, 12);
		System.out.println("Fonts created.");

		// Create the toolBar
		JMenuBar toolBar = new JMenuBar();
		toolBar.setBounds(0, 0, windowSize, 25);
		System.out.println("Toolbar created.");

		// Create the window itself
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(450, 0, 20+windowSize, 22+windowSize + 2*toolBar.getHeight());
		setTitle("Minesweeper");

		// Create the content pane, and add the toolBar
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(toolBar);
		System.out.println("Window created.");

		// Create the field
		Field field = new Field(fieldSize, tileSize, howManyMines, toolBar.getHeight(), contentPane, icons);
		field.setUnrevealedColor(desertColors[0]);
		field.setRevealedColor(desertColors[1]);
		field.setTextColor(desertColors[2]);
		System.out.println("Field created.");

		// Create a menu for the colors
		JMenu colors = new JMenu("Colors");
		colors.setFont(menuFont);
		toolBar.add(colors, 0);
		System.out.println("Color menu created.");

		// Classic colors
		JMenuItem classicColor = new JMenuItem("Classic");
		classicColor.setFont(subMenuFont);
		classicColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying classic colors.");
				field.setUnrevealedColor(classicColors[0]);
				field.setRevealedColor(classicColors[1]);
				field.setTextColor(classicColors[2]);
			}
		});
		colors.add(classicColor);
		System.out.println("Classical colors added.");

		// Dark mode colors
		JMenuItem darkColor = new JMenuItem("Dark");
		darkColor.setFont(subMenuFont);
		darkColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying dark mode colors.");
				field.setUnrevealedColor(darkColors[0]);
				field.setRevealedColor(darkColors[1]);
				field.setTextColor(darkColors[2]);
			}
		});
		colors.add(darkColor);
		System.out.println("Dark mode colors added.");

		// Desert colors
		JMenuItem desertColor = new JMenuItem("Desert");
		desertColor.setFont(subMenuFont);
		desertColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying desert colors.");
				field.setUnrevealedColor(desertColors[0]);
				field.setRevealedColor(desertColors[1]);
				field.setTextColor(desertColors[2]);
			}
		});
		colors.add(desertColor);
		System.out.println("Desert colors added.");

		// Field colors
		JMenuItem fieldColor = new JMenuItem("Field");
		fieldColor.setFont(subMenuFont);
		fieldColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying field colors.");
				field.setUnrevealedColor(grassColors[0]);
				field.setRevealedColor(grassColors[1]);
				field.setTextColor(grassColors[2]);
			}
		});
		colors.add(fieldColor);
		System.out.println("Field colors added.");

		// Random colors
		JMenuItem randomColors = new JMenuItem("Random");
		randomColors.setFont(subMenuFont);
		randomColors.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Applying random colors.");
				field.setUnrevealedColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
				field.setRevealedColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
				field.setTextColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
				System.out.println("\tUnrevealed Color RGB: "+field.getUnrevealedColor().getRed()+" "+field.getUnrevealedColor().getGreen()+" "+field.getUnrevealedColor().getBlue());
				System.out.println("\tRevealed Color RGB: "+field.getRevealedColor().getRed()+" "+field.getRevealedColor().getGreen()+" "+field.getRevealedColor().getBlue());
				System.out.println("\tText Color RGB: "+field.getTextColor().getRed()+" "+field.getTextColor().getGreen()+" "+field.getTextColor().getBlue());
			}
		});
		colors.add(randomColors);
		System.out.println("Random colors added.");

		// Create a menu for the game options
		JMenu gameActions = new JMenu("Actions");
		gameActions.setFont(menuFont);
		toolBar.add(gameActions, 1);
		System.out.println("Actions menu created.");

		// Action to reveal a random tile
		JMenuItem revealRandom = new JMenuItem("Reveal Random Tile");
		revealRandom.setFont(subMenuFont);
		revealRandom.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Revealing random tile.");
				field.revealRandomTile();
			}
		});
		gameActions.add(revealRandom);
		System.out.println("Reveal random tile action added.");

		// Action to reset the field
		JMenuItem restart = new JMenuItem("Restart");
		restart.setFont(subMenuFont);
		restart.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Restarting.");
				field.reset();
			}
		});
		gameActions.add(restart);
		System.out.println("Restart action added.");

		// Action to exit the game (maybe)
		JMenuItem exitGame = new JMenuItem("Exit Game");
		exitGame.setFont(subMenuFont);
		exitGame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("Exitting.");
				dispose();
			}
		});
		gameActions.add(exitGame);
		System.out.println("Exit action added.");
	}
}
