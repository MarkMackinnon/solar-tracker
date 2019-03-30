import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class Log {

	private String fileExt = ".txt";
	private String fileName = "LOG: Unknown Condition (Solar Tracker)";

	static String header;
	FileWriter fw;
	PrintWriter pw;
	File logF;

	BufferedWriter pen;
	FileOutputStream fos;
	OutputStreamWriter osw;

	Log(String header) {
		System.out.println("Creating Log");
		Log.header = header;
		createFile();
	}

	private String getDate() {
		SimpleDateFormat date = new SimpleDateFormat("(dd-MM-yyyy) HHmmss");
		return (String) date.format(new Date());
	}
	
	private String getWeatherCondition() {
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter current weather conditions. E.g. Partial cloud");
		String weather = reader.nextLine();
		reader.close();
		return weather;
	}

	private void createFile() {
		String date = getDate();
		String weather = getWeatherCondition();
		
		fileName = weather + " " + date + fileExt;
		logF = new File(fileName);

		try {
			fos = new FileOutputStream(logF);
			osw = new OutputStreamWriter(fos);
			pen = new BufferedWriter(osw);
		} catch (FileNotFoundException FNFEx) {
			FNFEx.printStackTrace();
		}
		writeLine(header);
		System.out.println("\nLog File Created:\n" + fileName);
	}

	public void writeLine (String line) {
		try {
			pen.write(line + "\n");
			pen.flush();
		} catch (IOException IOEx) {
			System.out.println("IO Ex. writing to the log file.");			
			IOEx.printStackTrace();
		} catch (Exception E) {
			System.out.println("Problem writing to the log file.");
			E.printStackTrace();
			closeWriter();
		}
	}

	public void closeWriter() {
		try {
			pen.close();
			System.out.println("Log closed");
		} catch (IOException e) {
			System.out.println("Exception thrown whilst closing the log file");
			e.printStackTrace();
		}
	}


}
