import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import phidgets.*;
import phidgets.AzimuthStepperMotor.TargetPositionOutOfBoundsException;
import solarCalculator.SolarCalculations;;

/**
 * Solar Tracker is logging framework. Desinged for the prototype Solar Tracker hardware system.
 * 
 * Phidgets in the system must be updated here for them to implemented and used.
 * 
 * External configuration of the system is for the logging and location of the system.
 * 
 * Before logging the and solar azimuth alignments can begin. The magnet and sensor are aligned.
 * 	Achieved by a method in the AzimuthStepperMotorClass.
 * 
 * Once the magnet has been found. The solar panel is directed at the sun, and the logging phase is entered.
 * 
 * Logging requires the user to input the situation to be used in the title of the file.
 * Once entered the log file is created and entries are made every interval until the duration
 * is over.
 * 
 * Once the logging is complete the program terminates.
 *
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
class SolarTrackerMain {

	static File configFile = new File("ConfigFile.txt");
	
	static double logDuration;	//log count (minutes
	static double logInterval;	//milli-seconds
	
	static boolean tracking;
	
	/*
	 * Global Positioning of Solar Panel
	 * Bournemouth: latitude = 50.7506208, longitude = -1.90262
	 */
	static double latitude, longitude;
	static boolean DST;	//Daylight Savings Time
	static SolarCalculations solarCalculations;

	/*
	 * NOTE: The Phidget Control Panel must be shut because only one application can interact with Phidgets at a time
	 */
	static PhidgetHub ph01;
	static int phDSN = 527370;	//Device Serial Number

	static VoltageSensor vltgSnsr;
	static String vltgName = "Voltage Sensor";
	static int vltgHP = 0;

	static AzimuthStepperMotor ASMtr;	//
	static String asmName = "Turntable Motor";
	static int asmHP = 1;

	static MagneticSensor magSnsr;
	static String magName = "Magnetic Sensor";
	static int magHP = 2;

	static AmbientLightSensor luxSnsr;
	static String luxName = "Ambient Light Sensor";
	static int luxHP = 3;

	static String separator = "- - - - - -";	//string to divide information on the console
	static boolean demo = false;	//used to display more/less information on the console (true/false)

	public static void main(String[] args) {

		//Ensure configuration for system is suitable before action.
		if (!config()) {
			System.err.println("Config File Error. - Program Terminating. Read above.");
			System.exit(1);
		}
		
		//the demo value and separator are referred to by other Phidgets
		ph01 = new PhidgetHub(phDSN, demo, separator);

		//Phidget Device creation (Phidget hub connected to, hub port, device name)
		vltgSnsr = new VoltageSensor(ph01, vltgHP, vltgName);
		vltgSnsr.openSetChannel(0);

		ASMtr = new AzimuthStepperMotor(ph01, asmHP, asmName);
		ASMtr.openSetChannel(0);

		magSnsr = new MagneticSensor(ph01, magHP, magName);
		magSnsr.openSetChannel(0);

		luxSnsr = new AmbientLightSensor(ph01, luxHP, luxName);
		luxSnsr.openSetChannel(0);

		solarCalculations = new SolarCalculations(latitude, longitude, DST);
		
		/*
		 * Make sure all the Phidget devices are attached before working with them.
		 */
		if(!ph01.allPhidgetsAttached()) {
			ph01.closePhidgetChannels();
			System.err.println("Not all Phidgets attached. - Program Terminated");
			System.exit(1);
		} else {
			System.out.println("All Phidgets attached.");
			System.out.println(separator);	
		}

		ASMtr.findMagnet(magSnsr);
		
		System.out.println("Current Solar Azimuth = " + getSolarAzimuth());
		System.out.println(separator);
		
		try {
			//Move to solar azimuth
			System.out.println("Aligning with azimuth now...");
			ASMtr.moveASMToPosition(getSolarAzimuth());
		} catch (TargetPositionOutOfBoundsException TPOOBEx) {
			ph01.closePhidgetChannels();
			System.err.println(TPOOBEx.getMessage());			
			System.exit(1);
		}
		
		System.out.println("Solar panel bearing = " + ASMtr.position());
		System.out.println(separator);
		
		startLogging();
		
		ph01.closePhidgetChannels();
		
		System.out.println("Program successful - Terminating now");
		System.exit(0);
	}

	/**
	 * @return the current solar azimuth
	 */
	private static double getSolarAzimuth() {
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(180 + solarCalculations.calcSolarAzimuth(Calendar.getInstance())));	//Azimuth Angle, returned from South so 180 degrees is added.
	}

	/**
	 * @return the current solar zenith
	 */
	private static double getSolarZenith() {
		return solarCalculations.calcSolarZenith(Calendar.getInstance());
	}

	/**
	 * Logs the sensory data at intervals
	 */
	private static void startLogging(){
		String logHeader = "Time, Panel Voltage, Light LUX, Bearing, Solar Azimuth, Offset, Zenith";
		int logCount = (int) (logDuration*60/logInterval);

		System.out.println("Creating 1 log file");
		System.out.println("Enter current weather condition E.g. Partial cloud");
		String weatherCond = getWeatherCondition();
		
		Log log = new Log(weatherCond);

		log.writeLineToFile("Latitude," + solarCalculations.getLatitude() + ",Longitude," 
				+ solarCalculations.getLongitude());
		log.writeLineToFile("Note," + weatherCond);
		log.writeLineToFile(logHeader);
		
		System.out.println(separator);
		System.out.println("\nLogging for " + logDuration + " minute(s) / " + logInterval 
				+ " second(s) Intervals\n"
				+ ":: Count = " + logCount + " ::\n");
	
		
		System.out.println("CSV \n" + logHeader + "\n");

		//Rolls through the log count. Waiting the set sleepTime between each and when its reached breaks.
		for (int logID = 1;logID <= logCount; logID++) {
			
			if (ph01.allPhidgetsAttached()) {
				if (tracking) {
					try {
						ASMtr.moveASMToPosition(getSolarAzimuth());
					} catch (TargetPositionOutOfBoundsException TPOOBEx) {
						System.out.println("Log closing");
						ph01.closePhidgetChannels();
						System.err.println(TPOOBEx.getMessage());			
						System.exit(1);
					}
				}
				String logEntry = buildLogEntry();
				log.writeLineToFile(logEntry);
				System.out.println(logID + " - " + logEntry);
				try {
					Thread.sleep((long) (logInterval*1000));
				} catch (InterruptedException IEx) {
					IEx.printStackTrace();
				}
			} else {
				System.out.println("Not all phidgets attached - closing log");
				break;
			}
		}
		log.closeWriter();
		System.out.println(separator);
	}

	/**
	 * Build log entry string
	 * @return record
	 */
	private static String buildLogEntry() {
		SimpleDateFormat datef = new SimpleDateFormat("HH:mm:ss");
		DecimalFormat df = new DecimalFormat("#.##");

		double spv = vltgSnsr.getVoltage();
		double lux = luxSnsr.getIlluminance();
		double bearing = ASMtr.position();
		double AziAng = Double.valueOf(df.format(getSolarAzimuth()));
		double ZenAng = Double.valueOf(df.format(getSolarZenith()));
		double offset = Double.valueOf(df.format(AziAng - bearing));

		String record = (datef.format(new Date()) + "," + spv + "," + lux + "," + bearing + "," + AziAng + "," + offset + "," + ZenAng);

		return record;
	}

	/**
	 * To create useful file names the atmospheric conditions are embedded in them for quick access.
	 * @return String weather condition
	 */
	private static String getWeatherCondition() {
		Scanner reader = new Scanner(System.in);
		String weather = reader.nextLine();
		reader.close();
		return weather;
	}

	/**
	 * Extracts Longitude, Latitude and DST plus the Log Duration and Interval
	 * from the Config.txt File
	 * @throws FileNotFoundException
	 */
	private static boolean config() {
		int configErrors = 0;
		
		try {
			Scanner configFileReader = new Scanner(configFile);

			while (configFileReader.hasNext()) {
				String text = (String) configFileReader.nextLine();

				switch (text){

				case ("//Longitude"): {
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							longitude = Double.parseDouble(text);
							System.out.println("Longitude = " + longitude);
							break;
						}
						catch (NumberFormatException NFEx) {
							System.out.println("Error setting longitude");
						}
					} 
					configErrors++;
				}

				case ("//Latitude"):{
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							latitude = Double.parseDouble(text);
							System.out.println("Latitude = " + latitude);
							break;
						}
						catch (NumberFormatException NFEx) {
							System.out.println("Error setting latitude");
						}
					}
					configErrors++;
				}

				case ("//Daylight Savings Time (boolean)"): {
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							if (text.equalsIgnoreCase("false") || text.equals("0")) {
								DST = false;
							} else if (text.equalsIgnoreCase("true") || text.equals("1")) {
								DST = true;
							} else {
								DST = false;
								throw new configException ("DST defaultly set to FALSE");
							}
							System.out.println("DST: " + DST);
							break;
						}
						catch (configException ConfEx) {
							System.out.println(ConfEx.getMessage());
						}
					}
				}
				
				case ("//Log duration (minutes)"):{
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							logDuration = Double.parseDouble(text);
							System.out.println("Log Duration = " + logDuration + " minutes");
							break;
						}
						catch (NumberFormatException NFEx) {
							System.out.println("Log duration must be an integer.");
						}				
					}
					configErrors++;
				}

				case ("//Log interval (seconds)"):{
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							logInterval = Double.parseDouble(text);
							System.out.println("Log Interval = " + logInterval + " seconds");
							break;
						}
						catch (NumberFormatException NFEx) {
							System.out.println("Log interval must be an integer.");
						}				
					}
					configErrors++;
				}
				
				case ("//track? (boolean)"): {
					if (configFileReader.hasNext()) {
						try {
							text = configFileReader.nextLine();
							if (text.equalsIgnoreCase("false") || text.equals("0")) {
								tracking = false;
							} else if (text.equalsIgnoreCase("true") || text.equals("1")) {
								tracking = true;
							} else {
								tracking = false;
								throw new configException ("Tracking defaultly set to FALSE");
							}
							System.out.println("Tracking: " + tracking);
							break;
						}
						catch (configException ConfEx) {
							System.out.println(ConfEx.getMessage());
						}
					}
				}
				}
			}

			configFileReader.close();
			System.out.println(separator);

			if (configErrors != 0) {
				System.out.println("ConfigFile.txt not suffice for system operation.\n"
						+ "\nTo set the longitude:"
						+ "\nIdentify the single number, E/W +/- on your GPS service\n"
						+ "\nTo set the latitude:"
						+ "\nIdentify the single number, N/S +/- on your GPS service\n"
						+ "\nDaylight Savings Time improves accuracy.\n"
						+ "\nThe log duration is the total time logging will process."
						+ "\nThe log interval is the time between log entries.\n"
						+ "\nExpected file format inside \"SolarTracker/ConfigFile.txt\""
						+ "\n//Longitude"
						+ "\n±##.###"
						+ "\n//Latitude"
						+ "\n±##.###"
						+ "\n//Daylight Savings Time (boolean)"
						+ "\ntrue|false|TRUE|FALSE|1|0"
						+ "\n//Log duration (minutes)"
						+ "\n+##.###"
						+ "\n//Log interval (seconds)"
						+ "\n+##.###"
						);
				return false;
			} else {
				System.out.println("System Configured.");
				System.out.println(separator);
				return true;
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Config File " + configFile.getPath() + "Not Found.");
			return false;
		}
	}

	/**
	 * Catches the entry for Daylight Savings configuration when its not a boolean entry
	 */
	private static class configException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		configException(String message){
			super(message);
		}
	}
}
