package window;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utilities.Field;

public class MinesweeperClient extends JFrame {
	private static final int FIELD_SIZE = 40;
	private static final int WINDOW_SIZE = 818;
	private static final int TILE_SIZE = 20;
	private static final int HOW_MANY_MINES = 200;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

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
		// The colors
		Color[] classicColors = { new Color(200, 200, 200), new Color(255, 255, 255), new Color(75, 75, 75) };
		Color[] darkColors = { new Color(50, 50, 50), new Color(105, 105, 105), new Color(255, 255, 255) };
		Color[] desertColors = { new Color(200, 150, 0), new Color(130, 130, 0), new Color(0, 0, 0) };
		Color[] grassColors = { new Color(0, 100, 0), new Color(55, 46, 14), new Color(255, 255, 255) };
		Random rand = new Random();

		// The fonts
		Font menuFont = new Font("Consolas", Font.BOLD, 15);
		Font subMenuFont = new Font("Consolas", Font.BOLD, 12);

		// Create the toolBar
		JMenuBar toolBar = new JMenuBar();
		toolBar.setBounds(0, 0, 822, 25);

		// Create the window itself
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WINDOW_SIZE + 20, WINDOW_SIZE + 42 + toolBar.getHeight());
		setTitle("Minesweeper");

		// Create the content pane, and add the toolBar
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(toolBar);

		// Create the field
		Field field = new Field(FIELD_SIZE, TILE_SIZE, HOW_MANY_MINES, toolBar.getHeight(), contentPane);
		field.setUnrevealedColor(desertColors[0]);
		field.setRevealedColor(desertColors[1]);
		field.setTextColor(desertColors[2]);

		// Create a menu for the colors
		JMenu colors = new JMenu("Colors");
		colors.setFont(menuFont);
		toolBar.add(colors, 0);

		// Default colors
		JMenuItem classicColor = new JMenuItem("Classic");
		classicColor.setFont(subMenuFont);
		classicColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.setUnrevealedColor(classicColors[0]);
				field.setRevealedColor(classicColors[1]);
				field.setTextColor(classicColors[2]);
			}
		});
		colors.add(classicColor);

		// Dark mode colors
		JMenuItem darkColor = new JMenuItem("Dark");
		darkColor.setFont(subMenuFont);
		darkColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.setUnrevealedColor(darkColors[0]);
				field.setRevealedColor(darkColors[1]);
				field.setTextColor(darkColors[2]);
			}
		});
		colors.add(darkColor);

		// Desert colors
		JMenuItem desertColor = new JMenuItem("Desert");
		desertColor.setFont(subMenuFont);
		desertColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.setUnrevealedColor(desertColors[0]);
				field.setRevealedColor(desertColors[1]);
				field.setTextColor(desertColors[2]);
			}
		});
		colors.add(desertColor);

		// Grass colors
		JMenuItem grassColor = new JMenuItem("Grass");
		grassColor.setFont(subMenuFont);
		grassColor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.setUnrevealedColor(grassColors[0]);
				field.setRevealedColor(grassColors[1]);
				field.setTextColor(grassColors[2]);
			}
		});
		colors.add(grassColor);

		// Random colors
		JMenuItem randomColors = new JMenuItem("Random");
		randomColors.setFont(subMenuFont);
		randomColors.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.setUnrevealedColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
				field.setRevealedColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
				field.setTextColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
			}
		});
		colors.add(randomColors);

		// Create a menu for the game options
		JMenu gameActions = new JMenu("Actions");
		gameActions.setFont(menuFont);
		toolBar.add(gameActions, 1);

		// Action to reveal a random tile
		JMenuItem revealRandom = new JMenuItem("Reveal Random Tile");
		revealRandom.setFont(subMenuFont);
		revealRandom.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.revealRandomTile();
			}
		});
		gameActions.add(revealRandom);

		// Action to reset the field
		JMenuItem restart = new JMenuItem("Restart");
		restart.setFont(subMenuFont);
		restart.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				field.reset();
			}
		});
		gameActions.add(restart);

		// Action to exit the game (maybe)
		JMenuItem exitGame = new JMenuItem("Exit Game");
		exitGame.setFont(subMenuFont);
		exitGame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});
		gameActions.add(exitGame);
	}
}
