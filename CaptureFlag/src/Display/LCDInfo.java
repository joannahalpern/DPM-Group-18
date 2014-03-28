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
	private UltrasonicPoller usPollerLeft, usPollerRight;
	private UltrasonicSensor usLeft, usRight;
	private Localization loc;
	
	
	// arrays for displaying data
	
	public LCDInfo(Odometer odo, TwoWheeledRobot robot, Localization loc, UltrasonicPoller usPollerLeft, UltrasonicPoller usPollerRight, ColorSensor csLineReader) {
		this.loc = loc;
		this.csLineReader = csLineReader;
		this.odo = odo;
		this.robot = robot;
		this.usPollerLeft = usPollerLeft;
		this.usPollerRight = usPollerRight;
		this.csFlagReader = robot.getColourSensorFlag();
		this.csLineReader = robot.getColourSensorLineReader();
		
		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();	
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		LCD.clear();
		LCD.drawString("X: " + odo.getX(), 0, 0);
		LCD.drawString("Y: " + odo.getY(), 0, 1);
		LCD.drawString("H: " + odo.getAngle(), 0, 2);	
//		LCD.drawString("L us: " + usPollerLeft.getMedianDistance(), 0, 3);	
//		LCD.drawString("R us: " + usPollerRight.getMedianDistance(), 0, 4);	
//		LCD.drawString("diff: " + (usPollerLeft.getMedianDistance()-usPollerRight.getMedianDistance()), 0, 5);	
//		LCD.drawString("orthogonal: " + loc.isOrthogonal(), 0, 6);
		LCD.drawString("CS: " + csLineReader.getRawLightValue(), 0, 3);	

	}
}
