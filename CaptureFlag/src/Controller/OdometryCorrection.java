//Odo correction works
//possible thing to add is to not do correction while the robot is turning
package Controller;

import Robot.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private LightPoller csPoller;
	
	private static double lightVal = 0;
	private static final double LIGHT_THRESHOLD = 350;
	private static double SENSOR_POS_X = 9.75;
	private static double SENSOR_POS_Y = 8.0;
	

	// constructor
	public OdometryCorrection(Odometer odometer, LightPoller csPollerLineReader) {
		this.odometer = odometer;
		this.csPoller = csPollerLineReader;
	}

	public void run() {
		long correctionStart, correctionEnd;
		csPoller.setFloodLight(Colour.GREEN);
		

		while (true) {
			correctionStart = System.currentTimeMillis();

			double x, y, theta;

			lightVal = csPoller.getColourVal();
			if (lightVal < LIGHT_THRESHOLD) {
				theta = odometer.getAngle();

				if (theta > 315 || theta < 45 || (135 < theta && theta < 225)) { // affects y
					y = odometer.getY();

					y = inversePositionY(y, theta);
					y = nearest30(y);
					y = centerPositionY(y, theta);
					odometer.setPosition(new double[]{0, y, 0}, new boolean[]{false, true, false});
				} 
				else { // affects x

					x = odometer.getX();

					x = inversePositionX(x, theta);
					x = nearest30(x);
					x = centerPositionX(x, theta);
					odometer.setPosition(new double[]{x, 0, 0}, new boolean[]{true, false, false});
				}
			}

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

	public static double nearest30(double value) {

		if (value < 30.48) { // if
			return 30.48;
		}

		value = (int) (value * 100);
		int q = (int) value;
		int remainder = q % 3048;
		int dividend = q / 3048;
		int a = 0;
		if (remainder > 1524) {
			a = 3048;
		}
		return ((double) (dividend * 3048 + a)) / 100;
	}

	/*
	 * This method compensates for the fact that the robot's starting position
	 * is considered (0,0) by subtracting GRID_OFFSET from the initial (x,y).
	 * Then, it compensates for the fact that the sensor is ahead of the center
	 * axle by 4.5cm by adding or subtracting that value to x or y, based on the
	 * rotation of the robot (theta)
	 */
	public double centerPositionX(double x, double theta) {
		if (theta > 45 && theta < 135) { // x is changing positively
			x -= SENSOR_POS_X;
		} else { // x is changing negatively
			x += SENSOR_POS_X;
		}
		return x;
	}

	public double inversePositionX(double x, double theta) {
		if (theta > 45 && theta < 135) { // x is changing positively
			x += SENSOR_POS_X;
		} else { // x is changing negatively
			x -= SENSOR_POS_X;
		}
		return x;
	}

	public double centerPositionY(double y, double theta) {
		if (theta > 315 || theta < 45) { // x is changing positively
			y -= SENSOR_POS_Y;
		} else { // x is changing negatively
			y += SENSOR_POS_Y;
		}
		return y;
	}

	public double inversePositionY(double y, double theta) {
		if (theta > 315 || theta < 45) { // x is changing positively
			y += SENSOR_POS_Y;
		} else { // x is changing negatively
			y -= SENSOR_POS_Y;
		}
		return y;
	}

	public static double getLightVal() {
		return lightVal;
	}
}