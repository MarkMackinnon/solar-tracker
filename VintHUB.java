package Phidgets;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import com.phidget22.*;

public class VintHUB {

	static String separator;
	static boolean demo;
	
	int HubDSN;
	
	Enumeration<Phidget> Phidgets = null; 
	public static Vector<Phidget> PhV = new Vector<>();
	
	public VintHUB(int HubSN, boolean d, String s) {
		this.HubDSN = HubSN;
		demo = d;
		separator = s;
		PhV.clear();
	}
	
	public VintHUB(int HubSN) {
		this.HubDSN = HubSN;
		PhV.clear();
	}
	
	public TurntableMotor createTurntableMotor(int portNumber, String label) throws PhidgetException {
		return new TurntableMotor(portNumber, label);
	}
	
	public MagneticSensor createMagneticSensor(int portNumber,  String label) throws PhidgetException {
		return new MagneticSensor(portNumber, label);
	}

	public LUXSensor createLightSensor(int portNumber,  String label) throws PhidgetException {
		return new LUXSensor(portNumber, label);
	}

	public boolean PhidgetsAttached() {
		ArrayList<Boolean> phidgetConList =  new ArrayList<Boolean>();

		Phidgets = PhV.elements();
		int i = 0;

		while (Phidgets.hasMoreElements()) {
			Phidget phi = (Phidget) Phidgets.nextElement();
			try {
				if(phi.getAttached()){
					i--;
				}
			} catch (PhidgetException PhEx) {
				System.err.println("Error getting Phidgets Attached to the VintHUB: " + HubDSN);
				System.err.println(PhEx.getDescription());
				return false;
			}
		}

		System.out.println("PhidgetConList Length = " + phidgetConList.size());

		if (i<0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void AttachPhidget(Phidget phi) {
		PhV.add(phi);
	}

	public void OpenSetChannel(Phidget phi, int channel) {
		try {
			phi.setChannel(channel);
			phi.open();	//hold channel open request on for attachment 
			System.out.println("PC:" + phi.getHubPort() + "/" + + phi.getChannel() + "\t(OPEN)");
			System.out.println(VintHUB.separator);
		}
		catch (PhidgetException PhEx) {
			PhidgetErrHand.PrintOpenErrorMessage(PhEx, phi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void CloseChannel(Phidget phi) {
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

	public boolean getAttached(Phidget phi, String phiName) {
		try {
			return phi.getAttached();
		} catch (PhidgetException PhEx) {
			System.err.println("Getting boolean attached, for " + phiName + "\nReturned default; FALSE");
			return false;
		}
	}
}
