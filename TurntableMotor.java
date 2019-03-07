package Phidgets;

public class TurntableMotor extends StepperMotor{
	
	public double southPos;
	
	public TurntableMotor(int portNumber, String label) {
		super(portNumber, label);
	}
	
	public void SetSouth() {
		if (position <= 0) { //anti-clockwise rotation
			southPos = position+135;
			System.out.println("Anti-clockwise SUCCESS");
			System.out.println("SOUTH = " + position + " + 135 = " + (position+135));
		} else {	//clockwise rotation
			southPos = position-225;
			System.out.println("Clockwise SUCCESS");
			System.out.println("SOUTH = " + position + " - 225 = " + (position-225));
		}
		System.out.println(VintHUB.separator);
		
	}
}
