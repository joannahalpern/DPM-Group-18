package Display;

import Controller.*;
import Robot.*;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 *Display on the computer 
 */
public class RConsoleDisplay implements TimerListener{
	public static final int LCD_REFRESH = 143;
	private Timer lcdTimer;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor csFlagReader, csLineReader;
	private UltrasonicSensor usLeft, usRight;
	
//	private UltrasonicPoller usPollerLeft;
//	private UltrasonicPoller usPollerRight;
//	private LightPoller csLinePoller;
//	private LightPoller csFlagPoller;
	
	// arrays for displaying data
	
	public RConsoleDisplay(Odometer odo, TwoWheeledRobot robot) {
		this.odo = odo;
		this.robot = robot;
		this.csFlagReader = robot.getColourSensorFlag();
		this.csLineReader = robot.getColourSensorLineReader();
		
		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();

		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// start the timer
		lcdTimer.start();
		RConsole.println("X  | Y | Angle");
	}
	
	public void timedOut() { 
		RConsole.println("" + odo.getX() + ", " + odo.getY() + ", " + odo.getAngle());
	}
}
