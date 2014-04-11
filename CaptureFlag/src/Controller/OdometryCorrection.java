//Odo correction works
//Need to start odoCorecction.start(); in Controller
package Controller;
import lejos.nxt.Sound;
import Robot.*;

public class OdometryCorrection extends Thread { //How frequently the correction iterates in ms
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private LightPoller csPoller;
	
	private static double lightVal = 0; // the value read by the colour sensor
	private static final double LIGHT_THRESHOLD = 300;
	private static double SENSOR_POS_X = 9.4; 	//The distance between the light sensor and the center of the robot
	private static double SENSOR_POS_Y = 7.5;
	//the distance btw grid lines
	private final double DISTANCE_BTWN_LINES = 30.48;
	

	// constructor
	public OdometryCorrection(Odometer odometer, LightPoller csPollerLineReader) {
		this.odometer = odometer;
		this.csPoller = csPollerLineReader;
		SENSOR_POS_X = -1 * TwoWheeledRobot.GROUND_LS_X_OFFSET;
		SENSOR_POS_Y = -1 * TwoWheeledRobot.GROUND_LS_Y_OFFSET;
	}
	/**
	 * If a line is detected when the robot is not, the odometer will be updated to a more accurate value.
	 * If it's headed in the x direction, only the y odometer reading will be corrected and vice versa.
	 */
	public void run() {
		long correctionStart, correctionEnd;
		csPoller.setFloodLight(Colour.GREEN);
		

		while (true) {
			correctionStart = System.currentTimeMillis();

			double x, y, theta;

			lightVal = csPoller.getColourVal();
			if (csPoller.lineSeen && (!Navigation.isTurning)) {
				Sound.beep();
				theta = odometer.getAngle(); 

				if (isYdirection(theta)) { // affects y
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
	/**What nearest30() does:
	 * This takes a value (which is the coordinate that the odometer is reporting at that time)
	 * and returns the multiple of 30.48 that that value is closest to.
	 * This is only called once the sensor sees a line
	 * 
	 * How nearest30 works:
	 *  -first it calculate the remainder of the input when divided by DISTANCE_BTWN_LINES
	 *  -It later (in the if statement) uses the remainder to determine if it should return the higher or lower line coordinate
	 *  -It next calculates the dividend of the input/tile length, which is the number of lines it has passed 
	 *  
	 *  Example:
	 *  -If the given value is 40, the remainder will be about 10 (40 mod 30). Since 10< half of 30, it returns the lower number.
	 *  -The dividend is 1, so the lower number it is returning is 1*30 + 0 = 30.
	 * 
	 *  -If the given value is 50, the remainder will be about 20 (50 mod 30). Since 20> half of 30, it returns the higher number.
	 *  -The dividend is still 1, so the lower number it is returning is 1*30 + 30 = 60.
	 */
	public double nearest30(double value) {

		if (value < DISTANCE_BTWN_LINES) { //if it is in the first tile, it returns the coordinate of the first line
			return DISTANCE_BTWN_LINES;
		}

		value = (int) (value * 100); //multiplied by 100 for specificity in int
		int q = (int) value;
		int remainder = q % 3048; //uses mod to get remainder
								  // 3048 is DISTANCE_BTWN_LINES*100
		int dividend = q / 3048; //since dividend is int, it is floored (eg 2.4134 become 2)
		int a = 0;
		if (remainder > 1524) { //if remainder is greater than half of DISTANCE_BTWN_LINES, then it goes to the higher value, else the lower one
			a = 3048;
		}
		return ((double) (dividend * 3048 + a)) / 100; //divided by 100 to put back to double in cm (since we multiplyed by 10 before)
	}

	/**
	 * This method compensates for the fact that the sensor is ahead of the center
	 * axle by 4.5cm by adding or subtracting that value to x or y, based on the
	 * rotation of the robot (theta)
	 */
	public double centerPositionX(double x, double theta) {
		double centerCorrection = SENSOR_POS_X;
		if (isPositiveXDirection(theta)) { // x is changing positively
			x -= centerCorrection;
		} else { // x is changing negatively
			x += centerCorrection;
		}
		return x;
	}

	public double centerPositionY(double y, double theta) {
		double centerCorrection = SENSOR_POS_Y;
		if (isPositiveYDirection(theta)) { // y is changing positively
			y -= centerCorrection;
		} else { // y is changing negatively
			y += centerCorrection;
		}
		return y;
	}

	/**
	 * The inversePosition functions do the exact inverse of the centerPostition functions for x and y
	 */
	public double inversePositionX(double x, double theta) {
		double centerCorrection = SENSOR_POS_X;
		if (isPositiveXDirection(theta)) { // x is changing positively
			x += centerCorrection;
		} else { // x is changing negatively
			x -= centerCorrection;
		}
		return x;
	}
	public double inversePositionY(double y, double theta) {
		double centerCorrection = SENSOR_POS_Y;
		if (isPositiveYDirection(theta)) { // y is changing positively
			y += centerCorrection;
		} else { // y is changing negatively
			y -= centerCorrection;
		}
		return y;
	}
	
	public static double getLightVal() {
		return lightVal;
	}

	/**
	 * This returns true if the robot is pointing in the positive y direction
	 */
	private boolean isPositiveYDirection(double theta) {
		if (theta >= 315 || theta <= 45) {
			return true;
		}
		return false;
	}
	/**
	 * This returns true if the robot is pointing in the negative y direction
	 */
	private boolean isNegativeYDirection(double theta) {
		if (135 <= theta && theta <= 225) {
			return true;
		}
		return false;
	}
	/**
	 * This returns true if the robot is pointing in the y direction
	 */
	private boolean isYdirection(double theta) {
		if (isPositiveYDirection(theta) || isNegativeYDirection(theta)) {
			return true;
		}
		return false;
	}
	/**
	 * This returns true if the robot is pointing in the positive x direction
	 */
	private boolean isPositiveXDirection(double theta) {
		if (theta > 45 && theta < 135) {
			return true;
		}
		return false;
	}
}
