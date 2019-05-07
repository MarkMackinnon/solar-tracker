package phidgets;

import java.text.DecimalFormat;
import com.phidget22.PhidgetException;
import com.phidget22.Stepper;

/**
 * Basic Stepper Phidget behaviour
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class StepperMotor extends PhidgetDevice {

	StepperMotor(PhidgetHub ph, String deviceName) {
		super(ph, deviceName);
	}

	/**
	 * Position of stepper motor
	 * @return
	 */
	public double position() {
		DecimalFormat df = new DecimalFormat("#.##");
		Stepper phi = (Stepper) this.phi;
		try {
			return Double.valueOf(df.format(phi.getPosition()));
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
			return -1;
		}
	}

	/**
	 * Cuts stepper from the power supply.
	 * @throws PhidgetException 
	 */
	protected void disengageStepperMotor() {
		Stepper phi = (Stepper) this.phi;
		try {
			if (phi.getAttached()) {
				phi.setEngaged(false);
				System.out.println(deviceName + " dissengaged");
				System.out.println(PhidgetHub.separator);
			}
		} catch (PhidgetException e) {
			System.out.println(deviceName + ": error dissengaging stepper motor");
		}
	}

	/**
	 * Disengages the stepper motor and closes the channel for the stepper Phidget
	 */
	@Override
	public void closeChannel() throws PhidgetException {
		System.out.println("Disengaging stepper motor (" + deviceName + ")");
		disengageStepperMotor();
		System.out.println("Closing Phidget channel (" + deviceName + ")");
		phi.close();
		System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(CLOSED)");
		System.out.println(PhidgetHub.separator);
	}

}