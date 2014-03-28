/*
 * To change:
 * 	-make and TestNav___ for each thing and replace folder for each
 *  -check that no more arrays
 *  
 * 	-email Connor to test nav on TestNav
 *  email team to name well and alt-shift-r and ctrl-d
 *  email team about RConsole
 *  email Ben about detection and that we pollers
 *  
 *  RConsole
 *  	-goto C:\Program Files (x86)\leJOS NXJ\bin\nxjconsoleviewer
 */

package Controller;

import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import Display.*;
import Robot.*;

/**
 * This is the main controller. Everything is controlled from here
 *
 */
public class Controller {
	
	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {//this is a lock
	}
	static public theLock lock = new theLock();
	
	public static void main(String[] args) {
		
		LCD.clear();
		LCD.drawString("   Controller   ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup everything
		UltrasonicSensor usLeft = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usRight = new UltrasonicSensor(SensorPort.S2);
		ColorSensor csFlagReader = new ColorSensor(SensorPort.S3);
		ColorSensor csLineReader = new ColorSensor(SensorPort.S4);
		
		UltrasonicPoller usPollerLeft = new UltrasonicPoller(usLeft);
		UltrasonicPoller usPollerRight = new UltrasonicPoller(usRight);
		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.BLUE);
		LightPoller colourDetector = new LightPoller(csFlagReader, Colour.BLUE);

		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C, Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		
		Navigation nav = new Navigation(odo, fuzzyPinkRobot);
		OdometryCorrection odoCorection = new OdometryCorrection(odo, csPollerLineReader);
		Localization localizer = new Localization(odo, nav, usPollerLeft, usPollerRight, csPollerLineReader);

		ObstacleAvoidance ostacleAvoidance = new ObstacleAvoidance(fuzzyPinkRobot, nav, odo);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot, nav);
		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement, colourDetector);
		
		
		NavController navController = new NavController(odo, fuzzyPinkRobot,objectDisplacement, colourDetector, nav, objectDetection, ostacleAvoidance);
		
//		initializeRConsole();
//		RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot);
		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			
			
			//change this accoridngly
			Colour flagColour = Colour.YELLOW;
			
			
			
			
			
			//PUT MAIN CODE HERE
			
			//Localization
			
			
			
			navController.setX(120);
			navController.setY((120));
			navController.setAxis(false);
			
			navController.travelTo(odo.getX(),120, true, false, false);
			navController.search(4*30.48, 4*30.48, 6*30.48, 6*30.48, false, flagColour);
			
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}
	//for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
