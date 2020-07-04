package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Files {
	// Create a file
	public static File create(String path, String fileName, String extension) {
		File createdFile = null;

		try {
			createdFile = new File(path + fileName + extension);
			if (createdFile.createNewFile())
				System.out.println("File created: " + createdFile.getName());
			else
				createdFile = null;	
		}
		catch (IOException e) {
			System.err.println("Could not create file.");
			e.printStackTrace();
		}

		return createdFile;
	}

	// Open a file
	public static File open(String path, String fileName, String extension) {
		File file = null;

		file = new File(path + fileName + extension);
		if (!file.exists())
			System.err.println("File does not exist.\n");

		// Return the opened file, or null if it doesn't exist
		return (file.exists())? file : null;
	}

	// Write one line to a file
	public static void write(String filePath, String toWrite, boolean saveOld) {
		FileWriter writer = null;
		Scanner fileIn = null;
		String copy = "";

		try {
			// Copy any existing data
			if(saveOld) {
				fileIn = new Scanner(new File(filePath));
				while (fileIn.hasNext())
					copy += fileIn.nextLine() + "\n";
				fileIn.close();
			}

			// Write to file
			writer = new FileWriter(filePath);
			if(saveOld)
				writer.write(copy);
			writer.append(toWrite);
			writer.close();
		} catch (Exception e) {
			System.err.println("Could not write to file.");
			e.printStackTrace();
		}
	}

	// Write several lines to a file
	public static void write(String filePath, String[] toWrite, boolean saveOld) {
		FileWriter writer = null;
		Scanner fileIn = null;
		String copy = "";

		try {
			// Copy any existing data
			if(saveOld) {
				fileIn = new Scanner(new File(filePath));
				while (fileIn.hasNext())
					copy += fileIn.nextLine() + "\n";
				fileIn.close();
			}

			// Write to file
			writer = new FileWriter(filePath);
			if(saveOld)
				writer.write(copy);
			for(int i = 0; i < toWrite.length; i++)
				writer.append(toWrite[i]+ "\n");
			writer.close();
		} catch (Exception e) {
			System.err.println("Could not write to file.");
			e.printStackTrace();
		}
	}

	// Read from a file
	public static String[] read(String filePath) {
		LinkedList<String> toReturn = new LinkedList<String>();
		Scanner fileIn = null;

		try {
			fileIn = new Scanner(new File(filePath));
			while (fileIn.hasNextLine())
				toReturn.add(fileIn.nextLine());
		} catch (Exception e) {
			System.err.println("Could not read from file.");
			e.printStackTrace();
		}

		return toReturn.toArray(new String[toReturn.size()]);
	}

	// Read from a file excluding specified lines
	public static String[] read(String filePath, LinkedList<Integer> excludeLines) {
		LinkedList<String> toReturn = new LinkedList<String>();
		Scanner fileIn = null;
		int currLine = 0;

		try {
			fileIn = new Scanner(new File(filePath));
			while (fileIn.hasNextLine()) {
				if(!excludeLines.contains(currLine))
					toReturn.add(fileIn.nextLine());
				else
					fileIn.nextLine();
				currLine++;
			}
		} catch (Exception e) {
			System.err.println("Could not read from file.");
			e.printStackTrace();
		}

		return toReturn.toArray(new String[toReturn.size()]);
	}
}
