import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import phidgets.*;
import solarCalculator.SolarCalculations;;

/**
 * Solar Tracker is logging framework. Desinged for the prototype Solar Tracker hardware system.
 * 
 * 
 * Variables are designed for quick modifications when the systems purpose changes.
 * 
 * Once the magnet has been found, the solar panel orientated successfully. Alignment with 
 * azimuths and logging commences
 *
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
class SolarTrackerMain {

	static boolean demo = true;	//used to display more/less information on the console (true/false)
	static final int logCount = 60;	//log count
	static final int logInterval = 60000;	//milli-seconds

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

	/*
	 * Global Positioning of Solar Panel
	 * Bournemouth: latitude = 50.7506208, longitude = -1.90262
	 */
	static final double latitude = 50.7506208, longitude = -1.90262;
	static final boolean DST = true;	//Daylight Savings Time
	static SolarCalculations solarCalculations = new SolarCalculations(latitude, longitude, DST);

	static String separator = "- - - - - -";	//string to divide information on the console

	public static void main(String[] args) {

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

		/*
		 * Make sure all the Phidget devices are attached before working with them.
		 */
		if(ph01.allPhidgetsAttached()) {
			System.out.println("All Phidgets attached");
			System.out.println(separator);
			ASMtr.findMagnet(magSnsr);
			ASMtr.moveToPosition(getSolarAzimuth());
			startLogging();
		}

		ph01.closePhidgetChannels();

		System.out.println("Exiting, goodbye.");
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
		double logTime = logCount*(logInterval/1000);

		System.out.println("Creating 1 log file");
		System.out.println("Enter current weather condition E.g. Partial cloud");
		String weatherCond = getWeatherCondition();

		Log log = new Log(weatherCond);

		log.writeLineToFile(weatherCond);
		log.writeLineToFile(logHeader);

		System.out.println("\nLogging for " + logTime/60 + " minute(s) :: Intervals " + logInterval/1000 + " second(s) :: count = " + logCount);
		System.out.println(separator);
		System.out.println("CSV \n" + logHeader + "\n");

		//Rolls through the log count. Waiting the set sleepTime between each and when its reached breaks.

		for (int logID = 1;logID <= logCount; logID++) {
			if (ph01.allPhidgetsAttached()) {
				String logEntry = buildLogEntry();
				log.writeLineToFile(logEntry);
				System.out.println(logID + " - " + logEntry);
				try {
					Thread.sleep(logInterval);
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
}
