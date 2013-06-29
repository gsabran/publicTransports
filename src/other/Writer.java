package other;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import main.main;

public class Writer {
	// used to write/delete files
	public static void WriteFile(String Text, String fileName) {
		String directoryPath = System.getProperty("user.dir") + "/"
				+ main.repertoire;
		String filePath = System.getProperty("user.dir") + "/"
				+ main.repertoire + "/" + fileName;

		String s = new StringBuilder(fileName).reverse().toString();
		int i = s.indexOf("/");
		if (i != -1) {
			String path = s.substring(i + 1, s.length());
			fileName = s.substring(0, i);
			path = new StringBuilder(path).reverse().toString();
			fileName = new StringBuilder(fileName).reverse().toString();
			directoryPath = System.getProperty("user.dir") + "/"
					+ main.repertoire + "/" + path;
			filePath = System.getProperty("user.dir") + "/" + main.repertoire
					+ "/" + path + "/" + fileName;
		}
		// Getting filePath and directoryPath with the file name and the
		// directory name.

		File file = new File(filePath);
		File dir = new File(directoryPath);

		if (dir.exists() && dir.isDirectory() && file.exists()) {

			try {

				FileWriter fw = new FileWriter(filePath, true);
				BufferedWriter output = new BufferedWriter(fw);

				output.write(Text);
				output.flush();
				output.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
			}

		}

		// Write the text if the file is already created

		else {

			if (dir.exists() && dir.isDirectory()) {

				try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// Create a new file if the directory already exists, but
				// neither the file

				try {
					FileWriter fw = new FileWriter(filePath, true);
					BufferedWriter output = new BufferedWriter(fw);

					output.write(Text);
					output.flush();
					output.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (java.lang.NullPointerException e) {
					e.printStackTrace();
				}
				// Write the text in the file
			} else {
				dir.mkdirs();
				try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					FileWriter fw = new FileWriter(filePath, true);
					BufferedWriter output = new BufferedWriter(fw);

					output.write(Text);
					output.flush();
					output.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (java.lang.NullPointerException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void eraseFile(String file) {
		String fileName = main.repertoire + "/" + file;
		String filePath = System.getProperty("user.dir") + "/" + fileName;
		boolean success = (new File(filePath)).delete();
		}

	public static void eraseDirecrory(String dir) {
		dir = main.repertoire + "/" + dir;
		File directory = new File(dir);
		if (!directory.exists()) {
		} else {
			try {
				delete(directory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					delete(fileDelete);
				}
				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else {
			// if file, then delete it
			file.delete();
		}
	}
}
