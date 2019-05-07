package phidgets;

import java.text.DecimalFormat;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;

/**
 * Voltage Sensor for Solar Panel
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class VoltageSensor extends VRISensor {
	
	public VoltageSensor(PhidgetHub ph, int portNumber, String deviceName){
		super(ph, portNumber, deviceName);
		try {
			phi.setHubPort(portNumber);
		} catch (PhidgetException e) {
			System.out.println(deviceName + " failed to set Hub Port ID");
		}
	}
	
	/**
	 * @return voltage of solar panel
	 */
	public double getVoltage() {
		final double maxVoltage = 30;	//Maxium voltage that can be detected by the sensor
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		final DecimalFormat df = new DecimalFormat("##.##");
		try {
			return Double.valueOf(df.format((phi.getVoltageRatio()-0.5)*maxVoltage));
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
			return -1;
		}
	}
}