package Controller;


import Display.*;
import Robot.*;
import lejos.util.Timer;
import lejos.util.TimerListener;
//Odometer using theta in degrees

public class Odometer implements TimerListener { 
	public static final int DEFAULT_PERIOD = 25;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	// position data
	private double x, y, theta;
	private double [] oldDH, dDH;
	
	public Odometer(TwoWheeledRobot robot, boolean start) {
		// initialise variables
		this.robot = robot;
		odometerTimer = new Timer(DEFAULT_PERIOD, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDH = new double [2];
		dDH = new double [2];
		
		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}
	
	public void timedOut() {
		robot.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];
		
		// update the position in a critical region
		synchronized (Controller.lock) {
			theta += dDH[1];
			theta = fixDegAngle(theta);
			
			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}
		
		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}
	
	// accessors
	public void getPosition(double [] pos) {
		synchronized (Controller.lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}
	public double getX() {
		double result;

		synchronized (Controller.lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (Controller.lock) {
			result = y;
		}

		return result;
	}

	
	public double getAngle() {
		double result;

		synchronized (Controller.lock) {
			result = theta;
		}

		return result;
	}
	public double getAngleRadians() {
		double result;

		synchronized (Controller.lock) {
			result = Math.toRadians(theta);
		}

		return result;
	}
	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}
	
	// mutators
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (Controller.lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}
	
	public void setAngle(double angle) {
		synchronized (Controller.lock) {
			theta = angle;
		}
	}
	
	// static 'helper' methods
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
	
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);
		
		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
