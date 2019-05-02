package phidgets;

import com.phidget22.PhidgetException;

/**
 * Obtains value of magnetic sensor which is used a switch to orientate the turntable on a compass heading
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class MagneticSensor extends VRISensor{

	public MagneticSensor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, portNumber, deviceName);
	}

	/**
	 * When the value of the sensor is above 0.8 the magnet is above it.
	 * @return
	 * @throws PhidgetException
	 */
	public boolean getMagAligned() throws PhidgetException {
		if (this.getValue() > 0.8) {
			return true;
		} else {
			return false;
		}
	}
}