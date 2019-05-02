import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * A file is made when this class is instantiated. Requesting the atmospheric conditions and the capturing the time signature.
 * 
 * File Name = getWeatherCondition() + getDate() + ".csv"
 * 
 * CSV header is parsed from the calling routine in SolarTrackerMain. 
 *
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
class Log {

	private String fileTitle;
	private String fileExt = ".csv";
	
	private File logFile;

	private static BufferedWriter pen;
	private static FileOutputStream fos;
	private static OutputStreamWriter osw;

	Log(String fileTitle) {
		this.fileTitle = fileTitle;
		
		try {
			openFileWriters(logFile = new File(createLogFileName()));
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
	 * creates the file name from the weather condition provided by the user, the date and the file extension.
	 * @return
	 */
	private String createLogFileName() {
		String date = getDate();
		return fileTitle + " " + date + fileExt;
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
	 * The logs file writers are created here
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
