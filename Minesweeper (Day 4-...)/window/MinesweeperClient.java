package window;

import java.util.Random;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utilities.Tile;
import javax.swing.JToolBar;

public class MinesweeperClient extends JFrame {
	private static final int FIELD_SIZE=40;
	private static final int WINDOW_SIZE=818;
	private static final int HOW_MANY_MINES=100;
	private static final long serialVersionUID = 1L;
	private static final Random RAND = new Random();
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
		// Create the toolBar
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 0, 822, 16);
		
		// Create the window itself
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WINDOW_SIZE+20, WINDOW_SIZE+42+toolBar.getHeight());
		setTitle("Minesweeper");
		
		// Create the content pane, and add the toolBar
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(toolBar);

		// Create the tiles
		Tile[][] tiles = new Tile[FIELD_SIZE][FIELD_SIZE];
		for(int i=0;i<FIELD_SIZE;i++) {
			for(int j=0;j<FIELD_SIZE;j++) {
				tiles[i][j] = new Tile(false, false, 0);
				tiles[i][j].setBounds(10+(i*20), 11+(j*20)+toolBar.getHeight(), 20, 20);
				contentPane.add(tiles[i][j]);
			}
		}
		// Deal with the mine placement
		placeMines(tiles, FIELD_SIZE, HOW_MANY_MINES);
		countMines(tiles, FIELD_SIZE);
	}

	// Place the mines in a Tile double array
	public void placeMines(Tile[][] field, int size, int howMany) {
		int row, col;
		for(int i=0;i<howMany;i++) {
			do {
				row=RAND.nextInt(size);
				col=RAND.nextInt(size);
			}while(field[row][col].hasMine());
			field[row][col].placeMine();
		}
	}
	
	// Count the mines
	public void countMines(Tile[][] field, int size) {
		int counter=0;
		boolean canGoNorth, canGoEast, canGoSouth, canGoWest;

		// For each tile
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				// Initialize the data
				canGoNorth=true;
				canGoEast=true;
				canGoSouth=true;
				canGoWest=true;
				counter=0;

				// Determine any edge positioning
				if (i == 0)
					canGoNorth = false;
				if (j == size - 1)
					canGoEast = false;
				if (i == size - 1)
					canGoSouth = false;
				if (j == 0)
					canGoWest = false;


				// Check north
				if(canGoNorth && field[i-1][j].hasMine())
					counter++;

				// Check north-east
				if(canGoNorth && canGoEast && field[i-1][j+1].hasMine())
					counter++;
				
				// Check east
				if(canGoEast && field[i][j+1].hasMine())
					counter++;
				
				// Check south-east
				if(canGoSouth && canGoEast && field[i+1][j+1].hasMine())
					counter++;
				
				// Check south
				if(canGoSouth && field[i+1][j].hasMine())
					counter++;
				
				// Check south-west
				if(canGoSouth && canGoWest && field[i+1][j-1].hasMine())
					counter++;
				
				// Check west
				if(canGoWest && field[i][j-1].hasMine())
					counter++;
				
				// Check north-west
				if(canGoNorth && canGoWest && field[i-1][j-1].hasMine())
					counter++;
				
				// Assign the number of discovered adjacent mines to the tile
				field[i][j].setValue(counter);
			}
		}
	}
}
