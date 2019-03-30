package Phidgets;
import com.phidget22.*;

/*
 * Phidget phi is a Stepper motor. CLASS: Stepper
 * Phidget ID STC1001_0.
 */
public class StepperMotor extends VintDevice{

	StepperMotor(VintHUB vh, int portNumber, String deviceName) {
		super(vh, deviceName);
		try {
			phi = new Stepper();
			phi.setHubPort(portNumber);
			phi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + deviceName);
			System.err.println(PhEx.getDescription());
		}

		addAttachListener();
		super.addDettachListener(phi);
		super.addErrorListener(phi);
		addPositionChangeListener();
	}

	protected void addAttachListener() {
		Stepper phi = (Stepper) super.phi; 
		phi.addAttachListener(new AttachListener()  {
			public void onAttach(AttachEvent ae) {
				try {
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + phi.getHubPort() + "/" + phi.getChannel());

					if (VintHUB.demo) {
						System.out.println("\tAcceleration " + phi.getAcceleration());
						System.out.println("\tVelocity " + phi.getVelocityLimit());
						System.out.println("\tScale factor set to " + phi.getRescaleFactor());
						System.out.println("\tData Interval: " + phi.getDataInterval() + "ms");
						System.out.println("\tTravel Amperage: " + phi.getCurrentLimit());
						System.out.println("\tHolding Amperage: " + phi.getHoldingCurrentLimit());
						System.out.println("\tStepper Engaged");
					}
				}
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);
			}
		});
	}

	protected void addPositionChangeListener() {
		Stepper phi = (Stepper) super.phi; 
		phi.addPositionChangeListener(new StepperPositionChangeListener() {
			public void onPositionChange(StepperPositionChangeEvent pos) {
				value = Math.round(pos.getPosition());
			}
		});
	}

	public void MoveStepper(double target) {
		Stepper phi = (Stepper) super.phi; 
		target = Math.round(target);
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
		}
		catch (PhidgetException PhEx) {
			System.err.println(PhEx.getErrorCode());
			System.err.println(PhEx.getDescription());
		}
	}

	public void setSTPControlModeRUN() throws PhidgetException {
		Stepper phi = (Stepper) super.phi;
		phi.setControlMode(StepperControlMode.RUN);
	}

	public void setSTPControlModeSTEP() throws PhidgetException {
		Stepper phi = (Stepper) super.phi;
		phi.setControlMode(StepperControlMode.STEP);
	}

	public boolean getIsMoving() {
		try {
			Stepper phi = (Stepper) super.phi;
			return phi.getIsMoving();
		} catch (PhidgetException PhEx) {
			System.out.println("Creating " + deviceName);
			System.err.println(PhEx.getDescription());
		}
		return false;
	}
}