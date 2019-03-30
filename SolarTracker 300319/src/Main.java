/**
 * @author mac
 *	It is imperative to note with this software that their are physical elements that must be controlled to prevent their damaging.
 *Namely, the stepper motor/turn table. Before starting ensure that the hole in the turn table near the centre allows the wires
 *connected to the solar panel to travel straight down.
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.phidget22.*;
import Phidgets.*;
import Sol.*;
import MachineIntelligence_st.MI_Variables;



class Main {

	static String logDataHeader = "Time, Panel Voltage, Light LUX, Turntable Bearing, Solar Azimuth";
	static Log log;

	static final double latitude = 50.7506208, longitude = -.90262;	//Bournemouth
	static SolarCalculations solarCalculations = new SolarCalculations(latitude, longitude);

	static String separator = "- - - - - -";	//string to divide information on the console

	static boolean demo = true;	//Used to display more/less dynamic data (true/false)

	/**
	 * Phidgets in the project are named here and their Hub Port (HP) ID with them.
	 */
	static VintHUB vh01;
	static int VintHUB_SN = 527370;

	static SolarVoltageSensor spvSnsr;
	static String spvName = "Voltage Sensor";
	static int spvHP = 0;

	static TurntableMotor ttSMtr;
	static String ttmName = "Turntable Motor";
	static int ttSHP = 1;

	static MagneticSensor magSnsr;
	static String magName = "Magnetic Sensor";
	static int magHP = 2;

	static LuxSensor luxSnsr;
	static String luxName = "Lux Sensor";
	static int luxHP = 3;

	public static void main(String[] args) throws InterruptedException, PhidgetException {	

		//produceInt.printInt();
		//MI_Variables mi = new MI_Variables();
		//mi.printInt();
		//System.out.println(buildDataLogEntry());

		vh01 = new VintHUB(VintHUB_SN, demo, separator);

		spvSnsr = new SolarVoltageSensor(vh01, spvHP, spvName, 30);
		spvSnsr.OpenSetChannel(0);

		ttSMtr =  new TurntableMotor(vh01, ttSHP, ttmName);
		ttSMtr.OpenSetChannel(0);

		magSnsr = new MagneticSensor(vh01, magHP, magName);
		magSnsr.OpenSetChannel(0);

		luxSnsr = new LuxSensor(vh01, luxHP, luxName);
		luxSnsr.OpenSetChannel(0);

		
		FindMagnet();

		if (vh01.catchPhidgetsAttached()) {
			int logCount = 10;
			log = new Log(logDataHeader);
			System.out.println("Logging for " + logCount + "seconds");
			for (int sec = 0; sec < logCount; sec++) {
				log.writeLine(dataLogEntry());
				System.out.println("Logging...");

				if (sec == 1) {
					ttSMtr.MoveStepper(ttSMtr.southPos);
				}
				if (sec == 6) {
					ttSMtr.MoveStepper(ttSMtr.southPos + getSolarAzimuth());
				}
				Thread.sleep(1000);
			}
			log.closeWriter();
			System.out.println(separator);
		}

		spvSnsr.CloseChannel();
		ttSMtr.CloseChannel();
		magSnsr.CloseChannel();
		luxSnsr.CloseChannel();
	
	}

	static void FindMagnet() {
		double maxClwRotation = 180;
		double maxAntiClwRotation = -180;
		double target = 0;
		int cnt = 0;

		try {
			if (vh01.catchPhidgetsAttached()) {
				System.out.println("SCAN FOR MAGNET STARTED");
				System.out.println("Magnet: " + magSnsr.getMagAligned() + "(" + magSnsr.value + ")");
				System.out.println("Position: " + ttSMtr.value);
				System.out.println(separator);

				while (!magSnsr.getMagAligned() && target >= maxAntiClwRotation) {
					cnt++;
					target = ttSMtr.value - 5;
					ttSMtr.MoveStepper(ttSMtr.value - 5);
					if (demo) {
						System.out.println("Move " + cnt + "\tTarget: " + target);
						System.out.println("\tMagnet: " + magSnsr.getMagAligned());
						System.out.println("\tposition: " + ttSMtr.value);
					}
					if (cnt > 37) { break; }	//insurance to ensure the target position doesn't exceed 5*37 (185 degrees)
				}

				if (!magSnsr.getMagAligned() && ttSMtr.value < maxAntiClwRotation) {
					System.out.println("\n\t<3<3<3\n");
					System.out.println("Magnet not found in ANTI Clockwise Scan");
					System.out.println("\n\t<3<3<3\n");
					ttSMtr.MoveStepper(0);	//move back to starting point
					System.out.println(separator);
				}

				while (!magSnsr.getMagAligned() && ttSMtr.value < maxClwRotation) {
					cnt++;
					ttSMtr.MoveStepper(Math.round(ttSMtr.value + 5));
					if (demo) {
						System.out.println("Move " + cnt);
						System.out.println("\tMagnet: " + magSnsr.getMagAligned());
						System.out.println("\tposition: " + ttSMtr.value);
					}
					if (cnt > 74) { break; }
				}

				if (magSnsr.getMagAligned()) {
					if (cnt != 0 && demo == true) {
						System.out.println(separator);	
					}
					System.out.println(" :: MAGNET FOUND ::");
					System.out.println("Moves\t\t = " + cnt );
					System.out.println("Position\t = " + ttSMtr.value);
					System.out.println("Magnet Value\t = " + magSnsr.value);
					System.out.println(separator);
					ttSMtr.SetSouth();
				} else {
					System.out.println("Finding the magnet has been unsuccessful");
				}
			}
		}
		catch (Exception Ex) {
			System.err.println(Ex.getMessage());
		}
	}

	private static String dataLogEntry() {
		SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
		double spv = spvSnsr.getVoltage();
		double lux = luxSnsr.value;
		double bearing = ttSMtr.getCurrentHeading();
		double AziAng = getSolarAzimuth();

		String logEntry = (date.format(new Date()) + "," + spv + "," + lux + "," + bearing + "," + AziAng);

		return logEntry;
	}

	private static double getSolarAzimuth() {
		DecimalFormat df = new DecimalFormat(".##");
		return Double.valueOf(df.format(solarCalculations.calcSolarAzimuth(Calendar.getInstance())));	//Azimuth Angle
	}
}

/*
cal.add(Calendar.HOUR_OF_DAY, -12);
 */