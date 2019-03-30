package Phidgets;
import com.phidget22.*;

public abstract class VintDevice {

	protected Phidget phi;
	protected int portNumber;
	public double value;
	protected String deviceName;
	
	public VintDevice(VintHUB vh, String deviceName) {
		this.deviceName = deviceName;
		vh.devices.add(this);
		System.out.println(deviceName + " CREATED");
	}

	protected void addDettachListener(Phidget phi) {
		phi.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				System.out.println(deviceName + " Dettached");
				System.out.println(VintHUB.separator);
			}
		});
	}

	protected void addErrorListener(Phidget phi) {
		phi.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.out.flush();
				System.out.println(deviceName + " Error:");
				System.out.flush();
				System.err.println(ee.getDescription());
				System.err.flush();
				System.out.println(VintHUB.separator);
			}
		});
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public void OpenSetChannel(int channel) {
		System.out.println("Opening Phidget (" + deviceName + ") channel");
		try {
			phi.setChannel(channel);
			phi.open();	//hold channel open request on for attachment 
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + " (OPEN)");
			System.out.println(VintHUB.separator);
		}
		catch (PhidgetException PhEx) {
			System.err.println(deviceName + " channel opening failure");
			PhidgetErrHand.PrintOpenErrorMessage(PhEx, phi);
		}
		catch (NullPointerException NPE) {
			System.err.println(NPE.getMessage());
		}
	}
	
	public void CloseChannel() {
		System.out.println("Closing Phidget (" + deviceName + ") channel");
		try {
			phi.close();
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(CLOSED)");
			System.out.println(VintHUB.separator);	
		}
		catch (PhidgetException PhEx) {
			System.out.println(deviceName + "channel closing failure");
			System.err.println(PhEx.getErrorCode());
			System.err.println(PhEx.getDescription());
		}
		catch (NullPointerException NPE) {
			System.err.println(NPE.getMessage());
		}
	}	
	
}
