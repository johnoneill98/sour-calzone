package window;

import crypto.AES;
import crypto.Password;
import utilities.Files;

import java.io.*;
import java.util.*;
import java.util.Base64.*;
import java.util.Base64.Encoder;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class Client extends JFrame {
	private static final long serialVersionUID = -3738390622714397285L;

	// Size of things
	int marginSides = 10;
	int marginTop = 50;
	int marginBottom = 70+marginTop;
	int width = 960;
	int height = 865;

	// The image icons
	public static final ImageIcon FOLDER_ICON = new ImageIcon("resources\\folder.png");
	
	// The password manager data
	static String secretKey;
	static String[][] allData = null;
	static String actionTaken = "remove";

	// All the listeners
	static MouseListener tableRevealPassword;
	static MouseListener tableHidePassword;
	static MouseListener tableGetRowRemove;
	static MouseListener tableGetRowModify;
	static DocumentListener searchListener;
	static DocumentListener searchToggleButton;
	static MouseListener addEntryButton;
	static MouseListener removeEntryButton;
	static MouseListener modifyEntryButton;

	// Style data, constants
	public static final Insets LEFT_SPACE = new Insets(0, 2, 0, 0);
	public static final Font TABLE_FONT = new Font("Ariel", Font.PLAIN, 15);
	public static final Font BOLD_FONT = new Font("Ariel", Font.BOLD, 15);
	public static final Font LARGE_FONT = new Font("Ariel", Font.PLAIN, 20);
	public static final Font TITLE_FONT = new Font("Ariel", Font.BOLD, 35);
	public static final Font SUB_TITLE_FONT = new Font("Ariel", Font.BOLD, 30);
	public static final int DEFAULT_ECHO_CHAR = new JPasswordField().getEchoChar();

	// Launch the application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setIconImage(new ImageIcon("resources\\windowIcon.png").getImage());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		//applyColors();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Create the frame
	public Client() {
		File vaultFile = null;					// File containing all encrypted passwords
		BufferedReader reader = null; 			// Reader for parsing the decrypted string

		Decoder decoder = Base64.getDecoder(); 	// Decoder for Base64
		Encoder encoder = Base64.getEncoder();	// Encoder for Base64

		String[] fromFile = null; 				// All lines from the vault file
		String[] decrypted = null; 				// The decryption of the vault
		Object[] rowData = new Object[3]; 		// The working row of the decrypted data

		String vaultName = null; 				// The name of the vault

		int howManyRows = 0;					// The number of rows in the table

		// Create the table components
		JLabel vaultLabel = new JLabel();
		JTable table = new JTable(new PasswordTableModel(new Object[] { "Title", "Username", "Password" }, 0));
		JScrollPane scrollpane = new JScrollPane(table);
		PasswordTableModel tableModel = (PasswordTableModel) table.getModel();
		ListSelectionModel cellSelectionModel = table.getSelectionModel();

		// Create the search components
		JLabel search = new JLabel("Search");
		JLabel searchTitleLabel = new JLabel("Title: ");
		JLabel searchUsernameLabel = new JLabel("Username: ");
		JLabel searchPasswordLabel = new JLabel("Password: ");
		JTextField searchTitle = new JTextField();
		JTextField searchUsername = new JTextField();
		JPasswordField searchPassword = new JPasswordField();
		JButton searchClearButton = new JButton("Clear");
		JCheckBox searchPasswordToggle = new JCheckBox("Show Password");
		JTextField[] searchFields = {searchTitle, searchUsername, searchPassword};

		// Create the add components
		JLabel addMessage = new JLabel();
		JLabel add = new JLabel("Add an Entry");
		JLabel addTitleLabel = new JLabel("Title: ");
		JLabel addUsernameLabel = new JLabel("Username: ");
		JLabel addPasswordLabel = new JLabel("Password: ");
		JTextField addTitle = new JTextField();
		JTextField addUsername = new JTextField();
		JPasswordField addPassword = new JPasswordField();
		JButton addButton = new JButton("Add Entry");
		JButton addGenerateButton = new JButton("G");
		JLabel strengthLabel = new JLabel(" 0%");
		JLabel strengthLabelFill = new JLabel();
		JCheckBox addPasswordToggle = new JCheckBox("Show Password");
		JTextField[] addFields = {addTitle, addUsername, addPassword};

		// Create the remove components
		JLabel remove = new JLabel("Remove Entries");
		JLabel removeTitleLabel = new JLabel("Title: ");
		JLabel removeUsernameLabel = new JLabel("Username: ");
		JLabel removePasswordLabel = new JLabel("Password: ");
		JTextField removeTitle = new JTextField();
		JTextField removeUsername = new JTextField();
		JPasswordField removePassword = new JPasswordField();
		JButton removeButton = new JButton("Remove Entries");
		JCheckBox removePasswordToggle = new JCheckBox("Show Password");
		JButton removeClearButton = new JButton("Clear");
		JLabel removeMessage = new JLabel();
		JTextField[] removeFields = {removeTitle, removeUsername, removePassword};

		// Create the modify components
		JLabel modify = new JLabel("Modify an Entry");
		JLabel modifyTitleLabel = new JLabel("Title: ");
		JLabel modifyUsernameLabel = new JLabel("Username: ");
		JLabel modifyPasswordLabel = new JLabel("Password: ");
		JTextField modifyTitleBefore = new JTextField();
		JTextField modifyUsernameBefore = new JTextField();
		JPasswordField modifyPasswordBefore = new JPasswordField();
		JTextField modifyTitleAfter = new JTextField();
		JTextField modifyUsernameAfter = new JTextField();
		JPasswordField modifyPasswordAfter = new JPasswordField();
		JButton modifyButton = new JButton("Modify");
		JButton modifyGenerateButton = new JButton("G");
		JCheckBox modifyPasswordToggle = new JCheckBox("Show Password");
		JButton modifyClearButton = new JButton("Clear");
		JLabel modifyMessage = new JLabel("[PLACE HOLDER]");
		JTextField[] modifyFields = {modifyTitleBefore, modifyUsernameBefore, modifyPasswordBefore, modifyTitleAfter, modifyUsernameAfter, modifyPasswordAfter};

		// Create the bottom components
		JSeparator bottomSeparator = new JSeparator();
		JLabel bottomLabel = new JLabel();

		// Create the frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(450, 0, width, height);
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(1200, 900));
		setTitle("PassLolo");
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				// Update the width and height
				width = getWidth();
				height = getHeight();

				// Update table size
				scrollpane.setBounds(marginSides, marginTop, 52*(width / 100), height - marginBottom);
				table.setBounds(marginSides, marginTop, 52*(width / 100), height - marginBottom);

				// Update column width
				table.getColumnModel().getColumn(0).setPreferredWidth(5);
				table.getColumnModel().getColumn(1).setPreferredWidth(20);
				table.getColumnModel().getColumn(2).setPreferredWidth(100);

				// Update the table title
				vaultLabel.setBounds(10, 0, scrollpane.getWidth(), marginTop);

				// Update the search
				search.setBounds((int)(3.5*marginSides+table.getWidth()), marginSides, width-(marginSides+table.getWidth()), 50);
				searchTitleLabel.setBounds(2*marginSides+table.getWidth(), marginSides+search.getHeight(), 50, 25);
				searchUsernameLabel.setBounds(2*marginSides+table.getWidth(), marginSides+searchTitleLabel.getY()+searchTitleLabel.getHeight(), 80, 25);
				searchPasswordLabel.setBounds(2*marginSides+table.getWidth(), marginSides+searchUsernameLabel.getY()+searchUsernameLabel.getHeight(), 80, 25);
				searchTitle.setBounds(searchTitleLabel.getX()+searchPasswordLabel.getWidth(), marginSides+search.getHeight(), width-(5*marginSides+table.getWidth()+searchPasswordLabel.getWidth()), 25);
				searchUsername.setBounds(searchTitleLabel.getX()+searchPasswordLabel.getWidth(), marginSides+searchTitleLabel.getY()+searchTitleLabel.getHeight(), width-(5*marginSides+table.getWidth()+searchPasswordLabel.getWidth()), 25);
				searchPassword.setBounds(searchTitleLabel.getX()+searchPasswordLabel.getWidth(), marginSides+searchUsernameLabel.getY()+searchUsernameLabel.getHeight(), width-(5*marginSides+table.getWidth()+searchPasswordLabel.getWidth()), 25);
				searchClearButton.setBounds(marginSides+searchPassword.getX()+searchPassword.getWidth()/2-65, searchPassword.getHeight()+searchPassword.getY()+marginSides, 100, 25);
				searchPasswordToggle.setBounds(searchPasswordLabel.getX()+searchPasswordLabel.getWidth()/2, searchClearButton.getY(), 200, 25);

				// Update the add
				add.setBounds((int)(3.5*marginSides+table.getWidth()), searchClearButton.getY()+searchClearButton.getHeight()+marginSides, width-(marginSides+table.getWidth()), 50);
				addTitleLabel.setBounds(2*marginSides+table.getWidth(), marginSides+add.getHeight()+add.getY(), 50, 25);
				addUsernameLabel.setBounds(2*marginSides+table.getWidth(), marginSides+addTitleLabel.getY()+addTitleLabel.getHeight(), 80, 25);
				addPasswordLabel.setBounds(2*marginSides+table.getWidth(), marginSides+addUsernameLabel.getY()+addUsernameLabel.getHeight(), 80, 25);
				addTitle.setBounds(addTitleLabel.getX()+addPasswordLabel.getWidth(), marginSides+add.getHeight()+add.getY(), width-(5*marginSides+table.getWidth()+addPasswordLabel.getWidth()), 25);
				addUsername.setBounds(addTitleLabel.getX()+addPasswordLabel.getWidth(), marginSides+addTitleLabel.getY()+addTitleLabel.getHeight(), width-(5*marginSides+table.getWidth()+addPasswordLabel.getWidth()), 25);
				addPassword.setBounds(addTitleLabel.getX()+addPasswordLabel.getWidth(), marginSides+addUsernameLabel.getY()+addUsernameLabel.getHeight(), width-(5*marginSides+table.getWidth()+addPasswordLabel.getWidth())-25, 25);
				addButton.setBounds(marginSides+addUsername.getX()+addUsername.getWidth()/2-65, addPassword.getHeight()+addPassword.getY()+marginSides, 100, 25);
				addGenerateButton.setBounds(addUsername.getX()+addUsername.getWidth()-addUsername.getHeight(), addPassword.getY(), addPassword.getHeight(), addPassword.getHeight());
				addMessage.setBounds(addTitle.getX(), addTitle.getY()-30, addTitle.getWidth(), 40);
				strengthLabel.setBounds(addButton.getX()+addButton.getWidth()+marginSides, addButton.getY(), addUsername.getWidth()-((addButton.getX()+addButton.getWidth()+marginSides)-addUsername.getX()), 25);
				strengthLabelFill.setBounds(strengthLabel.getX(), strengthLabel.getY(), Password.strengthOf(new String(addPassword.getPassword()))*strengthLabel.getWidth()/100, strengthLabel.getHeight());
				addPasswordToggle.setBounds(addPasswordLabel.getX()+addPasswordLabel.getWidth()/2, addButton.getY(), 200, 25);

				// Update the remove
				remove.setBounds((int)(3.5*marginSides+table.getWidth()), addButton.getY()+addButton.getHeight()+marginSides, width-(marginSides+table.getWidth()), 50);
				removeTitleLabel.setBounds(2*marginSides+table.getWidth(), marginSides+remove.getHeight()+remove.getY(), 50, 25);
				removeUsernameLabel.setBounds(2*marginSides+table.getWidth(), marginSides+removeTitleLabel.getY()+removeTitleLabel.getHeight(), 80, 25);
				removePasswordLabel.setBounds(2*marginSides+table.getWidth(), marginSides+removeUsernameLabel.getY()+removeUsernameLabel.getHeight(), 80, 25);
				removeTitle.setBounds(removeTitleLabel.getX()+removePasswordLabel.getWidth(), marginSides+remove.getHeight()+remove.getY(), width-(5*marginSides+table.getWidth()+removePasswordLabel.getWidth()), 25);
				removeUsername.setBounds(removeTitleLabel.getX()+removePasswordLabel.getWidth(), marginSides+removeTitleLabel.getY()+removeTitleLabel.getHeight(), width-(5*marginSides+table.getWidth()+removePasswordLabel.getWidth()), 25);
				removePassword.setBounds(removeTitleLabel.getX()+removePasswordLabel.getWidth(), marginSides+removeUsernameLabel.getY()+removeUsernameLabel.getHeight(), width-(5*marginSides+table.getWidth()+removePasswordLabel.getWidth()), 25);
				removeButton.setBounds(marginSides+removePassword.getX()+removePassword.getWidth()/2-90, removePassword.getHeight()+removePassword.getY()+marginSides, 150, 25);
				removePasswordToggle.setBounds(removePasswordLabel.getX()+removePasswordLabel.getWidth()/2, removeButton.getY(), 200, 25);
				removeClearButton.setBounds(removePassword.getX()+(removePassword.getWidth()-100), removePassword.getHeight()+removePassword.getY()+marginSides, 100, 25);
				removeMessage.setBounds(removeTitle.getX(), removeTitle.getY()-30, removeTitle.getWidth(), 40);

				// Update the modify
				modify.setBounds((int)(3.5*marginSides+table.getWidth()), removeButton.getY()+removeButton.getHeight()+marginSides, width-(marginSides+table.getWidth()), 50);
				modifyTitleLabel.setBounds(2*marginSides+table.getWidth(), marginSides+modify.getHeight()+modify.getY(), 50, 25);
				modifyUsernameLabel.setBounds(2*marginSides+table.getWidth(), marginSides+modifyTitleLabel.getY()+modifyTitleLabel.getHeight(), 80, 25);
				modifyPasswordLabel.setBounds(2*marginSides+table.getWidth(), marginSides+modifyUsernameLabel.getY()+modifyUsernameLabel.getHeight(), 80, 25);
				modifyTitleBefore.setBounds(modifyTitleLabel.getX()+modifyPasswordLabel.getWidth(), marginSides+modify.getHeight()+modify.getY(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-10, 25);
				modifyUsernameBefore.setBounds(modifyTitleLabel.getX()+modifyPasswordLabel.getWidth(), marginSides+modifyTitleLabel.getY()+modifyTitleLabel.getHeight(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-10, 25);
				modifyPasswordBefore.setBounds(modifyTitleLabel.getX()+modifyPasswordLabel.getWidth(), marginSides+modifyUsernameLabel.getY()+modifyUsernameLabel.getHeight(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-10, 25);
				modifyTitleAfter.setBounds(modifyTitleBefore.getX()+modifyTitleBefore.getWidth()+20, marginSides+modify.getHeight()+modify.getY(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-10, 25);
				modifyUsernameAfter.setBounds(modifyTitleBefore.getX()+modifyTitleBefore.getWidth()+20, marginSides+modifyTitleLabel.getY()+modifyTitleLabel.getHeight(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-10, 25);
				modifyPasswordAfter.setBounds(modifyTitleBefore.getX()+modifyTitleBefore.getWidth()+20, marginSides+modifyUsernameLabel.getY()+modifyUsernameLabel.getHeight(), (width-(5*marginSides+table.getWidth()+modifyPasswordLabel.getWidth()))/2-35, 25);
				modifyButton.setBounds(marginSides+modifyPasswordBefore.getX()+modifyPasswordBefore.getWidth()-53, modifyPasswordBefore.getHeight()+modifyPasswordBefore.getY()+marginSides, 100, 25);
				modifyPasswordToggle.setBounds(modifyPasswordLabel.getX()+modifyPasswordLabel.getWidth()/2, modifyButton.getY(), 200, 25);
				modifyClearButton.setBounds(modifyPasswordAfter.getX()+(modifyPasswordAfter.getWidth()-100), modifyPasswordAfter.getHeight()+modifyPasswordAfter.getY()+marginSides, 100, 25);
				modifyMessage.setBounds(modifyTitleBefore.getX(), modifyTitleBefore.getY()-30, modifyTitleBefore.getWidth()+20+modifyTitleAfter.getWidth(), 40);
				modifyGenerateButton.setBounds(modifyUsernameAfter.getX()+modifyUsernameAfter.getWidth()-modifyUsernameAfter.getHeight(), modifyPasswordAfter.getY(), modifyPasswordAfter.getHeight(), modifyPasswordAfter.getHeight());
				
				// Update bottom separator
				bottomSeparator.setBounds(10, scrollpane.getHeight()+scrollpane.getY()+4, width-35, 1);

				// Update update label
				bottomLabel.setBounds(marginSides, scrollpane.getHeight()+scrollpane.getY(), width, 35);
			}
		});

		// Create the content pane
		JLayeredPane contentPane = new JLayeredPane();
		contentPane.setLayout(null);
		contentPane.setOpaque(true);
		contentPane.setBackground(new Color(215, 215, 215));
		setContentPane(contentPane);
		System.out.println("Window created.\n");

		String[] recievedInformation = getStartingInformation();
		vaultName = recievedInformation[0];
		secretKey = recievedInformation[1];
		vaultFile = Files.open(vaultName, "", "");
		vaultName = getStringBeforeLastOccurrence(getStringAfterLastOccurrence(vaultName, '\\'), '.');
		System.out.println(vaultFile.getAbsolutePath() + " opened.\n");

		// Read the information from the vault
		System.out.println("Reading from vault.");
		fromFile = Files.read(vaultFile.getPath());
		howManyRows = fromFile.length;
		System.out.println("Read from vault.\n");

		// Decrypt the vault
		System.out.println("Decrypting vault.");
		decrypted = new String[howManyRows];
		try {
			for (int i = 0; i < howManyRows; i++)
				decrypted[i] = AES.decrypt(decoder.decode(fromFile[i].getBytes()), secretKey);
		} catch (Exception e) {
			System.err.println("Could not decrypt file.");
			e.printStackTrace();
		}
		System.out.println("Decrypted vault.\n");

		// Add the decrypted data to the table, and store it an array
		allData = new String[howManyRows][3];
		System.out.println("Adding decrypted data to table.");
		try {
			for (int i = 0; i < howManyRows; i++) {
				// Create the reader
				reader = new BufferedReader(new StringReader(decrypted[i]));

				// Read the data
				for (int j = 0; j < rowData.length; j++)
					rowData[j] = reader.readLine();

				// Copy it into an array
				for (int j = 0; j < rowData.length; j++)
					allData[i][j] = (String) rowData[j];

				// Change the password to be censored
				rowData[2] = "";
				for(int j = 0; j < 10; j++)
					rowData[2] = rowData[2] + ((char) DEFAULT_ECHO_CHAR+"");
				tableModel.addRow(rowData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Added decrypted data to table.");

		// Create the title for the table
		vaultLabel.setText("Vault: "+vaultName);
		vaultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		vaultLabel.setFont(TITLE_FONT);
		contentPane.add(vaultLabel);

		// Sort the table by the first column
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		table.setRowSorter(sorter);
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();

		// Customize the table
		table.setFont(TABLE_FONT);
		table.setFocusable(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(25);
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPane.add(scrollpane);


		// Add the label for search
		search.setFont(SUB_TITLE_FONT);
		search.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(search);
		// Add the text field labels for searching
		searchTitleLabel.setFont(TABLE_FONT);
		searchUsernameLabel.setFont(TABLE_FONT);
		searchPasswordLabel.setFont(TABLE_FONT);
		searchUsername.setMargin(LEFT_SPACE);
		searchPassword.setMargin(LEFT_SPACE);
		searchTitle.setMargin(LEFT_SPACE);
		contentPane.add(searchTitleLabel);
		contentPane.add(searchUsernameLabel);
		contentPane.add(searchPasswordLabel);
		// Add the text fields for searching
		searchTitle.setFont(TABLE_FONT);
		searchUsername.setFont(TABLE_FONT);
		searchPassword.setFont(TABLE_FONT);
		contentPane.add(searchTitle);
		contentPane.add(searchUsername);
		contentPane.add(searchPassword);
		// Add the button to clear the text fields
		searchClearButton.setEnabled(false);
		searchClearButton.setFont(TABLE_FONT);
		searchClearButton.setFocusable(false);
		searchClearButton.addMouseListener(clearTextFields(searchFields));
		contentPane.add(searchClearButton);
		// Add the box to toggle revealing the password
		searchPasswordToggle.setFont(TABLE_FONT);
		searchPasswordToggle.setFocusable(false);
		searchPasswordToggle.setOpaque(false);
		searchPasswordToggle.addItemListener(togglePasswordAppearance(searchPassword));
		contentPane.add(searchPasswordToggle);


		// Add the label for adding
		add.setFont(SUB_TITLE_FONT);
		add.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(add);
		// Add the text field labels for adding
		addTitleLabel.setFont(TABLE_FONT);
		addUsernameLabel.setFont(TABLE_FONT);
		addPasswordLabel.setFont(TABLE_FONT);
		contentPane.add(addTitleLabel);
		contentPane.add(addUsernameLabel);
		contentPane.add(addPasswordLabel);
		// Add the text fields for adding
		addTitle.setFont(TABLE_FONT);
		addUsername.setFont(TABLE_FONT);
		addPassword.setFont(TABLE_FONT);
		addUsername.setMargin(LEFT_SPACE);
		addPassword.setMargin(LEFT_SPACE);
		addTitle.setMargin(LEFT_SPACE);
		addTitle.getDocument().addDocumentListener(typeToggleButton(addFields, addButton, "ONE"));
		addUsername.getDocument().addDocumentListener(typeToggleButton(addFields, addButton, "ONE"));
		addPassword.getDocument().addDocumentListener(typeToggleButton(addFields, addButton, "ONE"));
		contentPane.add(addTitle);
		contentPane.add(addUsername);
		contentPane.add(addPassword);
		// Add the error message for adding
		addMessage.setFont(TABLE_FONT);
		addMessage.setForeground(new Color(255, 0, 0));
		addMessage.setVisible(false);
		contentPane.add(addMessage);
		// Add the button to clear the text fields
		addButton.setFont(TABLE_FONT);
		addButton.setEnabled(false);
		addButton.setFocusable(false);
		contentPane.add(addButton);
		// Add the button for generating a password
		addGenerateButton.setFont(BOLD_FONT);
		addGenerateButton.setMargin(new Insets(0, 0, 0, 0));
		addGenerateButton.setFocusable(false);
		addGenerateButton.addMouseListener(generatePassword(addPassword));
		contentPane.add(addGenerateButton);
		// Add the label for presenting password strength
		strengthLabel.setFont(BOLD_FONT);
		strengthLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK.brighter()));
		strengthLabelFill.setOpaque(true);
		addPassword.getDocument().addDocumentListener(checkStrength(addPassword, strengthLabel, strengthLabelFill));
		contentPane.add(strengthLabel);
		contentPane.add(strengthLabelFill);
		contentPane.moveToFront(strengthLabel);
		// Add the box to toggle revealing the password
		addPasswordToggle.setFont(TABLE_FONT);
		addPasswordToggle.setFocusable(false);
		addPasswordToggle.setOpaque(false);
		addPasswordToggle.addItemListener(togglePasswordAppearance(addPassword));
		contentPane.add(addPasswordToggle);


		// Add the label for removing
		remove.setFont(SUB_TITLE_FONT);
		remove.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(remove);
		// Add the text field labels for removing
		removeTitleLabel.setFont(TABLE_FONT);
		removeUsernameLabel.setFont(TABLE_FONT);
		removePasswordLabel.setFont(TABLE_FONT);
		contentPane.add(removeTitleLabel);
		contentPane.add(removeUsernameLabel);
		contentPane.add(removePasswordLabel);
		// Add the text fields for removing
		removeTitle.setFont(TABLE_FONT);
		removeUsername.setFont(TABLE_FONT);
		removePassword.setFont(TABLE_FONT);
		removeTitle.setMargin(LEFT_SPACE);
		removeUsername.setMargin(LEFT_SPACE);
		removePassword.setMargin(LEFT_SPACE);
		removeTitle.getDocument().addDocumentListener(typeToggleButton(removeFields, removeClearButton, "ONE"));
		removeUsername.getDocument().addDocumentListener(typeToggleButton(removeFields, removeClearButton, "ONE"));
		removePassword.getDocument().addDocumentListener(typeToggleButton(removeFields, removeClearButton, "ONE"));
		contentPane.add(removeTitle);
		contentPane.add(removeUsername);
		contentPane.add(removePassword);
		// Add the button to remove the entry
		removeButton.setFont(TABLE_FONT);
		removeButton.setEnabled(true);
		removeButton.setFocusable(false);
		contentPane.add(removeButton);
		// Add the box to toggle revealing the password
		removePasswordToggle.setFont(TABLE_FONT);
		removePasswordToggle.setFocusable(false);
		removePasswordToggle.setOpaque(false);
		removePasswordToggle.addItemListener(togglePasswordAppearance(removePassword));
		contentPane.add(removePasswordToggle);
		// Add the button for clearing the fields
		removeClearButton.setFont(TABLE_FONT);
		removeClearButton.setEnabled(false);
		removeClearButton.setFocusable(false);
		removeClearButton.addMouseListener(clearTextFields(removeFields));
		contentPane.add(removeClearButton);
		// Add the message for removing
		removeMessage.setFont(TABLE_FONT);
		removeMessage.setVisible(false);
		contentPane.add(removeMessage);



		// Add the label for modifying
		modify.setFont(SUB_TITLE_FONT);
		modify.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(modify);
		// Add the text field labels for modifying
		modifyTitleLabel.setFont(TABLE_FONT);
		modifyUsernameLabel.setFont(TABLE_FONT);
		modifyPasswordLabel.setFont(TABLE_FONT);
		contentPane.add(modifyTitleLabel);
		contentPane.add(modifyUsernameLabel);
		contentPane.add(modifyPasswordLabel);
		// Add the before text fields for modifying
		modifyTitleBefore.setFont(TABLE_FONT);
		modifyUsernameBefore.setFont(TABLE_FONT);
		modifyPasswordBefore.setFont(TABLE_FONT);
		modifyTitleBefore.setMargin(LEFT_SPACE);
		modifyUsernameBefore.setMargin(LEFT_SPACE);
		modifyPasswordBefore.setMargin(LEFT_SPACE);
		modifyTitleBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyUsernameBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyPasswordBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyTitleBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		modifyUsernameBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		modifyPasswordBefore.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		contentPane.add(modifyTitleBefore);
		contentPane.add(modifyUsernameBefore);
		contentPane.add(modifyPasswordBefore);
		// Add the after text fields for modifying
		modifyTitleAfter.setFont(TABLE_FONT);
		modifyUsernameAfter.setFont(TABLE_FONT);
		modifyPasswordAfter.setFont(TABLE_FONT);
		modifyTitleAfter.setMargin(LEFT_SPACE);
		modifyUsernameAfter.setMargin(LEFT_SPACE);
		modifyPasswordAfter.setMargin(LEFT_SPACE);
		modifyTitleAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyUsernameAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyPasswordAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyClearButton, "ONE"));
		modifyTitleAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		modifyUsernameAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		modifyPasswordAfter.getDocument().addDocumentListener(typeToggleButton(modifyFields, modifyButton, "ALL"));
		contentPane.add(modifyTitleAfter);
		contentPane.add(modifyUsernameAfter);
		contentPane.add(modifyPasswordAfter);
		// Add the button to modify the entry
		modifyButton.setFont(TABLE_FONT);
		modifyButton.setEnabled(false);
		modifyButton.setFocusable(false);
		contentPane.add(modifyButton);
		// Add the box to toggle revealing the password
		modifyPasswordToggle.setFont(TABLE_FONT);
		modifyPasswordToggle.setBounds(modifyPasswordLabel.getX()+modifyPasswordLabel.getWidth()/2, modifyButton.getY(), 200, 25);
		modifyPasswordToggle.setFocusable(false);
		modifyPasswordToggle.setOpaque(false);
		modifyPasswordToggle.addItemListener(togglePasswordAppearance(modifyPasswordBefore));
		modifyPasswordToggle.addItemListener(togglePasswordAppearance(modifyPasswordAfter));
		contentPane.add(modifyPasswordToggle);
		// Add the button for clearing the fields
		modifyClearButton.setFont(TABLE_FONT);
		modifyClearButton.setEnabled(false);
		modifyClearButton.setFocusable(false);
		modifyClearButton.addMouseListener(clearTextFields(modifyFields));
		contentPane.add(modifyClearButton);
		// Add the button for generating a password
		modifyGenerateButton.setFont(BOLD_FONT);
		modifyGenerateButton.setMargin(new Insets(0, 0, 0, 0));
		modifyGenerateButton.setFocusable(false);
		modifyGenerateButton.addMouseListener(generatePassword(modifyPasswordAfter));
		contentPane.add(modifyGenerateButton, 1);
		// Add the message for modifying
		modifyMessage.setFont(TABLE_FONT);
		modifyMessage.setHorizontalAlignment(SwingConstants.CENTER);
		modifyMessage.setVisible(false);
		contentPane.add(modifyMessage);

		// Create a separator to make the bottom more obvious
		contentPane.add(bottomSeparator);

		// Create the update label at the bottom
		bottomLabel.setFont(LARGE_FONT);
		bottomLabel.setText("Right click a row to copy its password.");
		contentPane.add(bottomLabel);

		// Add the listeners
		refreshTableListeners(
				table,
				removeFields,
				modifyFields,
				bottomLabel
				);
		refreshSearchListeners(searchFields, searchClearButton, table);
		refreshAddListeners(
				removeFields, removeButton, removeMessage,
				searchFields, searchClearButton,
				addFields, addButton, addMessage,
				modifyFields, modifyButton,
				bottomLabel,
				vaultFile, table, encoder
				);
		refreshRemoveListeners(
				removeFields, removeButton, removeMessage,
				searchFields, searchClearButton,
				addFields, addButton, addMessage,
				modifyFields, modifyButton,
				bottomLabel,
				vaultFile, table, allData, encoder
				);
		refreshModifyListeners(
				removeFields, removeButton, removeMessage,
				searchFields, searchClearButton,
				addFields, addButton, addMessage,
				modifyFields, modifyButton, modifyMessage,
				vaultFile, table, allData, encoder
				);
		JLabel[] allMessages = {addMessage, removeMessage, modifyMessage};
		JTextField[][] allFields = {addFields, removeFields, modifyFields};
		refreshMessageListeners(allFields, allMessages);
		
		// Change the echo character of all password fields
		searchPassword.setEchoChar((char) DEFAULT_ECHO_CHAR);
		addPassword.setEchoChar((char) DEFAULT_ECHO_CHAR);
		removePassword.setEchoChar((char) DEFAULT_ECHO_CHAR);
		modifyPasswordBefore.setEchoChar((char) DEFAULT_ECHO_CHAR);
		modifyPasswordAfter.setEchoChar((char) DEFAULT_ECHO_CHAR);
	}

	/********************************************************/
	/* MASTER PASSWORD DIALOG METHODS ***********************/
	/********************************************************/

	// Show the prompt for entering the vault and master password
	public String[] getStartingInformation() {
		Object[] buttonOptions = { "Create Vault", "Enter Vault" };
		int spacing = 30;

		// Create the contents of the window
		JPanel message = new JPanel();
		message.setLayout(null);

		// Create the pane
		JOptionPane pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, buttonOptions, buttonOptions[1]);

		// Create the dialog
		JDialog dialog = new JDialog(new JFrame(), "PassLolo", true);
		dialog.setIconImage(new ImageIcon("resources\\windowIcon.png").getImage());
		dialog.setContentPane(pane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setBounds(800, 400, 500, 190);

		// Remove the focus on the button
		recursiveUnfocusButtons(pane);
		
		// Add the labels
		JLabel vaultLabel = new JLabel("Vault:");
		vaultLabel.setFont(TABLE_FONT);
		vaultLabel.setBounds(0, 0, 40, 25);
		message.add(vaultLabel);
		JLabel passwordLabel = new JLabel("Master Password:");
		passwordLabel.setFont(TABLE_FONT);
		passwordLabel.setBounds(0, vaultLabel.getY()+spacing, 120, 25);
		message.add(passwordLabel);
		
		// Add the vault field
		JTextField vaultField = new JTextField();
		vaultField.setFont(TABLE_FONT);
		vaultField.setMargin(LEFT_SPACE);
		vaultField.setBounds(vaultLabel.getWidth(), vaultLabel.getY(), dialog.getWidth()-vaultLabel.getWidth()-65, 25);
		message.add(vaultField);
		// Add the button to open the file explorer
		JButton openFileExplorerButton = new JButton(FOLDER_ICON);
		openFileExplorerButton.setFont(BOLD_FONT);
		openFileExplorerButton.setBackground(new Color(100, 100, 100));
		openFileExplorerButton.setBounds(vaultField.getX()+vaultField.getWidth(), vaultField.getY(), 25, 25);
		openFileExplorerButton.setMargin(new Insets(0, 0, 0, 0));
		openFileExplorerButton.addMouseListener(openFileChooser(vaultField));
		message.add(openFileExplorerButton);
		
		// Add the password field
		JPasswordField passwordField = new JPasswordField();
		passwordField.setFont(TABLE_FONT);
		passwordField.setMargin(LEFT_SPACE);
		passwordField.setEchoChar((char) DEFAULT_ECHO_CHAR);
		passwordField.setBounds(passwordLabel.getWidth(), passwordLabel.getY(), dialog.getWidth()-passwordLabel.getWidth()-40, 25);
		message.add(passwordField);
		// Add the check box to reveal password
		JCheckBox passwordToggle = new JCheckBox("Show Password");
		passwordToggle.setFont(TABLE_FONT);
		passwordToggle.setBounds(passwordLabel.getX()-4, passwordLabel.getY()+spacing, 150, 25);
		passwordToggle.setFocusable(false);
		passwordToggle.setOpaque(false);
		passwordToggle.addItemListener(togglePasswordAppearance(passwordField));
		message.add(passwordToggle);
		
		// Add the error message
		JLabel errorMessage = new JLabel();
		errorMessage.setVisible(false);
		errorMessage.setFont(TABLE_FONT);
		errorMessage.setForeground(new Color(255, 0, 0));
		errorMessage.setBounds(passwordToggle.getX()+passwordToggle.getWidth(), passwordToggle.getY(), 350, 25);
		passwordField.getDocument().addDocumentListener(hideMessages(new JLabel[] {errorMessage}));
		vaultField.getDocument().addDocumentListener(hideMessages(new JLabel[] {errorMessage}));
		message.add(errorMessage);
		
		// Add the listeners for the button and window
		pane.addPropertyChangeListener(buttonPressed(dialog, buttonOptions, vaultField, passwordField, errorMessage));
		dialog.addWindowListener(windowClosing(dialog));

		// Check the state of the window
		do {
			// Show the panel
			dialog.setVisible(true);

			// Reset the value of pane if the input failed
			if(errorMessage.getForeground().equals(new Color(255, 0, 0)) || errorMessage.getForeground().equals(new Color(0, 180, 0)))
				pane.setValue(null);
			
		} while (dialog.isVisible() || errorMessage.getForeground().equals(new Color(255, 0, 0)) || errorMessage.getForeground().equals(new Color(0, 180, 0)));

		return new String[] {vaultField.getText(), new String(passwordField.getPassword())};
	}

	// Return a listener for when a dialog is closed
	public WindowAdapter windowClosing(JDialog dialog) {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.out.println("Exitting.");
				System.exit(0);
			}
		};
	}

	// Return a listener to begin the application
	public PropertyChangeListener buttonPressed(JDialog dialog, Object[] buttons, JTextField vaultField, JPasswordField passwordField, JLabel errorMessage) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent pce) {
				// If the "Create Vault" option is selected
				if(pce.getNewValue() == buttons[0]) {
					// Open the file explorer
					System.out.println("Opening file explorer to save file");
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
					
					// If the user goes through with the creation of the file
					if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						// Append .vault if it was not entered
						String toCreate = fileChooser.getSelectedFile().getAbsolutePath();
						toCreate = (getStringAfterLastOccurrence(toCreate, '.').equals("vault"))? toCreate : toCreate+".vault";
						
						// Create the file
						File vaultFile = Files.create(toCreate, "", "");
												
						// Make sure the file did not previously exist
						if(vaultFile == null) {
							vaultField.setText(null);
							errorMessage.setVisible(true);
							errorMessage.setText("The name \""+getStringBeforeLastOccurrence(getStringAfterLastOccurrence(toCreate, '\\'), '.')+"\" is already taken");
							errorMessage.setForeground(new Color(255, 0, 0));
							
						}
						else {
							// Tell the user to enter a master password
							vaultField.setText(toCreate);
							errorMessage.setVisible(true);
							errorMessage.setText("Choose your master password, then Enter Vault.");
							errorMessage.setForeground(new Color(0, 180, 0));
						}
						
						System.out.println("Using file "+toCreate);
					}
				}


				// If the "Enter Vault" option is selected
				else if(pce.getNewValue() == buttons[1]) {
					System.out.println("Attempting to enter "+vaultField.getText()+" using "+new String(passwordField.getPassword()));

					// Make sure a file was selected
					if(vaultField.getText().isEmpty()) {
						errorMessage.setVisible(true);
						errorMessage.setText("No vault selected");
						errorMessage.setForeground(new Color(255, 0, 0));
					}
					// Make sure a password was entered
					else if(new String(passwordField.getPassword()).isEmpty()) {
						errorMessage.setVisible(true);
						errorMessage.setText("No master password entered");
						errorMessage.setForeground(new Color(255, 0, 0));
					}
					else {
						File vaultFile = Files.open(vaultField.getText(), "", "");
						// Make sure the file exists
						if(vaultFile == null) {
							errorMessage.setVisible(true);
							errorMessage.setText("File does not exist");
							errorMessage.setForeground(new Color(255, 0, 0));
						}
						// Make sure it is the correct file type
						else if(!getStringAfterLastOccurrence(vaultFile.getAbsolutePath(), '.').equals("vault")) {
							errorMessage.setVisible(true);
							errorMessage.setText("Incorrect file type");
							errorMessage.setForeground(new Color(255, 0, 0));
						}
						// Attempt to decrypt the first line of the file
						else {
							try {
								Scanner fileIn = new Scanner(new File(vaultFile.getPath())); // Insert the Scanner into the file
								if (fileIn.hasNext())
									AES.decrypt(Base64.getDecoder().decode(fileIn.nextLine().getBytes()), new String(passwordField.getPassword()));
								errorMessage.setText("Success!");
								errorMessage.setForeground(new Color(255, 255, 255));
							}
							catch (Exception e) {
								errorMessage.setVisible(true);
								errorMessage.setText("Master password incorrect");
								errorMessage.setForeground(new Color(255, 0, 0));
							}
						}
					}
				}

				// If the input is valid, hide the dialog
				dialog.setVisible(false);
			}
		};
	}

	// Return a listener to open the file explorer to open a file
	public MouseAdapter openFileChooser(JTextField vaultField) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				System.out.println("Opening file explorer to open file");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					vaultField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		};
	}
	
	/********************************************************/
	/* SEARCH FUNTION METHODS *******************************/
	/********************************************************/

	// Return a listener for when text is typed into a search box
	public static DocumentListener typedForSearch(JTextField title, JTextField username, JTextField password, JTable table, String[][] contents) {
		return new DocumentListener() {

			private String titleText, usernameText, passwordText;

			public void insertUpdate(DocumentEvent e) {
				titleText = title.getText().toLowerCase();
				usernameText = username.getText().toLowerCase();
				passwordText = password.getText();
				updateTable();
			}

			public void removeUpdate(DocumentEvent e) {
				titleText = title.getText().toLowerCase();
				usernameText = username.getText().toLowerCase();
				passwordText = password.getText();
				updateTable();
			}

			public void changedUpdate(DocumentEvent e) {
				// Not applicable
			}

			private void updateTable() {
				System.out.println("Searching for ["+titleText+", "+usernameText+", "+passwordText+"]");
				int howManyRows = table.getRowCount();
				boolean isMatch = true;

				// Empty the table
				for(int i=0;i<howManyRows;i++)
					((PasswordTableModel)table.getModel()).removeRow(0);

				for(int i = 0; i < contents.length; i++) {
					// Reset isMatch to be true
					isMatch = true;

					// Checking the title
					if(!titleText.isEmpty())
						isMatch = (contents[i][0]).toLowerCase().contains(titleText);

					// Checking the username
					if(isMatch && !usernameText.isEmpty())
						isMatch = (contents[i][1]).toLowerCase().contains(usernameText);

					// Checking the username
					if(isMatch && !passwordText.isEmpty())
						isMatch = (contents[i][2]).contains(passwordText);

					// Entry is a match, add it to the table
					if(isMatch) {
						// Change the password to be censored
						String hidden = "";
						for(int j = 0; j < 10; j++)
							hidden = hidden + ((char) DEFAULT_ECHO_CHAR+"");
						((PasswordTableModel)table.getModel()).addRow(new Object[] {contents[i][0], contents[i][1], hidden});
					}
				}
			}

		};
	}

	/********************************************************/
	/* ADD FUNCTION METHODS *********************************/
	/********************************************************/

	// Add a password to the table and file
	public static MouseAdapter addEntry(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel bottomLabel, File file, JTable table, String[][] data, Encoder encoder) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				// Validate that the input isn't empty
				if(addFields[0].getText().isEmpty() && addFields[1].getText().isEmpty() && addFields[2].getText().isEmpty()) {
					addMessage.setHorizontalAlignment(SwingConstants.CENTER);
					addMessage.setVerticalAlignment(SwingConstants.CENTER);
					addMessage.setForeground(new Color(255, 0, 0));
					addMessage.setText("Cannot add empty entry.");
					addMessage.setVisible(true);
					return;
				}

				// Validate that the input will not create a duplicate
				String title = addFields[0].getText();
				String username = addFields[1].getText();
				for(int i = 0; i < table.getRowCount(); i++) {
					if(table.getValueAt(i, 0).equals(title) && table.getValueAt(i, 1).equals(username)) {
						addMessage.setForeground(new Color(255, 0, 0));
						addMessage.setText(String.format("<html><body style=\"text-align: center;\">%s</body></html>", "Title-Username combination already exists."));
						addMessage.setVisible(true);
						return;
					}
				}


				byte[] encrypted = null;
				String combinedData = addFields[0].getText() + "\n" + addFields[1].getText() + "\n" + addFields[2].getText() + "\n";
				String[][] newData = new String[data.length+1][3];

				// Encrypt the combination of everything
				try {
					// Encrypt the combined data
					encrypted = AES.encrypt(combinedData, secretKey);

					// Add to file
					Files.write(file.getPath(), encoder.encodeToString(encrypted) + "\n", true);

					// Empty the table
					int fullTableSize = table.getRowCount();
					for(int i=0;i<fullTableSize;i++)
						((PasswordTableModel)table.getModel()).removeRow(0);

					// Copy from data --> newData
					for(int i=0;i<data.length;i++)
						for(int j=0;j<data[i].length;j++)
							newData[i][j] = data[i][j];
					// Add new entry into newData
					for(int i=0;i<newData[data.length].length;i++)
						newData[data.length][i] = addFields[i].getText();
					// Update allData
					allData = new String[newData.length][3];
					for(int i=0;i<newData.length;i++)
						for(int j=0;j<newData[i].length;j++)
							allData[i][j] = newData[i][j];

					// Add data back to table, firing a table changed event, updating data[][]
					String hidden = "";
					for(int i = 0; i < 10; i++)
						hidden = hidden + (char)DEFAULT_ECHO_CHAR + "";
					for(int i=0;i<allData.length;i++)
						((PasswordTableModel)table.getModel()).addRow(new Object[] {allData[i][0], allData[i][1], hidden});

					// Update the table listeners
					refreshTableListeners(table, removeFields, modifyFields, bottomLabel);

					// Update the search box listeners
					refreshSearchListeners(searchFields, searchButton, table);

					// Update the add entry listeners
					refreshAddListeners(
							removeFields, removeButton, removeMessage,
							searchFields, searchButton, 
							addFields, addButton, addMessage,
							modifyFields, modifyButton,
							bottomLabel,
							file, table, encoder);

					// Update the remove entries listeners
					refreshRemoveListeners(
							removeFields, removeButton, removeMessage,
							searchFields, searchButton, 
							addFields, addButton, addMessage,
							modifyFields, modifyButton,
							bottomLabel,
							file, table, allData, encoder);

					// Fire the search DocumentListeners
					for(int i = 0; i < searchFields.length; i++)
						searchFields[i].setText(searchFields[i].getText());

					// Empty text fields and hide error message
					for(int i = 0; i < addFields.length; i++)
						addFields[i].setText(null);
					addMessage.setVisible(true);
					addMessage.setForeground(new Color(0, 180, 0));
					addMessage.setHorizontalAlignment(SwingConstants.CENTER);
					addMessage.setVerticalAlignment(SwingConstants.CENTER);
					addMessage.setText("Entry successfully added to the vault.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	// Generate a password when a button is pressed
	public static MouseAdapter generatePassword(JTextField field) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				field.setText(Password.generate(30));
			}
		};
	}

	// Check the strength of the password in the password field
	public static DocumentListener checkStrength(JPasswordField field, JLabel percentage, JLabel fill) {
		return new DocumentListener() {

			private String password;
			private int strength;

			public void insertUpdate(DocumentEvent e) {
				password = new String(field.getPassword());
				strength = Password.strengthOf(password);
				percentage.setText(" "+strength+"%");
				fill.setBounds(percentage.getX(), percentage.getY(), strength*percentage.getWidth()/100, percentage.getHeight());
			}

			public void removeUpdate(DocumentEvent e) {
				password = new String(field.getPassword());
				strength = Password.strengthOf(password);
				percentage.setText(" "+strength+"%");
				fill.setBounds(percentage.getX(), percentage.getY(), strength*percentage.getWidth()/100, percentage.getHeight());
			}

			public void changedUpdate(DocumentEvent e) {
				// Not applicable
			}

		};
	}


	/********************************************************/
	/* REMOVE FUNCTION METHODS ******************************/
	/********************************************************/

	// Remove entries from the table and file
	public static MouseAdapter removeEntry(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel bottomLabel, File file, JTable table, String[][] data, Encoder encoder) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				// The indices that need to be removed
				LinkedList<Integer> toRemove = new LinkedList<Integer>();
				boolean isMatch = true;

				// The values in the text fields
				String title = removeFields[0].getText();
				String username = removeFields[1].getText();
				String password = removeFields[2].getText();

				// Determine the constraints the user typed
				boolean hasTitle = !title.isEmpty();
				boolean hasUsername = !username.isEmpty();
				boolean hasPassword = !password.isEmpty();

				// Find the matches in data
				for(int i = 0; i < data.length; i++) {
					// Reinitialize isMatch
					isMatch = true;

					// Check the title
					if(hasTitle && !title.equals(data[i][0]))
						isMatch = false;

					// Check the username
					if(isMatch && hasUsername && !username.equals(data[i][1]))
						isMatch = false;

					// Check the password
					if(isMatch && hasPassword && !password.equals(data[i][2]))
						isMatch = false;

					if(isMatch)
						toRemove.add(i);
				}

				// Display message if no matches were found
				if(toRemove.size() <= 0) {
					removeMessage.setText("No matching entries found.");
					removeMessage.setHorizontalAlignment(SwingConstants.CENTER);
					removeMessage.setForeground(new Color(255, 0, 0));
					removeMessage.setVisible(true);
					return;
				}

				// Display confirmation dialog
				if(toRemove.size() == table.getRowCount()) {
					if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "Are you sure you want to "+actionTaken+" ALL entries?", "Removal Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE))
						return;
				}
				else if(toRemove.size()==1) {
					if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "Are you sure you want to "+actionTaken+" "+toRemove.size()+" entry?", "Removal Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE))
						return;
				}
				else {
					if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, "Are you sure you want to "+actionTaken+" "+toRemove.size()+" entries?", "Removal Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE))
						return;
				}

				// Remove the matching lines in the file
				String[] newFileContents = Files.read(file.getPath(), toRemove);				
				Files.write(file.getPath(), newFileContents, false);

				// Empty the table
				int fullTableSize = table.getRowCount();
				for(int i=0;i<fullTableSize;i++)
					((PasswordTableModel)table.getModel()).removeRow(0);

				// Update allData
				String[][] newData = new String[newFileContents.length][3];
				int currIndex = 0;
				for(int i = 0; i < allData.length; i++) {
					if(!toRemove.contains(i)) {
						newData[currIndex][0] = allData[i][0];
						newData[currIndex][1] = allData[i][1];
						newData[currIndex++][2] = allData[i][2];
					}
				}
				allData = new String[newData.length][3];
				for(int i = 0; i < allData.length; i++)
					for(int j = 0; j < allData[i].length; j++)
						allData[i][j] = newData[i][j];

				// Refill the table
				String hidden = "";
				for(int i = 0; i < 10; i++)
					hidden = hidden + (char)DEFAULT_ECHO_CHAR + "";
				for(int i=0;i<allData.length;i++)
					((PasswordTableModel)table.getModel()).addRow(new Object[] {allData[i][0], allData[i][1], hidden});

				// Update the listeners
				refreshTableListeners(table, removeFields, modifyFields, bottomLabel);
				refreshSearchListeners(searchFields, searchButton, table);
				refreshAddListeners(removeFields, removeButton, removeMessage, searchFields, searchButton,  addFields, addButton, addMessage, modifyFields, modifyButton, bottomLabel, file, table, encoder);
				refreshRemoveListeners(removeFields, removeButton, removeMessage, searchFields, searchButton,  addFields, addButton, addMessage, modifyFields, modifyButton, bottomLabel, file, table, allData, encoder);

				// Fire the search DocumentListeners
				for(int i = 0; i < searchFields.length; i++)
					searchFields[i].setText(searchFields[i].getText());

				// Empty the text fields and display a success message
				for(int i = 0; i < removeFields.length; i++)
					removeFields[i].setText(null);
				if(!actionTaken.equals("modify"))
					for(int i = 0; i < modifyFields.length; i++)
						modifyFields[i].setText(null);
				removeMessage.setText("Entries successfully removed.");
				removeMessage.setHorizontalAlignment(SwingConstants.CENTER);
				removeMessage.setForeground(new Color(0, 180, 0));
				removeMessage.setVisible(true);
			}
		};
	}

	/********************************************************/
	/* MODIFY FUNCTION METHODS ******************************/
	/********************************************************/
	public static MouseAdapter modifyEntry(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel modifyMessage, File file, JTable table, String[][] data, Encoder encoder) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if(modifyButton.isEnabled()) {
					// Attempt to remove
					if(passDataToAction(removeFields, removeButton, removeMessage, modifyFields, modifyMessage, 0, me)) {
						// Attempt to add
						if(passDataToAction(addFields, addButton, addMessage, modifyFields, modifyMessage, removeFields.length, me)) {
							for(int i = 0; i < modifyFields.length; i++)
								modifyFields[i].setText(null);
							for(int i = 0; i < removeFields.length; i++)
								removeFields[i].setText(null);
							modifyMessage.setForeground(new Color(0, 180, 0));
							modifyMessage.setText("Entry successfully modified.");
							modifyMessage.setHorizontalAlignment(SwingConstants.CENTER);
							modifyMessage.setVisible(true);
						}
						// If failure, re-add the removed entry
						else
							passDataToAction(addFields, addButton, addMessage, modifyFields, modifyMessage, 0, me);
					}
				}
			}
		};
	}

	public static boolean passDataToAction(JTextField[] fields, JButton button, JLabel message, JTextField[] modifyFields, JLabel modifyMessage, int offset, MouseEvent me) {
		String[] saved = new String[fields.length];

		// Update the action taken
		actionTaken = "modify";

		// Attempt to remove the old entry
		for(int i = 0; i < fields.length; i++) {
			saved[i] = fields[i].getText();
			fields[i].setText(modifyFields[i+offset].getText());
		}
		button.dispatchEvent(me);

		// Reset the fields
		for(int i = 0; i < fields.length; i++)
			fields[i].setText(saved[i]);
		message.setVisible(false);

		// Determine the success based on the message
		if(message.getForeground().equals(new Color(255, 0, 0))) {
			modifyMessage.setForeground(new Color(255, 0, 0));
			modifyMessage.setText(message.getText());
			modifyMessage.setHorizontalAlignment(SwingConstants.CENTER);
			modifyMessage.setVisible(true);
			return false;
		}

		//Update the action taken
		actionTaken = "remove";

		return true;
	}

	/********************************************************/
	/* REFRESH LISTENERS METHODS ****************************/
	/********************************************************/

	// Refresh the listeners for the table
	public static void refreshTableListeners(JTable table, JTextField[] removeFields, JTextField[] modifyFields, JLabel bottomLabel) {
		table.removeMouseListener(tableRevealPassword);
		table.removeMouseListener(tableHidePassword);
		table.removeMouseListener(tableGetRowRemove);
		table.removeMouseListener(tableGetRowModify);

		tableRevealPassword = PasswordTableModel.getMouseListenerPressed(table, allData, bottomLabel);
		tableHidePassword = PasswordTableModel.getMouseListenerReleased(table, allData, (char) DEFAULT_ECHO_CHAR);
		tableGetRowRemove = PasswordTableModel.fillTextFieldListener(table, removeFields[0], removeFields[1], removeFields[2]);
		tableGetRowModify = PasswordTableModel.fillTextFieldListener(table, modifyFields[0], modifyFields[1], modifyFields[2]);

		table.addMouseListener(tableRevealPassword);
		table.addMouseListener(tableHidePassword);
		table.addMouseListener(tableGetRowRemove);
		table.addMouseListener(tableGetRowModify);
	}

	// Refresh the listeners for searching
	public static void refreshSearchListeners(JTextField[] searchFields, JButton button, JTable table) {
		// Remove the listeners
		for(int i = 0; i < searchFields.length; i++) {
			searchFields[i].getDocument().removeDocumentListener(searchListener);
			searchFields[i].getDocument().removeDocumentListener(searchToggleButton);
		}


		// Update the listeners
		searchListener = typedForSearch(searchFields[0], searchFields[1], searchFields[2], table, allData);
		searchToggleButton = typeToggleButton(searchFields, button, "ONE");

		// Apply the listeners
		for(int i = 0; i < searchFields.length; i++) {
			searchFields[i].getDocument().addDocumentListener(searchListener);
			searchFields[i].getDocument().addDocumentListener(searchToggleButton);
		}
	}

	// Refresh the listeners for adding
	public static void refreshAddListeners(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel bottomLabel, File file, JTable table, Encoder encoder) {
		addButton.removeMouseListener(addEntryButton);
		addEntryButton = addEntry(
				removeFields, removeButton, removeMessage,
				searchFields, searchButton, 
				addFields, addButton, addMessage,
				modifyFields, modifyButton,
				bottomLabel,
				file, table, allData, encoder
				);
		addButton.addMouseListener(addEntryButton);
	}

	// Refresh the listeners for removing
	public static void refreshRemoveListeners(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel bottomLabel, File file, JTable table, String[][] data, Encoder encoder) {
		removeButton.removeMouseListener(removeEntryButton);

		removeEntryButton = removeEntry(
				removeFields, removeButton, removeMessage,
				searchFields, searchButton, 
				addFields, addButton, addMessage,
				modifyFields, modifyButton,
				bottomLabel,
				file, table, data, encoder
				);

		removeButton.addMouseListener(removeEntryButton);
	}

	// Refresh the listeners for modifying
	public static void refreshModifyListeners(JTextField[] removeFields, JButton removeButton, JLabel removeMessage, JTextField[] searchFields, JButton searchButton, JTextField[] addFields, JButton addButton, JLabel addMessage, JTextField[] modifyFields, JButton modifyButton, JLabel modifyMessage, File file, JTable table, String[][] data, Encoder encoder) {
		modifyButton.removeMouseListener(modifyEntryButton);
		modifyEntryButton = modifyEntry(
				removeFields, removeButton, removeMessage,
				searchFields, searchButton, 
				addFields, addButton, addMessage,
				modifyFields, modifyButton, modifyMessage,
				file, table, data, encoder
				);
		modifyButton.addMouseListener(modifyEntryButton);
	}

	// Refresh the listeners to hide the messages
	public static void refreshMessageListeners(JTextField[][] fields, JLabel[] messages) {
		for(int i = 0; i < fields.length; i++)
			for(int j = 0; j < fields[i].length; j++)
				fields[i][j].getDocument().addDocumentListener(hideMessages(messages));
	}
	
	/********************************************************/
	/* GENERAL SOLUTION METHODS* ****************************/
	/********************************************************/

	// Return a listener that makes all labels not visible when editing a text field
	public static DocumentListener hideMessages(JLabel[] labels) {
		return new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				hideLabels();
			}

			public void removeUpdate(DocumentEvent e) {
				hideLabels();
			}

			public void changedUpdate(DocumentEvent e) {
				// Not applicable
			}
			
			public void hideLabels() {
				for(int i = 0; i < labels.length; i++)
					labels[i].setVisible(false);
			}

		};
	}
	
	// Return a listener that disables a button when text fields are empty
	public static DocumentListener typeToggleButton(JTextField[] fields, JButton button, String conditional) {
		return new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				toggleButton();
			}

			public void removeUpdate(DocumentEvent e) {
				toggleButton();
			}

			public void changedUpdate(DocumentEvent e) {
				// Not applicable
			}

			public void toggleButton() {
				boolean enable = false;
				if(conditional.equals("ONE")) {
					enable = false;
					for(int i = 0; i < fields.length; i++)
						if(!fields[i].getText().isEmpty())
							enable = true;
				}
				else if(conditional.equals("ALL")) {
					enable = true;
					for(int i = 0; i < fields.length; i++)
						if(fields[i].getText().isEmpty())
							enable = false;
				}
				button.setEnabled(enable);
			}

		};
	}

	// Return a listener for toggling the appearance of a JPasswordField
	public static ItemListener togglePasswordAppearance(JPasswordField field) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					field.setEchoChar('\0');
				else
					field.setEchoChar((char) DEFAULT_ECHO_CHAR);
			}
		};
	}

	// Return a listener to clear text fields
	public static MouseAdapter clearTextFields(JTextField[] fields) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				for(int i=0;i<fields.length;i++)
					fields[i].setText(null);
			}
		};
	}
	
	// Unfocus all buttons in a component
	public void recursiveUnfocusButtons(Component component) {
		if (component instanceof JButton) {
			component.setFocusable(false);
			return;
		} else if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				recursiveUnfocusButtons(c);
			}
		}
	} 

	// Get the string after the last occurrence of a character
	public String getStringAfterLastOccurrence(String str, char c) {
		int charPosition = -1;
		String toReturn = "";
		
	    // Get the position of the last period
		for(int i = 0; i < str.length(); i++)
			if(str.charAt(i) == c)
				charPosition = i;
		
		if(charPosition != -1)
			for(int i = charPosition+1; i < str.length(); i++)
				toReturn += str.charAt(i);
		
		return toReturn;
	}
	
	// Get the string before the last occurrence of a character
	public String getStringBeforeLastOccurrence(String str, char c) {
		int charPosition = -1;
		String toReturn = "";
		
		for(int i = 0; i < str.length(); i++)
			if(str.charAt(i) == c)
				charPosition = i;
		
		if(charPosition != -1)
			for(int i = 0; i < charPosition; i++)
				toReturn += str.charAt(i);
		
		return toReturn;
	}
}
