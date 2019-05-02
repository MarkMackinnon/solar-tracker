package phidgets;

import com.phidget22.PhidgetException;
import com.phidget22.LightSensor;

/**
 * Ambient Light Sensor Phidget attached to system to collect the ambient light value in UNIT; Lux
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 */
public class AmbientLightSensor extends PhidgetDevice{
	
	public AmbientLightSensor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, portNumber, deviceName);
		try {
			phi = new LightSensor();
			phi.setHubPort(portNumber);
			phi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Error Creating " + deviceName);
			System.err.println(PhEx.getDescription());
		}
		
		addAttachListener();
		addDettachListener();
		addErrorListener();
	}
	
	/**
	 * @return current lux value
	 */
	public double getIlluminance() {
		LightSensor phi = (LightSensor) super.phi;
		try {
			return phi.getIlluminance();
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
			return -1;
		}
	}
}