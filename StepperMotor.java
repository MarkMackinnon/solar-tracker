package Phidgets;
import com.phidget22.*;

/*
 * Stepper Motor Phidget: STC1001_0.
 * Unit: Position	1 Full Rotation of turntable = 40441
 * 1 degree = 112.37 steps
 */
public class StepperMotor extends VintDevice{

	//	protected static Stepper phi;
	public double position;

	public StepperMotor(int portNumber, String deviceName) {
		super(deviceName);
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
					//Set Acceleration
					phi.setAcceleration(2000);

					//Set Velocity
					phi.setVelocityLimit(7000);

					//Set Scaling Factor
					phi.setRescaleFactor(1/112.35);	//degree = step

					//Set Data Interval
					phi.setDataInterval(200);

					//Set Current Limit
					phi.setCurrentLimit(0.8);

					//Set Holding Current Limit
					phi.setHoldingCurrentLimit(0.5);

					//Engaging Servo-Stepper Motor
					phi.setEngaged(true);

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
				position = Math.round(pos.getPosition());
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
}