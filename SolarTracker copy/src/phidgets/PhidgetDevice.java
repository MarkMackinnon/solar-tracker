package phidgets;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;

/**
 * Phidgets sharing the same behaviors are extended from this class
 * 
 * @author Mark MacKinnon {mark1mackinnon@outlook.com}
 *
 */
abstract class PhidgetDevice {

	protected Phidget phi;
	protected String deviceName;

	PhidgetDevice(PhidgetHub ph, int portNumber, String deviceName) {
		this.deviceName = deviceName;
		System.out.println(deviceName);
		ph.phidgetDevices.add(this);		
	}

	/**
	 * Before a Phidget can attach, the channel for attachment must be "opened" - part of the Phidget methodology
	 * @param channel
	 */
	public void openSetChannel(int channel) {
		System.out.println("Opening comm channel (" + deviceName + ")");
		try {
			phi.setChannel(channel);
			phi.open();	//hold channel open request on for attachment 
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + " (OPEN)");
			System.out.println(PhidgetHub.separator);
		} catch (PhidgetException PhEx) {
			System.err.println(deviceName + " channel opening failure");
		} catch (NullPointerException NPEx) {
			System.err.println(NPEx.getMessage());
		}
	}

	/**
	 * When a Phidget completes its handshake after the comm channel has been opened. This listener is triggered by that phidget
	 */
	protected void addAttachListener() {
		phi.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				try {
					System.out.print(deviceName + " Attached @ ");
					System.out.println("PC:" + phi.getHubPort() + "/" + phi.getChannel());
				} catch (PhidgetException PhEx) {
					System.out.println("Error Attaching " + deviceName);
				}
				System.out.println(PhidgetHub.separator);
			}
		});
	}

	/**
	 * When a Phidget separates from the Phidget Hub the program is notified here
	 */
	protected void addDettachListener() {
		phi.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				System.out.println(deviceName + " Dettached");
				System.out.println(PhidgetHub.separator);
			}
		});
	}

	/**
	 * Some errors are caught here. A suggested routine in the Phidget examples
	 */
	protected void addErrorListener() {
		phi.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				System.err.println(ee.getCode());
				System.err.println(ee.getDescription());
			}
		});
	}

	/**
	 * Closing the channel of the phidget.
	 * @throws PhidgetException 
	 */
	public void closeChannel() throws PhidgetException, NullPointerException {
		System.out.println("Closing Phidget channel (" + deviceName + ")");
		phi.close();
		System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(CLOSED)");
		System.out.println(PhidgetHub.separator);
	}	

}