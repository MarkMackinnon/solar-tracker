package Phidgets;
import com.phidget22.*;

abstract class VRISensor extends VintDevice{

	VRISensor(VintHUB vh, int portNumber, String deviceName) {
		super(vh, deviceName);
		try {
			phi = new VoltageRatioInput();
			phi.setHubPort(portNumber);
			phi.setIsHubPortDevice(true);
		}
		catch (PhidgetException PhEx) {
			System.out.println("Error Creating " + deviceName);
			System.err.println(PhEx.getDescription());
		}
		addAttachListener();
		super.addDettachListener(phi);
		super.addErrorListener(phi);
		addVoltageRatioChangeListener();
		setPhidget();
	}
	
	protected void addAttachListener() {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		phi.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				try {
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + phi.getHubPort() + "/" + phi.getChannel());
					if (VintHUB.demo) {
						System.out.println("\tData Interval: " + phi.getDataInterval() + "ms");
						System.out.println("\tChange Trigger: " + phi.getVoltageRatioChangeTrigger());
					}
				} 
				catch (PhidgetException PhEx) {
					PhidgetErrHand.DisplayError(PhEx, "Attaching " + deviceName);
				}
				System.out.println(VintHUB.separator);			
			}
		});
	}
	
	protected void addVoltageRatioChangeListener() {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		phi.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent v) {
				value = v.getVoltageRatio();
			}
		});
	}
	
	protected void setDataInterval(int di) {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		try {
			phi.setDataInterval(di);
			System.out.println(deviceName + "\n\tData Interval set to: " + phi.getDataInterval());
		} catch (PhidgetException PhEx) {
			System.out.println(deviceName);
			PhidgetErrHand.DisplayError(PhEx, "Setting Data Interval" );
		}
	}

	protected void setVoltageRatioChangeTrigger(double vct) {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		try {
			phi.setVoltageRatioChangeTrigger(vct);
			System.out.println(deviceName + "\n\tVoltage Change Ratio set to: " + phi.getVoltageRatioChangeTrigger());
		} catch (PhidgetException PhEx) {
			System.out.println(deviceName);
			PhidgetErrHand.DisplayError(PhEx, "Setting Data Change Trigger");
		} 
	}

	protected void setPhidget() {
		super.phi = phi;
	}
}
