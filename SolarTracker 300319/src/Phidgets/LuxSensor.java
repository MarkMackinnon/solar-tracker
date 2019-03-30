package Phidgets;

// imports
import com.phidget22.PhidgetException;

import java.text.DecimalFormat;

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
public class LuxSensor extends VintDevice{
	
	DecimalFormat df = new DecimalFormat(".##");

	public LuxSensor(VintHUB vh, int portNumber, String deviceName) {
		super(vh, deviceName);
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
		super.addDettachListener(phi);
		super.addErrorListener(phi);
		addVoltageRatioChangeListener();
		setPhidget();
	}
	
	protected void addAttachListener() {
		LightSensor phi = (LightSensor) super.phi;
		phi.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				try {
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + phi.getHubPort() + "/" + phi.getChannel());
					if (VintHUB.demo) {
						System.out.println("\tDataInterval: " + phi.getDataInterval() + "ms");
						System.out.println("\tLux ChangeTrigger set to " + phi.getIlluminanceChangeTrigger());
					}
				}
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);
			}
		});
	}
	
	protected void addVoltageRatioChangeListener() {
		LightSensor phi = (LightSensor) super.phi;
		phi.addIlluminanceChangeListener(new LightSensorIlluminanceChangeListener() {
			public void onIlluminanceChange(LightSensorIlluminanceChangeEvent lux) {
				value = Double.valueOf(df.format(lux.getIlluminance()));
			}
		});
	}

	protected boolean getAttached() {
		try {
			return phi.getAttached();	
		} catch (PhidgetException PhEx) {
			return false;
		}
	}
	
	protected void setPhidget() {
		super.phi = phi;
	}
}
