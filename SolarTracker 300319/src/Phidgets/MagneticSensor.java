package Phidgets;

import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;

public class MagneticSensor extends VRISensor {

	private boolean on;
	
	public MagneticSensor(VintHUB vh, int portNumber, String deviceName) {
		super(vh, portNumber, deviceName);
	}
	
	public boolean getMagAligned() {
		return on;
	}
	
	@Override
	protected void addVoltageRatioChangeListener() {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		phi.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent v) {
				value = v.getVoltageRatio();
				if (value > 0.8) {
					on = true;
				} else {
					on = false;
				}
			}
		});
	}
	
}
