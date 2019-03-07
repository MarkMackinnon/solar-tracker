package Phidgets;
import com.phidget22.*;

abstract class VintDevice {

	protected Phidget phi;
	protected String deviceName = "unknown";
	protected int portNumber;
	
	protected VintDevice(String deviceName) {
		this.deviceName = deviceName;
		VintHUB.devices.add(this);
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
				System.out.print(deviceName + " ");
				System.err.println("Error: " + ee.getDescription());
				System.out.println(VintHUB.separator);
			}
		});
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public void OpenSetChannel(int channel) {
		try {
			phi.setChannel(channel);
			phi.open();	//hold channel open request on for attachment 
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(OPEN)");
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
		try {
			phi.close();
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(CLOSED)");
			System.out.println(VintHUB.separator);	
		}
		catch (PhidgetException PhEx) {
			System.out.println("Failed to Closed");
			System.out.println(VintHUB.separator);	
			System.err.println(PhEx.getErrorCode());
			System.err.println(PhEx.getDescription());
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}	
}
