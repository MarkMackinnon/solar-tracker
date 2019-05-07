package phidgets;

import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;

/**
 * VRI = Voltage Ratio Input
 * A type of Phidget sensor whose value is between 0 and 1.
 *
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 * 
 */
abstract class VRISensor extends PhidgetDevice {

	VRISensor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, deviceName);
		try {
			phi = new VoltageRatioInput();
			phi.setIsHubPortDevice(true);	//Explicit for Voltage Ratio Input sensors, in this project
			phi.setHubPort(portNumber);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Error Setting VRI Sensor; " + deviceName + " as a Hub Port Device");
		}
		addAttachListener();
		addDettachListener();
		addErrorListener();
	}
	
	/**
	 * @return the current voltage ratio of the Phidget
	 * @throws PhidgetException
	 */
	public double getValue() throws PhidgetException {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		return phi.getVoltageRatio();
	}	
}
