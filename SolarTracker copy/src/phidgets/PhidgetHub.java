package phidgets;

import java.util.ArrayList;
import com.phidget22.PhidgetException;

/**
 * Harbour to the Phidget devices.
 *
 * Phidget attachment is checked here
 * Console detail seperation is captured here
 *
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 */
public class PhidgetHub {

	private int phSN;	//Phidget Hub serial number
	protected ArrayList<PhidgetDevice> phidgetDevices = new ArrayList<PhidgetDevice>();	//Added to by Phidgets on instantiation of the PhidgetDevice
	static String separator;
	static boolean demo;

	public PhidgetHub(int DSN, boolean demo, String separator) {
		this.phSN = DSN;
		PhidgetHub.demo = demo;
		PhidgetHub.separator = separator;
		System.out.println("Phidget Hub ID:" + phSN + " created.");
		System.out.println(separator);
	}

	/**
	 * Checks to see if every Phidget linked to this Phidget Hub is attached to the Hub. Cycles through PhidgetDevices added to the phidgetDevices list
	 * @return TRUE if every Phidget is attached
	 * @return FALSE if not every Phidget is attached; after 5 attempts and 1.5 seconds (0.3seconds of buffer in between each scan).
	 */
	public boolean allPhidgetsAttached() {
		ArrayList<PhidgetDevice> devicesNotAttached = new ArrayList<PhidgetDevice>();
		int attempts = 0;

		do {
			devicesNotAttached.clear();
			for (PhidgetDevice pd : phidgetDevices) {
				try {
					if (pd.phi.getAttached() == false) {
						devicesNotAttached.add(pd);
					}
				} catch (PhidgetException PhEx) {
					System.err.println("Error getting Phidgets attached to the Phidget Hub: " + phSN);
					System.err.println(PhEx.getMessage());
				}
			}

			if(devicesNotAttached.isEmpty()) {break;}

			try {
				Thread.sleep(300);
			} catch (InterruptedException ie) {
				System.err.println(ie.getMessage());
			}
			attempts++;
		} while (attempts < 5);

		if (devicesNotAttached.isEmpty()) {
			return true;
		} else {
			//Display which phidgets were not attached
			if (demo) {
				System.out.println("Phidgets created = " + phidgetDevices.size());
				System.out.println("Phidgets attached = " + (phidgetDevices.size() - devicesNotAttached.size()));
				System.out.println(separator);
			}
			for (PhidgetDevice vd : devicesNotAttached) {
				System.err.println(vd.deviceName + " NOT ATTACHED");
			}
			return false;
		}
	}

	/**
	 * Closes the channels of all the Phidgets connected to this hub
	 */
	public void closePhidgetChannels() {
		for (PhidgetDevice pd : phidgetDevices) {
			try {
				pd.closeChannel();
			} catch (NullPointerException | PhidgetException e) {
				System.out.println(pd.deviceName + " channel closing error");
				System.err.println(e.getMessage());
			}
		}
	}
}