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
	private LightPoller linePoller;
	
	// arrays for displaying data
	
	public LCDInfo(Odometer odo, TwoWheeledRobot robot, LightPoller linePoller) {
		this.odo = odo;
		this.robot = robot;
		this.csFlagReader = robot.getColourSensorFlag();
		this.csLineReader = robot.getColourSensorLineReader();
		
		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();	
		
		this.linePoller = linePoller;
		
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		LCD.drawString("Task: " + Controller.task, 0, 0);
		LCD.drawString("X: " + (int)odo.getX(), 0, 1);
		LCD.drawString("Y: " + (int)odo.getY(), 0, 2);
		LCD.drawString("H: " + (int)odo.getAngle(), 0, 3);
		LCD.drawString("          ", 0, 4);
		
		LCD.drawString("Zn: (" + Controller.ourZoneLL_X + "," + Controller.ourZoneLL_Y + ")to(" + Controller.ourZoneUR_X + "," + Controller.ourZoneUR_Y + ")", 0, 5);
		LCD.drawString("Flag: " + Controller.ourFlagColour, 0, 6);
		
		switch (Controller.task){
		case LOCALIZING:
			LCD.drawString("                 ", 0, 5);
			LCD.drawString("LineVal: " + (int)linePoller.getColourVal(), 0, 6);
			
			break;
		case NAVIGATING:
			LCD.drawString("Zn: (" + Controller.ourZoneLL_X + "," + Controller.ourZoneLL_Y + ")to(" + Controller.ourZoneUR_X + "," + Controller.ourZoneUR_Y + ")", 0, 5);
			LCD.drawString("Flag: " + Controller.ourFlagColour, 0, 6);
			
			break;
		case SEARCHING:
			LCD.drawString("Zn: (" + Controller.ourZoneLL_X + "," + Controller.ourZoneLL_Y + ")to(" + Controller.ourZoneUR_X + "," + Controller.ourZoneUR_Y + ")", 0, 5);
			LCD.drawString("Flag: " + Controller.ourFlagColour, 0, 6);
			break;
//		case DROPPING_OFF:
//			break;
		default:
			LCD.drawString("Zn: (" + Controller.ourZoneLL_X + "," + Controller.ourZoneLL_Y + ")to(" + Controller.ourZoneUR_X + "," + Controller.ourZoneUR_Y + ")", 0, 5);
			LCD.drawString("Flag: " + Controller.ourFlagColour, 0, 6);
			 break;
	}
	}
}