package Controller;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import Display.*;
import Robot.*;

/**
 * While Lab5.obstacleAvoidance == true:
 * 
 * if us sensor sees something within threshold, it stops navigation (navigation = false), and turns right.
 * It then checks if something's in front, if it is it turns right again. If not, moves forward an amount d (d should be bigger than a block),
 * then it sets navigation to true again. (some of this should be in a synchronized lock)
 */
public class ObstacleAvoidance {
	private NXTRegulatedMotor leftMotor, rightMotor;
	private UltrasonicSensor usLeft, usRight;
	private Navigation nav;
	private TwoWheeledRobot robot; 
	private UltrasonicPoller usPoller;
	private Odometer odometer;

	// Obstacle Avoidance variables
	private final double AVOID_DISTANCE = 15;
	private final int cTURN_HIGH = 530;
	private final int cTURN_LOW = 140;
	private double usDistance;
	private double usLeftDistance;
	private double usRightDistance;
	private double x0;
	private double y0;
	
	// Sets whether or not you want to put p-turn on
	private boolean turnON = false;
	public boolean reverse = false;
	
	// Avoidance Status variables
	private int sensorCount = 0;

	
	
	public ObstacleAvoidance( TwoWheeledRobot robot, Navigation nav, Odometer odo) {
		this.robot = robot;
		this.nav = nav;
		this.odometer = odo;
		
		this.leftMotor = this.robot.getLeftMotor();
		this.rightMotor = this.robot.getRightMotor();
		
		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();
	}
	
	public void run(){
		
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
							if(usDistance < 10){
								reverse = true;
							}
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
					if(usDistance < 10){
						reverse = true;
					}
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
		 * 
		 * @param on
		 *            : initiates smoother turning. Used to reuce hard 90 degree
		 *            turns.
		 */
		public void cTurn(boolean on, boolean xAxis) {
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
				nav.travel(3, false);
			}
		}
}