/**
 * @author mac
 *
 */

import java.util.*;
import java.lang.*;
import com.phidget22.*;

import Phidgets.*;
import Sol.*;

public class Main {

	static double latitude = 50.7506208, longitude = -1.90262;	//Bournemouth

	static boolean demo = false;

	/**
	 * Phidgets in the project are named here and their Hub Port (HP) ID with them.
	 */
	static VintHUB VH1;
	static int VH_01_SN = 527370;	//Vint Hub 01 Serial Number

	static StepperMotor STP_01;
	static int STP_01_HP = 1;

	static MagneticSensor MAG_01;
	static int MAG_01_HP = 2;

	static LuxSensor LUX_01;
	static int LUX_01_HP = 3;

	static SolarAzimuth SA;
	static Calendar CAL;
	
	static String seperator = "- - - - - -";

	public static void main(String[] args) throws InterruptedException, PhidgetException {	

		VH1 = new VintHUB(seperator, VH_01_SN, demo);

		MAG_01 = VH1.createMagneticSensor(MAG_01_HP);
		STP_01 = VH1.createStepperMotor(STP_01_HP);

		MAG_01.OpenChannel();
		STP_01.OpenChannel();

		int waitStop = 0;
		boolean PHA = false;	//Phidgets Attached
		
		/*while (!STP_01.StepperAttached() || !MAG_01.MagnetAttached()) {
			VintHUB.PhidgetsAttached(STP_01.getPhidget);
			Thread.sleep(100);
			waitStop = waitStop+100;
			if (waitStop > 5000) {
				PHA = false;
				break;
			}
			System.out.println(waitStop);
			PHA = true;
		}
		*/
		
		while (!VH1.PhidgetsAttached()) {
			Thread.sleep(100);
			waitStop = waitStop+100;
			if (waitStop > 3000) {
				PHA = false;
				System.out.println("3 Seconds waited.. ");
				break;
			}
			PHA = true;
		}
		
		

		if (PHA) {
			FindMagnet();
			CalculateSouth();

			SA = new SolarAzimuth(latitude, longitude);
			CAL = Calendar.getInstance();

			double AziAng = SA.calcSolarAzimuth(CAL);	//Azimuth Angle
			AziAng = Math.round(AziAng);

			System.err.print("DATE ");
			System.out.println(CAL.getTime());
			System.out.println("Azimuth Angle = " + AziAng);
			System.out.println("Azimuth Pos = " + STP_01.SouthPos + " + " + AziAng + " = " + (STP_01.SouthPos+AziAng));
			System.out.println(seperator);

			STP_01.MoveStepper(STP_01.SouthPos+AziAng);
			System.out.println("Position: " + STP_01.Position);
			System.out.println(seperator);
		}
		
		
		MAG_01.CloseChannel();
		STP_01.CloseChannel();

		/*
		LUX_01 = VH1.createLuxSensor(LUX_01_HP);
		LUX_01.OpenChannel();
		LUX_01.CloseChannel();
		
		cal.add(Calendar.HOUR_OF_DAY, -12);
		 */
	}

	private static void FindMagnet() {
		double MaxClwRotation = 180;
		double MaxAntiClwRotation = -180;
		int cnt = 0;
		
		System.out.println("START SCAN");
		System.out.println("Position: " + STP_01.Position);
		System.out.println("Magnet: " + MAG_01.On + "(" + MAG_01.VRIValue + ")");
		System.out.println(seperator);

		while (!MAG_01.On && STP_01.Position > MaxAntiClwRotation) {
			cnt++;
			STP_01.MoveStepper(STP_01.Position - 5);
			if (demo) {
				System.out.println("Move " + cnt);
				System.out.println("\tMagnet: " + MAG_01.On);
				System.out.println("\tPosition: " + STP_01.Position);
			}
		}

		if (!MAG_01.On && STP_01.Position <= MaxAntiClwRotation) {
			System.out.println("\n\t<3<3<3\n");
			System.out.println("Magnet not found in ANTI Clockwise Scan");

			STP_01.MoveStepper(0);	//move back to starting point

			System.out.println("\n\t<3<3<3\n");
		}

		while (!MAG_01.On && STP_01.Position < MaxClwRotation) {
			cnt++;
			STP_01.MoveStepper(Math.round(STP_01.Position + 5));
			if (demo) {
				System.out.println("Move " + cnt);
				System.out.println("\tMagnet: " + MAG_01.On);
				System.out.println("\tPosition: " + STP_01.Position);
			}
		}

		if (MAG_01.On) {
			if (cnt != 0 && demo == true) {
				System.out.println(seperator);	
			}
			System.out.println(" :: MAGNET FOUND ::");
			System.out.println("Moves\t\t = " + cnt );
			System.out.println("Position\t = " + STP_01.Position);
			System.out.println("VRI Value\t = " + MAG_01.VRIValue);
		}		
	}

	private static void CalculateSouth() {
		if (STP_01.Position <= 0) { //R1 (route 1) anticlockwise
			STP_01.setSouth(STP_01.Position+135);
			System.out.println("Anti-clockwise SUCCESS");
			System.out.println("SOUTH: " + STP_01.Position + " + 135 = " + (STP_01.Position+135));
		} else {	//R2
			STP_01.setSouth(STP_01.Position-225);
			System.out.println("Clockwise SUCCESS");
			System.out.println("SOUTH: " + STP_01.Position + " - 225 = " + (STP_01.Position-225));
		}
		System.out.println(seperator);
	}
}