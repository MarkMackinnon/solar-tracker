package Phidgets;
import com.phidget22.*;

public class PhidgetErrHand {

	static void DisplayError(PhidgetException err, String message) {

		System.out.println("\nError " + message);
		System.err.println("Desc: " + err.getDescription());

		if (err.getErrorCode() == com.phidget22.ErrorCode.WRONG_DEVICE) {
			System.err.println("\tThis error commonly occurs when the Phidget function you are calling does not match the class of the channel that called it.");
			System.err.println("\tFor example, you would get this error if you called a PhidgetVoltageInput_* function with a PhidgetDigitalOutput channel.");
		}
		else if (err.getErrorCode() == com.phidget22.ErrorCode.NOT_ATTACHED) {
			System.err.println("\tThis error occurs when you call Phidget functions before a Phidget channel has been opened and attached.\n");
			System.err.println("\tTo prevent this error, ensure you are calling the function after the Phidget has been opened and the program has verified it is attached.\n");
		}
		else if (err.getErrorCode() == com.phidget22.ErrorCode.NOT_CONFIGURED) {
			System.err.println("\tThis error code commonly occurs when you call an Enable-type function before all Must-Set Parameters have been set for the channel.\n");
			System.err.println("\tCheck the API page for your device to see which parameters are labled \"Must be Set\" on the right-hand side of the list.");
		}
	}

	static void PrintOpenErrorMessage(PhidgetException e,  Phidget phi) {

		DisplayError(e, "Opening Phidget Channel");
		
		if (e.getErrorCode() == com.phidget22.ErrorCode.TIMEOUT) {
			System.err.println("\nThis error commonly occurs if your device is not connected as specified, " +
					"or if another program is using the device, such as the Phidget Control Panel.\n\n" +
					"If your Phidget has a plug or terminal block for external power, ensure it is plugged in and powered.\n");
			try {

				ChannelClass channelClass = phi.getChannelClass();

				if (channelClass != com.phidget22.ChannelClass.VOLTAGE_INPUT
						&& channelClass != com.phidget22.ChannelClass.VOLTAGE_RATIO_INPUT
						&& channelClass != com.phidget22.ChannelClass.DIGITAL_INPUT
						&& channelClass != com.phidget22.ChannelClass.DIGITAL_OUTPUT) {
					System.err.println("\nIf you are trying to connect to an analog sensor, you will need to use the " +
							"corresponding VoltageInput or VoltageRatioInput API with the appropriate SensorType.\n");
				}

			}
			catch (PhidgetException PhEx) {
				System.err.println(PhEx.getMessage());
				System.err.println(PhEx.getDescription());
			}
		}	
	}
}