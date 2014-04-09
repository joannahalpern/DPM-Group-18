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
	public static final int LCD_REFRESH = 50;
	private Timer lcdTimer;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor csFlagReader, csLineReader;
	private UltrasonicSensor usLeft, usRight;
//	private UltrasonicPoller usPollerLeft;
//	private UltrasonicPoller usPollerRight;
	private LightPoller linePoller;
//	private LightPoller csFlagPoller;
	
	// arrays for displaying data
	
	public RConsoleDisplay(Odometer odo, TwoWheeledRobot robot, LightPoller linePoller) {
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
		RConsole.println("" + odo.getAngle() + ", " + linePoller.getColourVal());

//		RConsole.println("" + odo.getX() + ", " + odo.getY() + ", " + odo.getAngle());

//		RConsole.println("");
//		RConsole.println("" + Controller.task);
//		RConsole.println("");
//		RConsole.println("");
//		RConsole.println("X: " + (int)odo.getX());
//		RConsole.println("Y: " + (int)odo.getY());	
//		RConsole.println("Angle: " + (int)odo.getAngle());
//		RConsole.println("");
//		RConsole.println("");
//		RConsole.println("Search Zone: (" + Controller.ourZoneLL_X + ", " + Controller.ourZoneLL_Y + ") to (" + Controller.ourZoneUR_X + ", " + Controller.ourZoneUR_Y + ")");
//		RConsole.println("Flag coulour: " + Controller.ourFlagColour);
//		
//		
//		switch (Controller.task){
//			case NAVIGATING:
//				break;
//			case SEARCHING:
//				break;
//			default:
//				 break;
//		}
	}
}
