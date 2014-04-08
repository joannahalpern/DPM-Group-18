package Controller;

import Robot.*;
import lejos.nxt.*;

public class NavController {
	// Initializers
	private TwoWheeledRobot robot;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private UltrasonicSensor usLeft, usRight;

	private ObjectDisplacement displacer;
	private Navigation nav;
	private ObstacleAvoidance avoider;
	private ObjectDetectIdentify detector;

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
	private final int lCorrection = 30;
	private double x0;
	private double y0;

	// Sets whether or not you want to put p-turn on
	private boolean turnON = false;

	// Avoidance Status variables
	private boolean xReached;
	private boolean yReached;
	private boolean xAxis;
	private boolean exit;
	private int searchCount = 0;
	private double searchArea = 10;

	// Search variables
	private int csCount = 0;
	private double lowValue;
	private double csValue;
	
	private boolean searching = true;
	private boolean found = false;
	
	public boolean longX = false;
	public Colour flagColour;
	public int inv = 1;

	// Constructor
	public NavController(Odometer odo, TwoWheeledRobot robot,
			ObjectDisplacement displacer, Navigation nav,
			ObjectDetectIdentify detector, ObstacleAvoidance avoider) {
		this.odometer = odo;
		this.robot = robot;
		this.nav = nav;
		this.leftMotor = this.robot.getLeftMotor();
		this.rightMotor = this.robot.getRightMotor();

		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();

		this.cs = robot.getColourSensorFlag();

		this.displacer = displacer;
		this.detector = detector;
		this.avoider = avoider;

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

	/**
	 * Travels to x,y Avoid sets obstacle avoidance on
	 * 
	 * 
	 * @param x
	 *            : final x coord
	 * @param y
	 *            : final y coord
	 * @param avoid
	 *            : true turns avoid on
	 * @param turn
	 *            : true sets turn on
	 * @param search
	 *            : true sets search on
	 */
	public void travelTo(double x, double y, boolean avoid,
			boolean search) {
		double desiredAngle;
		exit = false;

		desiredAngle = nav.calculateAngle(x, y);
		nav.turnTo(desiredAngle, false, false);
		// Set forward pace
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();

		// Concave L Obstacle avoidance and corner avoid
		if (avoid) {
			concaveAvoidance();
		}

		// MAIN LOOP
		while (Math.abs(x - odometer.getX()) > ACCEPTABLE_DISTANCE_ERROR
				|| Math.abs(y - odometer.getY()) > ACCEPTABLE_DISTANCE_ERROR) {

			
			if (exit) {
				LCD.clear();
				LCD.drawString("Exit Return ", 0, 6);
				return;
			}
			if (!searching && !avoid) {
				LCD.clear();
				LCD.drawString("Search Return ", 0, 6);
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				return;
			}
			// Main code for navigation
			desiredAngle = nav.calculateAngle(x, y);
			if (!nav.isAcceptableTrajectory(desiredAngle)) {
				LCD.clear();
				LCD.drawString("Is HERE", 0, 6);
				nav.turnTo(desiredAngle, false, false);
			}

			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.forward();
			rightMotor.forward();

			// Search code
			if (search) {
				if (detector.isBlock()) {
					
					if (detector.getColour() == flagColour) {
						found = true;
						Sound.beepSequence();
						searching = false;
						displacer.run();
					}
				}
			}

			// Code for avoidance
			if (avoid) {

				if (avoider.avoidObstacle(false, false)) {

					if (avoider.reverse) {
						if(!found){
						nav.travelDistance(-5);
						avoider.reverse = false;
						}
					}
					if (axisAvoidance()) {
						return;
					}
					if (xAxis) {

						xAxis = false;
						travelTo(odometer.getX(), y0, true, false);
					} else {

						xAxis = true;
						travelTo(x0, odometer.getY(), true,  false);
					}
				}
			}
			// exits original call when y coord is reached
			if ((Math.abs(y0 - odometer.getY())) <= ACCEPTABLE_DISTANCE_ERROR
					&& avoid && !yReached) {
				yReached = true;
				travelTo(x0, y0, true,  false);
			
				exit = true;
			}

			// Exits loop when reaches proper x coordinate, xCall is used so
			// that loop will no exit when traveling along xAxis to y
			if (Math.abs(x0 - odometer.getX()) <= ACCEPTABLE_DISTANCE_ERROR
					&& avoid && !xReached) {
				xReached = true;
				travelTo(x0, y0, true,  false);
				
				exit = true;
			}

			if (exit) {
				LCD.clear();
				LCD.drawString("Exit Return ", 0, 6);
				return;
			}
		}

		// stop at destination
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		LCD.clear();
		LCD.drawString("Finish Return ", 0, 6);
		return;

	}

	// SEARCH Methods
	public void search(double x0, double y0, double x1, double y1,
			boolean setFlag, Colour flagInput) {

		// Search Initializaers

		if (setFlag) {
			flagColour = flagInput;
		} else {
			// flagColour = Bluetooth.getOurFlagType();
		}

		searching = true;

		nav.travelTo(x0 , y0 );

		LCD.clear();
		LCD.drawString("Searching", 0, 7);
		searchCount = 0;
		//Search Algorithm
		while (searching) {
				if(longX){
					if(searching){
						travelTo(x1 + inv*searchCount*searchArea, odometer.getY(), false,  true);
					}
					if (searching) {
						travelTo( odometer.getX(),y1 + inv*searchCount*searchArea, false,  true);
					}
					
					if (searching) {
						travelTo( x0 + inv*searchCount*searchArea,odometer.getY(), false,  true);
					}
					
					if (searching) {
						travelTo( odometer.getX(),y0 + inv*30 , false,  true);
					}
					
					if (searching) {
						travelTo(x1 + inv*searchCount*searchArea,odometer.getY(),  false,  true);
					}
					if (searching) {
						travelTo(odometer.getX(),y1 + inv*10 + searchCount*inv*searchArea, false,  true);
					}
					if (searching) {
						travelTo( x0 - inv*10 - inv*searchCount*searchArea,odometer.getY(), false,  true);
					}
					if (searching) {
						travelTo( odometer.getX(),y0 - inv*10 - inv*searchCount*searchArea, false,  true);
					}
			}
				else if(!longX){
					if(searching){
						travelTo(odometer.getX(), y1 + inv*searchCount*searchArea, false,  true);
					}
					
					if (searching) {
						travelTo(x1 + inv*searchCount*searchArea, odometer.getY(), false,  true);
					}
					
					if (searching) {
						travelTo(odometer.getX(), y0 + inv*searchCount*searchArea, false,  true);
					}
					
					if (searching) {
						travelTo(x0 + inv*30 , odometer.getY(), false,  true);
					}
					
					if (searching) {
						travelTo(odometer.getX(), y1 + inv*searchCount*searchArea, false,  true);
					}
					if (searching) {
						travelTo(x0 - searchCount*inv*searchArea, odometer.getY(), false,  true);
					}
					if (searching) {
						travelTo(odometer.getX(), y0 - inv*10 - inv*searchCount*searchArea, false,  true);
					}
			}
				
			searchCount++;
		}
		LCD.clear();
		LCD.drawString("Found", 0, 7);
		return;
	}

	// obstacle Avoidance Methods

	/**
	 * Checks if there is an obstacle, if so turns again towards heading with an
	 * offset of Lcorretciton. Used to avoid L-shaped obstacles
	 * 
	 * @return
	 */
	private void concaveAvoidance() {
		if (avoider.avoidObstacle(false, true)) {
 
			
			if (xAxis) {
				xAxis = false;
				if((x0 - odometer.getX()) >= 0){
					
					
					//Pass
					if (((y0 - odometer.getY()) >= 0)) {
						travelTo(odometer.getX(), odometer.getY() - lCorrection,true,  false);
						xAxis = true;
						if (exit) {return;}
						travelTo(x0, odometer.getY(), true,  false);
					} 
					//Pass
					else {
						travelTo(odometer.getX(), odometer.getY() + lCorrection,true,  false);
						xAxis = true;
						if (exit) {return;}
						travelTo(x0, odometer.getY(), true,  false);
					}

				} 
				else{
					//pass
					if (((y0 - odometer.getY()) >= 0)) {
						travelTo(odometer.getX(), odometer.getY() - lCorrection,true,  false);
						xAxis = true;
						if (exit) {return;}
						travelTo(x0, odometer.getY(), true,  false);
					} 
					//pass
					else {
						travelTo(odometer.getX(), odometer.getY() + lCorrection,true,  false);
						xAxis = true;
						if (exit) {return;}
						travelTo(x0, odometer.getY(), true,  false);
					}
					
				}
			}
			
			else {
				xAxis = true;
				if (((y0 - odometer.getY()) >= 0)){
					
					
				
					if ((x0 - odometer.getX()) >= 0) {
						travelTo(odometer.getX() - lCorrection, odometer.getY(),
								true,  false);
						xAxis = false;
						if (exit) {return;}
						travelTo(odometer.getX(), y0, true,  false);
					} 
					else {
						travelTo(odometer.getX() + lCorrection, odometer.getY(),true,  false);
						xAxis = false;
						travelTo(odometer.getX(), y0, true,  false);
					}
				}
				else{
					if ((x0 - odometer.getX()) >= 0) {
					
						travelTo(odometer.getX() - lCorrection, odometer.getY(),
								true,  false);
						xAxis = false;
						if (exit) {return;}
						travelTo(odometer.getX(), y0, true,  false);
					} 
					else {
						travelTo(odometer.getX() + lCorrection, odometer.getY(),true,  false);
						xAxis = false;
						if (exit) {return;}
						travelTo(odometer.getX(), y0, true,  false);
					}
				}

			}
		}

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
			travelTo(odometer.getX(), odometer.getY() - lCorrection, true, false);
			xAxis = true;
			travelTo(x0, odometer.getY(), true,  false);
			return true;
		}
		// robot traveling along y Axis while at x0
		if (Math.abs(x0 - odometer.getX()) <= ACCEPTABLE_DISTANCE_ERROR
				&& !xAxis) {
			xAxis = true;
			xReached = false;
			travelTo(odometer.getX() - lCorrection, odometer.getY(), true, false);
			xAxis = false;
			travelTo(odometer.getX(), y0, true,  false);
			return true;
		}
		return false;
	}

	// Getters and setters for global variables x0, y0 and xAxis

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
	 * @param travellingInXDirection
	 *            : true if headed in xDirection, false if headed in Y direction
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

	/**
	 * Values that need to be set before traveling to destination
	 * 
	 * @param x1
	 *            : final x coordinate
	 * @param y1
	 *            : final y coordinate
	 * @param axis
	 */
	public void avoidanceSetter(double x1, double y1, boolean axis) {
		x0 = x1;
		y0 = y1;
		xAxis = axis;
		return;
	}
}
