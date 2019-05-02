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
	
	final DecimalFormat df = new DecimalFormat(".###");
	static final double maxVoltage = 30;	//that can pass through the sensor
	
	public VoltageSensor(PhidgetHub ph, int portNumber, String deviceName){
		super(ph, portNumber, deviceName);
	}
	
	/**
	 * @return voltage of solar panel
	 */
	public double getVoltage() {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		try {
			return Double.valueOf(df.format(phi.getVoltageRatio()*maxVoltage));
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
			return -1;
		}
	}
}