package Phidgets;

// imports
import com.phidget22.PhidgetException;
import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.LightSensor;
import com.phidget22.LightSensorIlluminanceChangeEvent;
import com.phidget22.LightSensorIlluminanceChangeListener;

/**
 * Light Sensor - Phidget LUX1000_0
 * @author mac
 * 
 * LuxSensor is the 'lock' for the LightSensor Phidget
 */
public class LUXSensor extends VintDevice{

	protected static LightSensor luxPhi;	//com.phidget22
	public double LuxValue;

	public LUXSensor(int portNumber, String deviceName) {
		super(deviceName);
		try {
			luxPhi = new LightSensor();
			luxPhi.setHubPort(portNumber);
			luxPhi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + deviceName);
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
				}
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);
			}
		});

		luxPhi.addIlluminanceChangeListener(new LightSensorIlluminanceChangeListener() {
			public void onIlluminanceChange(LightSensorIlluminanceChangeEvent lux) {
				LuxValue = lux.getIlluminance();
				System.out.println("LUX = " + LuxValue);
			}
		});
	}

	protected boolean getAttached() {
		try {
			return luxPhi.getAttached();	
		} catch (PhidgetException PhEx) {
			return false;
		}
	}
}
