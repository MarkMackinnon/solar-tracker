package phidgets;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.PhidgetException;
import com.phidget22.Stepper;

/**
 * Bespoke stepper motor class for the azimuth (horizontal) axis
 * 
 * Attach Listener contains optimum settings for moving solar panel and stepping in degrees.
 * 
 * Find magnet calls aligns moves anti-clockwise 5 degrees for 185 degrees or until the magnet and magnetic
 * 	sensor are aligned
 * 
 * MoveASMToPosition moves the solar panel once the magnet has been found, keeping below 40/360 degrees.
 * 	These boundaries are defined by the mechanical constraints of the system.
 * 
 * Target out of bounds exception is thrown when the target requested is below 90/above 270
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
public class AzimuthStepperMotor extends StepperMotor {

	private int moveCounter = 0;
	private double azimuthPositionalOffset;
	private double MinBearing = 40, MaxBearing = 360;

	public AzimuthStepperMotor(PhidgetHub ph, int portNumber, String deviceName) {
		super(ph, deviceName);
		try {
			phi = new Stepper();
			phi.setHubPort(portNumber);
		} catch (PhidgetException e) {
			System.out.println(deviceName + " failed to set Hub Port ID");
		}
		
		addAttachListener();
		addDettachListener();
		addErrorListener();
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
	 * 2 part method.
	 * 1st. magnetScan - moving of the turntable
	 * 2nd. analysis of magnet/sensor alignment
	 * @param magSnsr : a MagneticSensor that operates as a switch for this function.
	 */
	public void findMagnet(MagneticSensor magSnsr) {
		int positionLimit = -185;	//step
		System.out.println("Scanning anti-clockwise sector for magnetic sensor\n");
		
		try {
			while (magSnsr.phi.getAttached() && this.phi.getAttached() 
					&& !magSnsr.getMagAligned() && (position()-5) >= positionLimit) {
				moveCounter++;				
				Stepper phi = (Stepper) this.phi;
				try {
					if (phi.getAttached()) {
						phi.setTargetPosition(position()-5);
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
			
			if (magSnsr.getMagAligned()) {
				if (PhidgetHub.demo) {	//demo mode displays movement data
					System.out.print("Move " + moveCounter);
					System.out.println("\tMagnet v: " + magSnsr.getValue());
					System.out.println("\tPosition: " + position() + "\n");
				}
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
	 * Check target is between 90 degrees (East) and 270 degrees (West)
	 * If it is, Move. If not. Terminate.
	 * @param target
	 * @throws TargetPositionOutOfBoundsException 
	 */
	public void moveASMToPosition(double target) throws TargetPositionOutOfBoundsException {
		if (target < MinBearing || target > MaxBearing) {
			throw new TargetPositionOutOfBoundsException("Movement error: Request outside of Northern Hemisphere\n"
					+ "operation bounds (" + MinBearing + " - " + MaxBearing + " degrees)\n"
					+ "Program Terminating");
		}
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
	 * Catches the entry for Daylight Savings configuration when its not a boolean entry
	 *
	 */
	public static class TargetPositionOutOfBoundsException extends Exception {
		/**
		 * 
		 */
		public static final long serialVersionUID = 1L;

		TargetPositionOutOfBoundsException(String message){
			super(message);
		}
	}
}
