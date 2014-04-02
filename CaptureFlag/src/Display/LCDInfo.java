package Display;

import Controller.*;
import Robot.*;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * Displays on the LCD of the robot
 *
 */
public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Timer lcdTimer;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor csFlagReader, csLineReader;
	private UltrasonicSensor usLeft, usRight;
	private LightPoller lines;
	
	
	// arrays for displaying data
	
	public LCDInfo(Odometer odo, TwoWheeledRobot robot, LightPoller lines) {
		this.odo = odo;
		this.robot = robot;
		this.csFlagReader = robot.getColourSensorFlag();
		this.csLineReader = robot.getColourSensorLineReader();
		
		this.lines = lines;
		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();	
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 

		LCD.drawString("X: " + (int)odo.getX(), 0, 0);
		LCD.drawString("Y: " + odo.getY(), 0, 1);
		LCD.drawString("H: " + (int)odo.getAngle(), 0, 2);
		LCD.drawString("LSraw: " + (int)lines.getColourVal(), 0, 3);
		LCD.drawString("line: " + lines.lineSeen, 0, 4);
		LCD.drawString("usA: " + Localization.usA, 0, 5);
		LCD.drawString("usB: " + Localization.usB, 0, 6);

	}
}
