package Phidgets;

// imports
import com.phidget22.PhidgetException;
import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.LightSensor;
import com.phidget22.LightSensorIlluminanceChangeEvent;
import com.phidget22.LightSensorIlluminanceChangeListener;
import com.phidget22.Phidget;

/**
 * Light Sensor - Phidget LUX1000_0
 * @author mac
 * 
 * LuxSensor is the 'lock' for the LightSensor Phidget
 */
public class LUXSensor {

	LightSensor luxPhi;	//com.phidget22
	String deviceName;

	public double LuxValue;

	public LUXSensor(int portNumber, String label) {
		try {
			luxPhi = new LightSensor();
			deviceName = label;
			luxPhi.setHubPort(portNumber);
			luxPhi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + label);
			System.err.println(PhEx.getDescription());
		}

		luxPhi.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				try {
					//Set Change Trigger
					luxPhi.setIlluminanceChangeTrigger(0.1);
					
					System.out.println("Light Sensor Attached:");
					System.out.println("\tPC: " + luxPhi.getHubPort() + "/" + luxPhi.getChannel());

					if (VintHUB.demo) {
						System.out.println("\tDataInterval: " + luxPhi.getDataInterval() + "ms");
						System.out.println("\tLux ChangeTrigger set to " + luxPhi.getIlluminanceChangeTrigger());
					}
					
					VintHUB.AttachPhidget(luxPhi);
				}
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);
			}
		});

		luxPhi.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				System.out.println("Light Sensor Dettached");
				System.out.println(VintHUB.separator);
			}
		});

		luxPhi.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.err.println("Error: " + ee.getDescription());
			}
		});

		luxPhi.addIlluminanceChangeListener(new LightSensorIlluminanceChangeListener() {
			public void onIlluminanceChange(LightSensorIlluminanceChangeEvent lux) {
				LuxValue = lux.getIlluminance();
				System.out.println("LUX = " + LuxValue);
			}
		});
	}
	
	public Phidget getPhidget() {
		return this.luxPhi;	
	}
}
