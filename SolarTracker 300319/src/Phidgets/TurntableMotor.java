package Phidgets;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.PhidgetException;
import com.phidget22.Stepper;
import com.phidget22.StepperPositionChangeEvent;
import com.phidget22.StepperPositionChangeListener;

public class TurntableMotor extends StepperMotor{
	
	public double southPos;
	public double minPos;
	public double maxPos;
	
	public TurntableMotor(VintHUB vh, int portNumber, String deviceName) {
		super(vh, portNumber, deviceName);
		southPos = 0;
	}
	
	public void SetSouth() {
		if (value <= 0) { //anti-clockwise rotation
			southPos = value+135;
			System.out.println("Anti-clockwise SUCCESS");
			System.out.println("SOUTH = " + value + " + 135 = " + (value+135));
		} else {	//clockwise rotation
			southPos = value-225;
			System.out.println("Clockwise SUCCESS");
			System.out.println("SOUTH = " + value + " - 225 = " + (value-225));
		}
		System.out.println(VintHUB.separator);
	}
	
	/*
	 * 1 Full Rotation of Turning Table = 40441 units
	 * 1 degree = 40441/360 = 112.37 units
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
	
	@Override
	protected void addPositionChangeListener() {
		Stepper phi = (Stepper) super.phi; 
		phi.addPositionChangeListener(new StepperPositionChangeListener() {
			public void onPositionChange(StepperPositionChangeEvent pos) {
				value = Math.round(pos.getPosition());
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	public double getCurrentHeading() {
		return value - southPos;
	}
}
