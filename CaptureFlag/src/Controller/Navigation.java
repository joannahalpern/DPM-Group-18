package Controller;

import Robot.*;
import lejos.nxt.*;
  
public class Navigation {
	// Initializers
	private NXTRegulatedMotor leftMotor, rightMotor;
	private TwoWheeledRobot robot;
	private Odometer odometer;

	// Constants for navigation
	private final double ACCEPTABLE_THETA_ERROR = Math.PI / 110;
	private final double ACCEPTABLE_DISTANCE_ERROR = 1;
	private final int TURNING_SPEED = 300;
	private final int FORWARD_SPEED = 330;
	// private final int ACCELERATION = 3000;

	//Status variables
	public boolean isTurning = false;
	
	// Constructor
	public Navigation(Odometer odo, TwoWheeledRobot robot) {
		this.odometer = odo;
		this.robot = robot;

		this.leftMotor = this.robot.getLeftMotor();
		this.rightMotor = this.robot.getRightMotor();

	}

	public void travelTo(double x, double y){


		//Initialize turn
		double desiredAngle = calculateAngle(x,y);
		turnTo(desiredAngle, false, false);

		//Set forward pace
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();

		//While not within destination threshold, keep going
		while(Math.abs(x-odometer.getX()) > ACCEPTABLE_DISTANCE_ERROR || Math.abs(y-odometer.getY()) > ACCEPTABLE_DISTANCE_ERROR){
			
			//Determine if need to correct angle
			desiredAngle = calculateAngle(x,y);
			if(!isAcceptableTrajectory(desiredAngle)){
				turnTo(desiredAngle, false, false);
			}

			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			

			leftMotor.forward();
			rightMotor.forward();

		}

		//stop at destination
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

	
	/**
	 * 
	 * @param theta: absolute angle to turn to (less than 360)
	 * @param stop
	 *            : true to stop rotation when angle is reached
	 * @param degrees
	 *            : true if input angle is in degrees
	 */

	public void turnTo(double theta, boolean stop, boolean degrees) {
		
		
		// Changes degrees to radians
		if (degrees) {
			theta = Math.toRadians(theta);
		}
		double err = theta - odometer.getAngleRadians();

		isTurning = true;
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
																		
																		//LCD.drawString("turning to " + Math.toDegrees(theta), 0, 6);
		}
		if (stop) {
			this.leftMotor.setSpeed(0);
			this.rightMotor.setSpeed(0);
		}
		isTurning = false; 
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
	public boolean isAcceptableTrajectory(double desiredAngle) {

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
	
	
	public boolean getTurning(){
		return isTurning;
		
	}
}

