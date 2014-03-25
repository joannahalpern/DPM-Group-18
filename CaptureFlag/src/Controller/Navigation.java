package Controller;

import Robot.*;
import lejos.nxt.*;

public class Navigation {
	// Initializers
	private NXTRegulatedMotor leftMotor, rightMotor;
	private UltrasonicSensor usLeft, usRight;
	private ObjectDisplacement displacer;
	private TwoWheeledRobot robot;
	private ObjectDisplacement od;
	private Odometer odometer;
	private ColorSensor cs;
	private LightPoller colourDetector;

	

	// Constants for navigation
	private final double ACCEPTABLE_THETA_ERROR = Math.PI / 110;
	private final double ACCEPTABLE_DISTANCE_ERROR = 1;
	private final int TURNING_SPEED = 300;
	private final int FORWARD_SPEED = 330;
	// private final int ACCELERATION = 3000;

	// Obstacle Avoidance variables
	private final double AVOID_DISTANCE = 15;
	private final int cTURN_HIGH = 530;
	private final int cTURN_LOW = 140;
	private final int lCorrection = 30;
	private double usDistance;
	private double usLeftDistance;
	private double usRightDistance;
	private double x0;
	private double y0;

	// Sets whether or not you want to put p-turn on
	private boolean turnON = false;

	// Avoidance Status variables
	private int sensorCount = 0;
	private boolean xReached;
	private boolean yReached;
	private boolean xAxis;
	private boolean exit;

	//Search variables
	private int csCount = 0;
	private double lowValue;
	private double csValue;
	private Colour flagColour;
	private boolean searching = true;
	
	
	// Constructor
	public Navigation(Odometer odo, TwoWheeledRobot robot, ObjectDisplacement displacer, LightPoller colourDetector) {
		this.odometer = odo;
		this.robot = robot;

		this.leftMotor = this.robot.getLeftMotor();
		this.rightMotor = this.robot.getRightMotor();

		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();

		this.cs = robot.getColourSensorFlag();
		this.displacer = displacer;
		this.colourDetector = colourDetector;
		// NXTRegulatedMotor[] motors = new NXTRegulatedMotor[2];
		// motors = this.robot.getWheelMotors();
		// this.leftMotor = motors[0];
		// this.rightMotor = motors[1];
		//
		// UltrasonicSensor[] usSensors = this.robot.getusSensors();
		// this.usLeft = usSensors[0];
		// this.usRight = usSensors[1];
	}

	/**Travels to x,y
	 * Avoid sets obstacle avoidance on
	 * 
	 * 
	 * @param x: final x coord	
	 * @param y: final y coord
	 * @param avoid: true turns avoid on
	 * @param turn: true sets turn on
	 * @param search: true sets search on
	 */
	public void travelTo(double x, double y, boolean avoid, boolean turn, boolean search) {
		double desiredAngle;
		exit = false;

		desiredAngle = calculateAngle(x, y);
		turnTo(desiredAngle, false, false);
		// Set forward pace
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		
		//Search Initializaers
		if(search){
			flagColour = Bluetooth.getOurFlagType();
		}
		// Concave L Obstacle avoidance and corner avoid
		if (avoid) {
			if (concaveAvoidance()) {
				return;
			}
		}

		// MAIN LOOP
		while (Math.abs(x - odometer.getX()) > ACCEPTABLE_DISTANCE_ERROR
				|| Math.abs(y - odometer.getY()) > ACCEPTABLE_DISTANCE_ERROR) {

			// exits original call when y coord is reached
			if ((Math.abs(y0 - odometer.getY())) <= ACCEPTABLE_DISTANCE_ERROR
					&& avoid && !yReached) {
				yReached = true;
				travelTo(x0, y0, true, turnON, false);
				Sound.twoBeeps();
				exit = true;
			}
			// Exits loop when reaches proper x coordinate, xCall is used so
			// that loop will no exit when travelling along xaxis to y
			if (Math.abs(x0 - odometer.getX()) <= ACCEPTABLE_DISTANCE_ERROR && avoid && !xReached) {
				xReached = true;
				travelTo(x0, y0, true, turnON, false);
				Sound.twoBeeps();
				exit = true;
			}

			if (exit || !searching) {
				return;
			}

			// Main code for navigation
			desiredAngle = calculateAngle(x, y);
			if (!isAcceptableTrajectory(desiredAngle)) {
				turnTo(desiredAngle, false, false);
			}

			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.forward();
			rightMotor.forward();
			
			//Search code
			if (search){
				if(isBlock()){
					if(getColour() == flagColour){
						displacer.run();
						searching = false;
				}
			}
			
			
			// Code for avoidance
			/**
			 * If avoidObstacle returns true, first checks special case of axis
			 * avoidance, then checks current heading. Sets xAxis to opposite
			 * value and turns 90 degrees towards x0/y0
			 * 
			 */
			if (avoid) {

				if (avoidObstacle(false, false)) {
					if (axisAvoidance()) {
						return;
					}
					if (xAxis) {
						cTurn(turnON);
						xAxis = false;
						travelTo(odometer.getX(), y0, true, turnON, false);
					} else {
						cTurn(turnON);
						xAxis = true;
						travelTo(x0, odometer.getY(), true, turnON, false);
					}
				}
			}
		}

		// stop at destination
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		return;

	}
	}
	
	
	
	
	
	
	
	
	// SEARCH Methods
	
