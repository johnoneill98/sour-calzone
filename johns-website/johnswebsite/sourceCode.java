import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.ListViewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class GamePickerClient extends Composite {
	public static String games="";
	public static Scanner reader=null;
	public String chosenGame="";
	public Random rand=new Random();
	public static void main(String[] args) {
		String name="games.txt";
		boolean fileFound=false;
		
		// Open the scanners
		try {
			reader=new Scanner(new File(name));
			fileFound=true;
		}
		catch(FileNotFoundException e) {
			System.out.println("--File Not Found--");
		}
		
		// Run the window
		if(fileFound) {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setLayout(new GridLayout(1, false));
			shell.setText("Game Picker");
			shell.setBackground(SWTResourceManager.getColor(32, 32, 32));
			GamePickerClient gpc = new GamePickerClient(shell, SWT.NONE);
			shell.pack();
			shell.open();
			while(!shell.isDisposed()) {
				if(!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
			System.out.println(gpc);
		}
	}
	
	public GamePickerClient(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(32, 32, 32));
		setLayout(null);
		
		// Set the right side of the window to not be ugly
		Label rightBound = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		rightBound.setBounds(788, 10, 2, 580);
		rightBound.setVisible(false);
		
		// Display the list of games
		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		List list = listViewer.getList();
		list.setBounds(10, 53, 250, 537);
		list.setBackground(SWTResourceManager.getColor(48, 48, 48));
		list.setForeground(SWTResourceManager.getColor(255, 255, 255));
		while(reader.hasNext())
			list.add(reader.nextLine());
		int howManyGames=list.getItemCount();
		
		// Display the label at the top of the list of games
		Label gamesLabel = new Label(this, SWT.NONE);
		gamesLabel.setAlignment(SWT.CENTER);
		gamesLabel.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.NORMAL));
		gamesLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
		gamesLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		gamesLabel.setBounds(10, 10, 250, 37);
		gamesLabel.setText("Potential Games");
		
		// Display the label that prompts the user to click the button
		Label promptText = new Label(this, SWT.NONE);
		promptText.setBackground(SWTResourceManager.getColor(32, 32, 32));
		promptText.setForeground(SWTResourceManager.getColor(255, 255, 255));
		promptText.setAlignment(SWT.CENTER);
		promptText.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		promptText.setBounds(344, 53, 376, 65);
		promptText.setText("Click the button below to choose from the list of "+howManyGames+" games:");
		
		// Display the randomly chosen game
		Label randomGameLabel = new Label(this, SWT.NONE);
		randomGameLabel.setBackground(SWTResourceManager.getColor(32, 32, 32));
		randomGameLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		randomGameLabel.setAlignment(SWT.CENTER);
		randomGameLabel.setFont(SWTResourceManager.getFont("Segoe UI", 35, SWT.NORMAL));
		randomGameLabel.setBounds(344, 334, 376, 256);
		randomGameLabel.setText(chosenGame);
		
		// Display the button
		Button randomGameButton = new Button(this, SWT.NONE);
		randomGameButton.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		randomGameButton.setBounds(374, 135, 316, 110);
		randomGameButton.setText("Choose Random Game");
		randomGameButton.setBackground(SWTResourceManager.getColor(48, 48, 48));
		randomGameButton.setForeground(SWTResourceManager.getColor(255, 255, 255));
		randomGameButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
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
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
