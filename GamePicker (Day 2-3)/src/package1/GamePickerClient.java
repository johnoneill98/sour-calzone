package package1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.ListViewer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;

public class GamePickerClient extends Composite {
	// Static global variables
	static String games="";
	static String fileName="resources\\games.txt";
	static String windowPic="resources\\WindowPNG.png";
	static Scanner reader=null;

	// Non-static global variables
	Random rand=new Random();
	String chosenGame="";
	boolean darkMode=true;
	String moonPic="resources\\MoonPNG.png";
	String sunPic="resources\\SunPNG.png";

	// The main method
	public static void main(String[] args) throws IOException {
		boolean fileFound=false;

		// Open the games file, creating it if necessary
		while(!fileFound) {
			try {
				reader=new Scanner(new File(fileName));
				fileFound=true;
			}
			catch(FileNotFoundException e) {
				File file = new File (fileName);
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("Your games list was empty!\n");
				bw.write("Click the button below to edit this list.\n");
				bw.write("Each line should be the name of ONE game.\n");
				bw.write("Be sure to refresh once you're done!\n");
				bw.flush();
				bw.close();
			}
		}

		// Run the window
		if(fileFound) {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setLayout(new GridLayout(1, false));
			shell.setText("Random Game Picker");
			shell.setImage(new Image(display, windowPic));
			shell.setBackground(SWTResourceManager.getColor(32, 32, 32));
			@SuppressWarnings("unused")
			GamePickerClient gpc = new GamePickerClient(shell, SWT.NONE);
			shell.pack();
			shell.open();
			while(!shell.isDisposed()) {
				if(!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		}
	}

	public GamePickerClient(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(32, 32, 32));
		setLayout(null);

		// Display the dark mode button
		Button darkModeButton = new Button(this, SWT.NONE);
		darkModeButton.setToolTipText("Toggle Dark Mode");
		darkModeButton.setBounds(765, 10, 25, 25);
		darkModeButton.setImage(SWTResourceManager.getImage(moonPic));
		darkModeButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
		darkModeButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

		// Display the list of games
		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		List list = listViewer.getList();
		list.setBounds(10, 53, 250, 506);
		list.setBackground(SWTResourceManager.getColor(48, 48, 48));
		list.setForeground(SWTResourceManager.getColor(255, 255, 255));
		while(reader.hasNext())
			list.add(reader.nextLine());

		// Display the label at the top of the list of games
		Label gamesLabel = new Label(this, SWT.NONE);
		gamesLabel.setAlignment(SWT.CENTER);
		gamesLabel.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		gamesLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
		gamesLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		gamesLabel.setBounds(10, 10, 250, 37);
		gamesLabel.setText("Potential Games");

		// Display the label that prompts the user to click the button
		Label promptLabel = new Label(this, SWT.NONE);
		promptLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
		promptLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		promptLabel.setAlignment(SWT.CENTER);
		promptLabel.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		promptLabel.setBounds(344, 53, 376, 65);
		promptLabel.setText("Click the button below to choose from the list of "+list.getItemCount()+" games:");

		// Display the randomly chosen game
		Label randomGameLabel = new Label(this, SWT.NONE);
		randomGameLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
		randomGameLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		randomGameLabel.setAlignment(SWT.CENTER);
		randomGameLabel.setFont(SWTResourceManager.getFont("Segoe UI", 35, SWT.NORMAL));
		randomGameLabel.setBounds(344, 334, 376, 256);
		randomGameLabel.setText(chosenGame);

		// Display the random game button
		Button randomGameButton = new Button(this, SWT.NONE);
		randomGameButton.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		randomGameButton.setBounds(374, 135, 316, 110);
		randomGameButton.setText("Choose Random Game");
		randomGameButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
		randomGameButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

		//Display the edit games button
		Button editGamesButton = new Button(this, SWT.NONE);
		editGamesButton.setBounds(10, 565, 120, 25);
		editGamesButton.setText("Edit Games List");
		editGamesButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
		editGamesButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

		// Display the refresh games list button
		Button refreshGamesButton = new Button(this, SWT.NONE);
		refreshGamesButton.setText("Refresh Games List");
		refreshGamesButton.setForeground(SWTResourceManager.getColor(255, 255, 255));
		refreshGamesButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
		refreshGamesButton.setBounds(140, 565, 120, 25);

		// Choose a random game after cycling through some of them
		randomGameButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("Choosing a random game.");
				for(int i=0;i<25;i++) {
					chosenGame=list.getItem(rand.nextInt(list.getItemCount()));
					randomGameLabel.setText(chosenGame);
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// Change between dark and light modes
		darkModeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				darkMode=!darkMode;
				if(darkMode) {
					System.out.println("Turning on dark mode.");

					// Dark mode button
					darkModeButton.setImage(SWTResourceManager.getImage(moonPic));
					darkModeButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
					darkModeButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Window
					setBackground(SWTResourceManager.getColor(32, 32, 32));
					parent.setBackground(SWTResourceManager.getColor(32, 32, 32));

					// Games list
					list.setBackground(SWTResourceManager.getColor(48, 48, 48));
					list.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Games label
					gamesLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
					gamesLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Prompt label
					promptLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
					promptLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Random game label
					randomGameLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
					randomGameLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Random game button
					randomGameButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
					randomGameButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Edit games button
					editGamesButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
					editGamesButton.setForeground(SWTResourceManager.getColor(255, 255, 255));

					// Refresh games button
					refreshGamesButton.setForeground(SWTResourceManager.getColor(255, 255, 255));
					refreshGamesButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
				}
				else {
					System.out.println("Turning on light mode");

					// Dark mode button
					darkModeButton.setImage(SWTResourceManager.getImage(sunPic));
					darkModeButton.setBackground(SWTResourceManager.getColor(255, 255, 255));
					darkModeButton.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Window
					setBackground(SWTResourceManager.getColor(230, 230, 230));
					parent.setBackground(SWTResourceManager.getColor(230, 230, 230));

					// Games list
					list.setBackground(SWTResourceManager.getColor(255, 255, 255));
					list.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Games label
					gamesLabel.setBackground(SWTResourceManager.getColor(230, 230, 230));
					gamesLabel.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Prompt label
					promptLabel.setBackground(SWTResourceManager.getColor(230, 230, 230));
					promptLabel.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Random game label
					randomGameLabel.setBackground(SWTResourceManager.getColor(230, 230, 230));
					randomGameLabel.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Random game button
					randomGameButton.setBackground(SWTResourceManager.getColor(255, 255, 255));
					randomGameButton.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Edit games button
					editGamesButton.setBackground(SWTResourceManager.getColor(255, 255, 255));
					editGamesButton.setForeground(SWTResourceManager.getColor(0, 0, 0));

					// Refresh games button
					refreshGamesButton.setBackground(SWTResourceManager.getColor(255, 255, 255));
					refreshGamesButton.setForeground(SWTResourceManager.getColor(0, 0, 0));
				}
			}
		});

		// Open a text editor to edit the games list
		editGamesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("Opening text editor.");
				File file = new File(fileName);
				try {
					java.awt.Desktop.getDesktop().edit(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// Refresh the games list
		refreshGamesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("Refreshing games list.");

				try {
					list.removeAll();
					reader=new Scanner(new File(fileName));
					while(reader.hasNext())
						list.add(reader.nextLine());
					promptLabel.setText("Click the button below to choose from the list of "+list.getItemCount()+" games:");
				}
				catch(FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
