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
		int x = (int)(odo.getX()/30.48);
		if (x<10){
			LCD.drawString("X:  " + x, 0, 1);
		}
		else{
			LCD.drawString("X: " + x, 0, 1);
		}
		int y = (int)(odo.getY()/30.48);
		if (y<10){
			LCD.drawString("Y:  " + y, 0, 2);
		}
		else{
			LCD.drawString("Y: " + y, 0, 2);
		}
		int angle = (int)odo.getAngle();
		if (angle<100){
			LCD.drawString("H: 0" + angle, 0, 3);
		}
		else{
			LCD.drawString("H: " + angle, 0, 3);
		}
		LCD.drawString("          ", 0, 4);
		
		LCD.drawString("Zn: (" + Controller.ourZoneLL_X + "," + Controller.ourZoneLL_Y + ")to(" + Controller.ourZoneUR_X + "," + Controller.ourZoneUR_Y + ")", 0, 5);
		LCD.drawString("Flag: " + Controller.ourFlagColour, 0, 6);
		LCD.drawString("Drop:("+ Controller.ourDZone_X + "," + Controller.ourDZone_Y + ")", 0, 7);
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