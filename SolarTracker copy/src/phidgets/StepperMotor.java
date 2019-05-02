package phidgets;

import com.phidget22.*;
import java.text.DecimalFormat;

/**
 * Basic Stepper Phidget behaviour
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class StepperMotor extends PhidgetDevice {

	StepperMotor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, portNumber, deviceName);
		try {
			phi = new Stepper();
			phi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + deviceName);
			System.err.println(PhEx.getDescription());
		}
		
		addAttachListener();
		addDettachListener();
		addErrorListener();
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
	 * Move to stepper motor position
	 * @param target
	 */
	public void moveToPosition(double target) {
		Stepper phi = (Stepper) this.phi;
		try {
			if (phi.getAttached()) {
				phi.setTargetPosition(target);
				try {
					while (phi.getIsMoving()) {
						Thread.sleep(500);
					}
				} catch (InterruptedException IE) {
					System.err.println(IE.getMessage());
				}
			}
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
		}
	}

	/**
	 * Cuts stepper from the power supply.
	 * @throws PhidgetException 
	 */
	protected void disengageStepperMotor() {
		Stepper phi = (Stepper) this.phi;
		try {
			phi.setEngaged(false);
			System.out.println(deviceName + " dissengaged");
			System.out.println(PhidgetHub.separator);
		} catch (PhidgetException e) {
			System.out.println(deviceName + " motor not attached when trying dissengage");
			System.out.println("No error here...");
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