	/**Reads 5 values for each colour and returns median
	 * Compares values to determine which colour flag it is
	 * 
	 * @return: colour enum
	 */
	private Colour getColour(){
		
		
		//Calls and returns medians for each value
		colourDetector.setFloodLight(Colour.GREEN);
		colourDetector.run();
		double green = colourDetector.getMedian();
		
		colourDetector.setFloodLight(Colour.BLUE);
		colourDetector.run();
		double blue = colourDetector.getMedian();
		
		colourDetector.setFloodLight(Colour.RED);
		colourDetector.run();
		double red = colourDetector.getMedian();
		
		
		/*
	    double green = cs.getRawColor().getGreen();
		double red = cs.getRawColor().getRed();
		double blue = cs.getRawColor().getBlue();
		double backgroundLumination = cs.getRawColor().getBackground();
		*/
		
		
		if( (blue > green ) && (green > red) ) {
			LCD.drawString("Dark blue: ", 0, 2);
//			LCD.drawString("LCDblue: " + blue, 0, 3);
//			LCD.drawString("LCDgreen: " + green, 0, 4);
//			LCD.drawString("LCDred: " + red, 0, 5);
			return Colour.DARK_BLUE;
		}
		else if ( (red > green) && (green > blue)) {
			LCD.drawString("Yellow: ", 0, 2);
			return Colour.YELLOW;
			
		}
		else if ( ( (red > blue) && (red > green) ) ) {
			LCD.drawString("Red: ", 0, 2);
			return Colour.RED;
			
		}
		else if ( ((red > 300) && (green > 300) && (blue > 300)) && (( (blue +70) > green) && ( (blue +70) > red) )	) { //((red && green && blue) >300) && ( (blue + 70) ) > (green && red) )
			LCD.drawString("Light Blue: ", 0, 2);
			return Colour.LIGHT_BLUE;
		}
		else { 			//((red && green && blue) >300) && ( (red + 20)  > (green && blue) )
		LCD.drawString("White: ", 0, 2);
		return Colour.WHITE;
		}
							
	}
		
	/**Continuously checks light cs for a value greater than base value
	 * 
	 *
	 * @return: true if more than 3 consecutive high values
	 */
	private boolean isBlock(){
		csValue = cs.getRawLightValue();
		if(csValue >= lowValue){
			csCount++;
			if(csCount > 3){
				csCount = 0;
				return true;
			}
		}
	return false;
	}
	
	
	
	
	
	
	
	// obstacle Avoidance Methods
	
