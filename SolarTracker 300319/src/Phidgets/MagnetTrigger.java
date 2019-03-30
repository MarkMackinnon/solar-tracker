package Phidgets;

public class MagnetTrigger extends Thread {
	
	@Override
	public void run()
	{
		synchronized (this) {
			System.out.println("ThreadA is running");
			notify();
			System.out.println("ThreadA called notify() on its own");
			try {
				System.out.println("ThreadA sleeps for 1 seconds");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("sleeping thread interrupted ");
			}
			System.out.println("ThreadA synchronization block completed");
		}
	}	
}