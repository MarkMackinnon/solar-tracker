package Phidgets;

import java.text.DecimalFormat;

import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;

public class SolarVoltageSensor extends VRISensor {
	
	DecimalFormat df = new DecimalFormat(".##");
	double voltage = -1;
	double maxVoltage;
	
	public SolarVoltageSensor(VintHUB vh, int portNumber, String deviceName, double maxVoltage){
		super(vh, portNumber, deviceName);
		this.maxVoltage = maxVoltage;
	}
	
	protected void addVoltageRatioChangeListener() {
		VoltageRatioInput phi = (VoltageRatioInput) super.phi;
		phi.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent v) {
				value = Double.valueOf(df.format(v.getVoltageRatio()));;
				voltage = Double.valueOf(df.format((value-0.5)*maxVoltage));
			}
		});
	}
	
	public double getVoltage() {
		return voltage;
	}
}