	/**
	 * Checks sensor values and returns lower of 2 sensors. If smaller value is
	 * less than AVOID_DISTANCE more than 3 times, returns true indicating there
	 * is an obstacle that needs to be avoided
	 * 
	 * @param test
	 *            : true if wanting to display value of sensors, only used for
	 *            testing
	 * @param checkObstacle
	 *            : true if first call after turn, checks if there is an
	 *            obstacle in its immediate path before starting motors.
	 *            Otherwise false
	 * @return
	 */
	public boolean avoidObstacle(boolean test, boolean checkObstacle) {

		usLeftDistance = usLeft.getDistance();
		usRightDistance = usRight.getDistance();
		LCD.drawString("usLeft: " + usLeftDistance, 0, 5);
		LCD.drawString("usRight: " + usRightDistance, 0, 6);
		if (checkObstacle) {
			for (int i = 0; i < 3; i++) {
				usLeftDistance = usLeft.getDistance();
				usRightDistance = usRight.getDistance();
				if (usLeftDistance <= usRightDistance) {
					usDistance = usLeftDistance;
				} else {
					usDistance = usRightDistance;
				}

				if (usDistance <= AVOID_DISTANCE) {
					sensorCount++;
					if (sensorCount > 3) {
						sensorCount = 0;
						return true;
					}
				}
			}
		}

		if (usLeftDistance <= usRightDistance) {
			usDistance = usLeftDistance;
		} else {
			usDistance = usRightDistance;
		}

		if (usDistance <= AVOID_DISTANCE) {
			sensorCount++;
			if (sensorCount > 3) {
				sensorCount = 0;
				return true;
			}
		}

		while (test) {
			usLeftDistance = usLeft.getDistance();
			usRightDistance = usRight.getDistance();
			LCD.clear();
			LCD.drawString("usLeft: " + usLeftDistance, 0, 5);
			LCD.drawString("usRight: " + usRightDistance, 0, 6);

		}
		return false;

	}

	
	
	
	
