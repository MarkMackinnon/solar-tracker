package phidgets;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.PhidgetException;
import com.phidget22.Stepper;

/**
 * Bespoke for the azimuth (horizontal) axis stepper motor and turntable system
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class AzimuthStepperMotor extends StepperMotor {

	private int moveCounter = 0;
	private double azimuthPositionalOffset;

	public AzimuthStepperMotor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, portNumber, deviceName);
		try {
			phi.setHubPort(portNumber);
		} catch (PhidgetException PhEx) {
			PhEx.printStackTrace();
		}
	}

	/**
	 * Triggered by the Stepper Phidget attaching to Phidget Hub
	 * 
	 * Configuration most suited to the following values.
	 * 
	 * 1 Full Rotation of Turning Table = 40441 units
	 * 1 compass degree = 40441/360/12 = 112.37 units
	 */
	@Override
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
					phi.setRescaleFactor(1/112.35);	//step = degree

					//Set Data Interval
					phi.setDataInterval(200);

					//Set Current Limit
					phi.setCurrentLimit(0.85);

					//Set Holding Current Limit
					phi.setHoldingCurrentLimit(0.25);

					//Engaging Servo-Stepper Motor
					phi.setEngaged(true);

					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + phi.getHubPort() + "/" + phi.getChannel());

					if (PhidgetHub.demo) {
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
					System.out.println("Error Attaching " + deviceName);
				}
				System.out.println(PhidgetHub.separator);
			}
		});
	}

	/**
	 * @param magSnsr : a MagneticSensor that operates as a switch for this function.
	 */
	public void findMagnet(MagneticSensor magSnsr) {
		try {
			magnetScan(magSnsr);

			//magnet scan report
			if (magSnsr.getMagAligned()) {
				System.out.println(" :: MAGNET FOUND ::\n");
				System.out.println("Moves: " + moveCounter);
				System.out.println("Position = " + position());
				System.out.println("Magnet Value = " + magSnsr.getValue());
				System.out.println("Solar Panel bearing = 45degrees");
				
				/*
				 * When the magnet is found during the initial scan the solar panel is at 45 degrees.
				 * After this position instructions are given in degrees and the formula applied to the movements
				 */
				azimuthPositionalOffset = position();
				Stepper phi = (Stepper) this.phi;
				phi.addPositionOffset(-azimuthPositionalOffset+45);	//negating the current position sets the position to 0. Adding 45 aligns it with the solar panels bearing.
			} else {
				System.out.println("\nMagnet Not Found in anti-clockwise sector");
				System.out.println("Orientating the sytem has been unsuccessful");
				System.out.println(PhidgetHub.separator);
			}
		} catch (PhidgetException PhEx) {
			PhEx.getDescription();
			PhEx.printStackTrace();
		}
		System.out.println(PhidgetHub.separator);
	}


	/**
	 * Rotates anti-clockwise in increments of 5degrees.
	 * 
	 * Stopping above the magnet sensor or when the next position it will move to is more than 185 degrees less than the starting position
	 * 
	 * @param magSnsr : a MagneticSensor, whose value operates as a switch for this function.
	 * @throws PhidgetException : when checking if a Phidget is attached
	 */
	private void magnetScan(MagneticSensor magSnsr) throws PhidgetException {
		int positionLimit = -185;	//step

		System.out.println("Scanning anti-clockwise sector for magnetic sensor\n");


		while (magSnsr.phi.getAttached() && this.phi.getAttached() && !magSnsr.getMagAligned() && (position()-5) >= positionLimit) {
			moveCounter++;
			moveToPosition(position()-5);

			if (PhidgetHub.demo) {	//demo mode displays movement data
				System.out.print("Move " + moveCounter);
				System.out.println("\tMagnet v: " + magSnsr.getValue());
				System.out.println("\tPosition: " + position() + "\n");
			}
		}
	}
}
