package Phidgets;

import java.util.ArrayList;
import com.phidget22.*;

public class VintHUB {

	static String separator;
	static boolean demo;

	int HubDSN;

	public static ArrayList<VintDevice> devices = new ArrayList<VintDevice>();

	public VintHUB(int HubSN, boolean d, String s) {
		this.HubDSN = HubSN;
		demo = d;
		separator = s;
		devices.clear();
	}

	public TurntableMotor createTurntableMotor(int portNumber, String label) throws PhidgetException {
		return new TurntableMotor(portNumber, label);
	}

	public MagneticSensor createMagneticSensor(int portNumber,  String label) throws PhidgetException {
		return new MagneticSensor(portNumber, label);
	}

	public LUXSensor createLightSensor(int portNumber,  String label) throws PhidgetException {
		return new LUXSensor(portNumber, label);
	}

	public boolean catchPhidgetsAttached() {
		//boolean allAttached = false;
		int loopCount = 0;
		//int phidgetAttCount = phidgetCount;	//attached until proven otherwise
		ArrayList<VintDevice> devicesNotAttached = new ArrayList<VintDevice>();

		try {
			do{
				devicesNotAttached.clear();
				for (VintDevice p : devices) {
					if (!p.phi.getAttached()) {
						devicesNotAttached.add(p);
						Thread.sleep(300);
					}
				}
				loopCount++;
				if (loopCount == 5) {	break;	}
			} while (devicesNotAttached.size() != 0);
		} catch (PhidgetException PhEx) {
			System.err.println("Error getting Phidgets attached to the VintHUB: " + HubDSN);
			System.err.println(PhEx.getMessage());
		} catch (InterruptedException IE) {
			System.err.println(IE.getMessage());
		}

		if (devicesNotAttached.isEmpty()) {
			System.out.println("All Phidgets attached");
			System.out.println(separator);
			return true;
		} else {
			if (demo) {
				System.out.println("Phidgets created = " + devices.size());
				System.out.println("Phidgets attached = " + (devices.size() - devicesNotAttached.size()));
			}
			for (VintDevice p : devicesNotAttached) {
				
				System.err.println(p.deviceName + " NOT ATTACHED");
			}
			System.out.println(separator);
			return false;
		}

	}
}