	/**
	 * Checks if there is an obstacle, if so turns again towards heading with an
	 * offset of Lcorretciton. Used to avoid L-shaped obstacles
	 * 
	 * @return
	 */
	private boolean concaveAvoidance() {
		if (avoidObstacle(false, true)) {
			if (xAxis) {
				xAxis = false;
				if (((y0 - odometer.getY()) >= 0)) {
					travelTo(odometer.getX(), odometer.getY() - lCorrection,
							true, turnON, false);
					xAxis = true;
					travelTo(x0, odometer.getY(), true, turnON, false);
				} else {
					travelTo(odometer.getX(), odometer.getY() + lCorrection,
							true, turnON, false);
					xAxis = true;
					travelTo(odometer.getX(), y0, true, turnON, false);
				}

			} else {
				xAxis = true;
				if ((x0 - odometer.getX()) >= 0) {
					travelTo(odometer.getX() - lCorrection, odometer.getY(),
							true, turnON, false);
					xAxis = false;
					travelTo(odometer.getX(), y0, true, turnON, false);
				} else {
					travelTo(odometer.getX() - lCorrection, odometer.getY(),
							true, turnON, false);
					xAxis = false;
					travelTo(odometer.getX(), y0, true, turnON, false);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Called when robot reaches x0 or y0 axis, initiates avoidance by moving
	 * robot Lcorrection distance off axis
	 * 
	 * @return
	 */
	private boolean axisAvoidance() {

		// Robot traveling along x axis while at y0
		if (Math.abs(y0 - odometer.getY()) <= ACCEPTABLE_DISTANCE_ERROR
				&& xAxis) {
			xAxis = false;
			yReached = false;
			travelTo(odometer.getX(), odometer.getY() - lCorrection, true,
					turnON, false);
			xAxis = true;
			travelTo(x0, odometer.getY(), true, turnON, false);
			return true;
		}
		// robot traveling along y Axis while at x0
		if (Math.abs(x0 - odometer.getX()) <= ACCEPTABLE_DISTANCE_ERROR
				&& !xAxis) {
			xAxis = true;
			xReached = false;
			travelTo(odometer.getX() - lCorrection, odometer.getY(), true,
					turnON, false);
			xAxis = false;
			travelTo(odometer.getX(), y0, true, turnON, false);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param on
	 *            : initiates smoother turning. Used to reuce hard 90 degree
	 *            turns.
	 */
	private void cTurn(boolean on) {
		if (on) {
			if (!xAxis) {
				if ((odometer.getX() - x0) < 0) {
					leftMotor.setSpeed(cTURN_HIGH);
					rightMotor.setSpeed(cTURN_LOW);
				} else {
					leftMotor.setSpeed(cTURN_LOW);
					rightMotor.setSpeed(cTURN_HIGH);
				}
			} else {
				if ((odometer.getY() - y0) < 0) {
					leftMotor.setSpeed(cTURN_LOW);
					rightMotor.setSpeed(cTURN_HIGH);
				} else {
					leftMotor.setSpeed(cTURN_HIGH);
					rightMotor.setSpeed(cTURN_LOW);
				}
			}
			leftMotor.forward();
			rightMotor.forward();
			travel(3, false);
		}
	}

	/**
	 * 
	 * @param theta: absolute angle to turn to (less than 360)
	 * @param stop
	 *            : true to stop rotation when angle is reached
	 * @param degrees
	 *            : true if input angle is in degrees
	 */

	
	
	
	
	//Navigation Methods
	
	
	
	public void turnTo(double theta, boolean stop, boolean degrees) {

		// Changes degrees to radians
		if (degrees) {
			theta = Math.toRadians(theta);
		}
		double err = theta - odometer.getAngleRadians();

		// Set speed
		leftMotor.setSpeed(TURNING_SPEED);
		rightMotor.setSpeed(TURNING_SPEED);

		// determine wheel directions
		if ((err > 0 && err < Math.PI) || (err < 0 && err < -Math.PI)) {
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.backward();
			rightMotor.forward();
		}

		// Keep turning until have reached an acceptable angle
		while (Math.abs(err) > ACCEPTABLE_THETA_ERROR) {
			err = theta - odometer.getAngleRadians();
		}
		if (stop) {
			this.leftMotor.setSpeed(0);
			this.rightMotor.setSpeed(0);
		}
	}

	/**
	 * Calculates angle for which to turn given x and y coordinates
	 * 
	 * @param x
	 * @param y
	 * @return: returns angles in radians
	 */
	public double calculateAngle(double x, double y) {
		double angle;
		angle = Math.atan2(x - odometer.getX(), y - odometer.getY());
		return (angle < 0) ? (angle + Math.PI * 2) % (Math.PI * 2) : angle
				% (Math.PI * 2);
	}

	/**
	 * Compares angle to odometer and returns true in angle is correct
	 * 
	 * @param desiredAngle
	 *            : angle in radians
	 * @return
	 */
	private boolean isAcceptableTrajectory(double desiredAngle) {

		// For this range need to calculate the angle differently otherwise the
		// difference will be too large
		if (desiredAngle > 7 * Math.PI / 4
				&& odometer.getAngleRadians() < Math.PI / 4) {
			return 2 * Math.PI - desiredAngle < odometer.getAngleRadians()
					+ Math.PI / 16
					&& 2 * Math.PI - desiredAngle > odometer.getAngleRadians()
							- Math.PI / 16;
		}

		return desiredAngle < odometer.getAngleRadians() + Math.PI / 16
				&& desiredAngle > odometer.getAngleRadians() - Math.PI / 16;
	}

	/**
	 * Method to travel for a inputed amount of time
	 * 
	 * @param seconds
	 *            : time in seconds to travel
	 * @param setSpeed
	 *            : true if want to change speed to FORWARD_SPEED, false if want
	 *            to continue at previous speed
	 */
	public void travel(double seconds, boolean setSpeed) { // travel forward for
															// a specified
															// amount of time

		if (setSpeed) {
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.forward();
			rightMotor.forward();
		}
		double start = System.currentTimeMillis();
		double end = System.currentTimeMillis();
		while (end - start < seconds * 1000) {
			end = System.currentTimeMillis();
		}
		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	 * Getter for xAxis, true if traveling facing xAxis
	 * 
	 * @return
	 */
	public boolean getAxis() {
		return xAxis;
	}

	/**
	 * Setter for xAxis
	 * 
	 * @param travellingInXDirection: true if headed in xDirection, false if headed in Y direction
	 */
	public void setAxis(boolean travellingInXDirection) {
		xAxis = travellingInXDirection;
	}

	/**
	 * Setter for x destination
	 * 
	 * @param x1
	 *            : X coordiante of destination
	 */
	public void setX(double x1) {
		x0 = x1;
		return;
	}

	/**
	 * Setter for y destination
	 * 
	 * @param y1
	 *            : y coordinate of destination
	 */
	public void setY(double y1) {
		y0 = y1;
		return;
	}

	// the robot will travel distance
	public void travelDistance(double distance) {

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		double radius = robot.getRadius();
		rightMotor.rotate(convertDistance(radius, distance), true);
		leftMotor.rotate(convertDistance(radius, distance), false);

	}

	// calculate numbers of degrees need to rotate to achieve the distance
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// calculate numbers of degrees need to rotate to achieve the angle
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}