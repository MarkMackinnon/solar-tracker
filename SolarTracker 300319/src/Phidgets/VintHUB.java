package Phidgets;

import java.util.ArrayList;
import com.phidget22.*;

/**
 * To represent the physical system, the VintHub class is liken to the Vint Hub, where the phidgets are connected via wires, terminals and 1 USB to the computer.
 * For maintenance purposes. This class has its own serial number so that multiple Hubs can be used and their phidgets associtated with them.
 * 
 * @author mac -Mark Andrew MacKinnon {mark1mackinnon@outlook.com}
 */
public class VintHUB {

	protected int HubDSN;	//unique serial number
	protected ArrayList<VintDevice> devices = new ArrayList<VintDevice>();

	static boolean demo;
	static String separator;
	
	/**
	 * @param HubSN	//device serial number
	 * @param demo	//compilation mode. set in the main, used by package
	 * @param separator	//printed in console by the package
	 */
	public VintHUB(int HubSN, boolean demo, String separator) {
		this.HubDSN = HubSN;
		VintHUB.demo = demo;
		VintHUB.separator = separator;
	}

	/**
	 * Moves through the ArrayList (devices) checking if their Phidget (phi) is attached
	 * @return TRUE if all the VintDevice Phidgets are attached
	 */
	public boolean catchPhidgetsAttached() {
		int loopCount = 0;
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
				System.out.println(separator);
			}
			for (VintDevice d : devicesNotAttached) {
				System.err.println(d.deviceName + " NOT ATTACHED");
			}
			System.exit(1);
			return false;
		}
	}

}
