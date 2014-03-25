package Robot;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class TwoWheeledRobot {
	public static final double DEFAULT_LEFT_RADIUS = 1.6;
	public static final double DEFAULT_RIGHT_RADIUS = 1.6;
	public static final double DEFAULT_WIDTH = 26.0;
	
	private NXTRegulatedMotor leftMotor, rightMotor, clawMotor;
	private UltrasonicSensor usLeft, usRight;
	private ColorSensor csFlagReader, csLineReader;
	
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, NXTRegulatedMotor clawMotor,
							UltrasonicSensor usLeft, UltrasonicSensor usRight, ColorSensor csFlagReader, ColorSensor csLineReader,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.clawMotor = clawMotor;
		this.usLeft = usLeft;
		this.usLeft = usRight;
		this.csFlagReader = csFlagReader;
		this.csLineReader = csLineReader;
		
		
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		
//		clawMotor.resetTachoCount();
	}

	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, NXTRegulatedMotor clawMotor,
			UltrasonicSensor usLeft, UltrasonicSensor usRight, ColorSensor csFlagReader, ColorSensor csLineReader) {
		this(leftMotor, rightMotor, clawMotor, usLeft, usRight, csFlagReader, csLineReader, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}

	// accessors
	public NXTRegulatedMotor getLeftMotor() {
		return leftMotor;
	}
	public NXTRegulatedMotor getRightMotor() {
		return rightMotor;
	}
	public NXTRegulatedMotor getBlockGrabber() {
		return clawMotor;
	}
	public UltrasonicSensor getLeftUSSensor() {
		return usLeft;
	}
	public UltrasonicSensor getRightUSSensor() {
		return usLeft;
	}
	public ColorSensor getColourSensorFlag(){
		return csFlagReader;
	}
	public ColorSensor getColourSensorLineReader(){
		return csLineReader;
	}
	public double getRadius(){
		return DEFAULT_RIGHT_RADIUS;
	}
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius + rightMotor.getTachoCount() * rightRadius) * Math.PI / (180.0*2);
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius - rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) * 180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) * 180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
	}
}
