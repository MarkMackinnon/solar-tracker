package Phidgets;
import com.phidget22.*;

public class VRISensor {

	public VoltageRatioInput vriPhi;
	String deviceName;
	public double VRIValue;

	public VRISensor(int portNumber, String label) {
		try {
			vriPhi = new VoltageRatioInput();
			deviceName = label;
			vriPhi.setHubPort(portNumber);
			vriPhi.setIsHubPortDevice(true);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Creating " + label);
			System.err.println(PhEx.getDescription());
		}

		vriPhi.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				try {
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC: " + vriPhi.getHubPort() + "/" + vriPhi.getChannel());
					
					if (VintHUB.demo) {
						System.out.println("\tData Interval: " + vriPhi.getDataInterval() + "ms");
						System.out.println("\tChange Trigger: " + vriPhi.getVoltageRatioChangeTrigger());
					}
					
					VintHUB.AttachPhidget(vriPhi);
				} 
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);			
			}
		});

		vriPhi.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				System.out.println(deviceName + " Dettached");
				System.out.println(VintHUB.separator);
			}
		});

		vriPhi.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.out.print(deviceName + " ");
				System.err.println("Error: " + ee.getDescription());
				System.out.println(VintHUB.separator);
			}
		});

		vriPhi.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent v) {
				VRIValue = v.getVoltageRatio();
			}
		});
	}

	public void setDataInterval(int di) {
		try {
			vriPhi.setDataInterval(di);
			System.out.println(deviceName + "\n\tData Interval set to: " + vriPhi.getDataInterval());
		} catch (PhidgetException PhEx) {
			System.out.println(deviceName);
			PhidgetErrHand.DisplayError(PhEx, "Setting Data Interval" );
		} 
	}

	public void setVoltageRatioChangeTrigger(double vct) {
		try {
			vriPhi.setVoltageRatioChangeTrigger(vct);
			System.out.println(deviceName + "\n\tVoltage Change Ratio set to: " + vriPhi.getVoltageRatioChangeTrigger());
		} catch (PhidgetException PhEx) {
			System.out.println(deviceName);
			PhidgetErrHand.DisplayError(PhEx, "Setting Data Change Trigger");
		} 
	}

	public Phidget getPhidget() {
		return this.vriPhi;	
	}

}
