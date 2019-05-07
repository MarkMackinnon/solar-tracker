import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * A file and its writers are instantiated with this class.
 * 
 * The default file extension is a time stamp + ".csv"
 * 
 * The default file path is folder where the program executes.
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
class Log {

	private String fileTitle;
	private String fileExtension = ".csv";
	
	private File logFile;

	private static BufferedWriter pen;
	private static FileOutputStream fos;
	private static OutputStreamWriter osw;

	/**
	 * instantiates the Log file with the title given and opens the writers for it.
	 * @param fileTitle
	 */
	Log(String fileTitle) {
		this.fileTitle = fileTitle;
		
		try {
			openFileWriters(logFile = new File("Logs/" + createLogFileName()));
		} catch (Exception FEx) {
			System.out.println("Error creating log file");
			FEx.printStackTrace();
			closeWriter();
		}
		
		System.out.println("\nLog File Created:\n" + logFile.getPath());
	}

	/**
	 * @return String date : (dd-MM-yyyy) HHmmss
	 */
	private String getDate() {
		SimpleDateFormat date = new SimpleDateFormat("(dd-MM-yyyy) HHmmss");
		return (String) date.format(new Date());
	}
	
	/**
	 * Creates the file name, attaching whats passed to the class with a time stamp and the fileExt.
	 * @return
	 */
	private String createLogFileName() {
		String date = getDate();
		return fileTitle + " " + date + fileExtension;
	}
	
	/**
	 * Write the passed string to the file.
	 * @param line
	 */
	public void writeLineToFile (String line) {
		try {
			pen.write(line + "\n");
			pen.flush();
		}
		catch (IOException IOEx) {
			System.out.println("IO Exception writing to the log file.");			
			IOEx.printStackTrace();
			closeWriter();
		}
		catch (Exception E) {
			System.out.println("Problem writing to the log file.");
			E.printStackTrace();
			closeWriter();
		}
	}
	
	/**
	 * The logs file writers are instantiated here
	 * @param file
	 * @throws FileNotFoundException
	 */
	private void openFileWriters(File file) throws FileNotFoundException {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			pen = new BufferedWriter(osw);
	}

	/**
	 * Close the file writers
	 */
	public void closeWriter() {
		try {
			fos.close();
			osw.close();
			pen.close();
			System.out.println("Log closed");
		} catch (Exception Ex) {
			System.out.println("Exception thrown whilst closing the log file:");
			Ex.printStackTrace();
		}
	}


}
