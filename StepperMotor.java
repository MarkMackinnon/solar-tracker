package Phidgets;
import com.phidget22.*;

/*
 * Stepper Motor Phidget: STC1001_0.
 * Unit: Position	1 Full Rotation of turntable = 40441
 * 1 degree = 112.37 steps
 */
public class StepperMotor {

	public Stepper stpPhi;
	public String deviceName;
	public double position;

	public StepperMotor(int portNumber, String label) {
		try {
			stpPhi = new Stepper();
			deviceName = label;
			stpPhi.setHubPort(portNumber);
			stpPhi.setIsHubPortDevice(false);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + label);
			System.err.println(PhEx.getDescription());
		}

		stpPhi.addAttachListener(new AttachListener()  {
			public void onAttach(AttachEvent ae) {
				try {
					//Set Acceleration
					stpPhi.setAcceleration(2000);

					//Set Velocity
					stpPhi.setVelocityLimit(7000);

					//Set Scaling Factor
					stpPhi.setRescaleFactor(1/112.35);	//degree = step

					//Set Data Interval
					stpPhi.setDataInterval(200);

					//Set Current Limit
					stpPhi.setCurrentLimit(0.8);

					//Set Holding Current Limit
					stpPhi.setHoldingCurrentLimit(0.5);

					//Engaging Servo-Stepper Motor
					stpPhi.setEngaged(true);
					
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + stpPhi.getHubPort() + "/" + stpPhi.getChannel());

					if (VintHUB.demo) {
						System.out.println("\tAcceleration " + stpPhi.getAcceleration());
						System.out.println("\tVelocity " + stpPhi.getVelocityLimit());
						System.out.println("\tScale factor set to " + stpPhi.getRescaleFactor());
						System.out.println("\tData Interval: " + stpPhi.getDataInterval() + "ms");
						System.out.println("\tTravel Amperage: " + stpPhi.getCurrentLimit());
						System.out.println("\tHolding Amperage: " + stpPhi.getHoldingCurrentLimit());
						System.out.println("\tStepper Engaged");
					}
					
					VintHUB.AttachPhidget(stpPhi);
				}
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);
			}
		});

		stpPhi.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				System.out.println(deviceName + " Dettached");
				System.out.println(VintHUB.separator);
			}
		});

		stpPhi.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.out.print(deviceName + " ");
				System.err.println("Error: " + ee.getDescription());
				System.out.println(VintHUB.separator);
			}
		});

		stpPhi.addPositionChangeListener(new StepperPositionChangeListener() {
			public void onPositionChange(StepperPositionChangeEvent pos) {
				position = pos.getPosition();
			}
		});
	}

	public void MoveStepper(double target) {
		target = Math.round(target);
		try {
			if (stpPhi.getAttached()) {
				stpPhi.setTargetPosition(target);
				try {
					while (stpPhi.getIsMoving()) {
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
		stpPhi.setControlMode(StepperControlMode.RUN);
	}

	public void setSTPControlModeSTEP() throws PhidgetException {
		stpPhi.setControlMode(StepperControlMode.STEP);
	}

}