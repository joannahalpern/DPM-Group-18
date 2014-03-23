package Controller;
/*
 * DPM Lab 2 - OdometryCorrection
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
import Robot.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private LightPoller csPoller;
	

	// constructor
	public OdometryCorrection(Odometer odometer, LightPoller csPollerLineReader) {
		this.odometer = odometer;
		this.csPoller = csPollerLineReader;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}