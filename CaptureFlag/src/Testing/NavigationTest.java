package Testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import Controller.*;
import Robot.*;
import Display.*;

public class NavigationTest {

	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {// this is a lock
	}

	static public theLock lock = new theLock();

	public static void main(String[] args) {

		LCD.clear();
		LCD.drawString("   Navigation   ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);

		// setup everything
		UltrasonicSensor usLeft = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usRight = new UltrasonicSensor(SensorPort.S2);
		ColorSensor csFlagReader = new ColorSensor(SensorPort.S3);
		ColorSensor csLineReader = new ColorSensor(SensorPort.S4);

		// UltrasonicPoller usPollerLeft = new UltrasonicPoller(usLeft);
		// UltrasonicPoller usPollerRight = new UltrasonicPoller(usRight);

		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.BLUE);
		LightPoller colourDetector = new LightPoller(csFlagReader,Colour.BLUE);

		
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C,Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Navigation nav = new Navigation(odo, fuzzyPinkRobot);
		
		ObstacleAvoidance obstacleAvoidance =  new ObstacleAvoidance(fuzzyPinkRobot, nav, odo);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot,nav);
		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement, colourDetector);

		NavController navController = new NavController(odo, fuzzyPinkRobot,objectDisplacement, colourDetector, nav, objectDetection, obstacleAvoidance);
		OdometryCorrection odoCorection = new OdometryCorrection(odo, csPollerLineReader);
		//
		// Localization localizer = new Localization(odo, nav, usPollerLeft,
		// usPollerRight, csPollerLineReader);


		// usPollerLeft);
		
		
		
		//initializeRConsole();
		//RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot);
		
		
		
		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot);
		
		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();

		switch (option) {
		case Button.ID_LEFT:

			while(true){
				
				if(option == Button.ID_LEFT ){
					
					option = Button.ID_ENTER;
					
					// PUT MAIN CODE HERE
					LCD.clear();
					LCD.drawString("Starts Travel ", 0, 4);
					
					//Navigation
//					navController.travelTo(0,120,false,false,false);
//					navController.travelTo(120,120,false,false,false);
//					navController.travelTo(120,240,false,false,false);
//					nav.turnTo(0,true,true);
					
				
					
					
					//Travel in a sqaure
					/*
					navController.travelTo(0,30, false, false, false);
					navController.travelTo(30, 30, false, false, false);
					navController.travelTo(30, 0 , false,  false, false);
					navController.travelTo(0, 0 , false,  false, false);
					nav.turnTo(0,true,true);
					*/
				
//					navController.setX(120);
//					navController.setY((120));
//					navController.setAxis(false);
//					LCD.drawString("here", 0, 7);
//					 
//					navController.travelTo(0,120, true, false, false);
//					navController.search(4*30.48, 4*30.48, 6*30.48, 6*30.48, true, Colour.LIGHT_BLUE);
//					navController.avoidanceSetter(120, 240, false);
//					navController.travelTo(0, 240, true, false, false);
		
					//Searching and moving
					navController.search(1*30.48, 1*30.48, 3*30.48, 3*30.48, true, Colour.RED);
					Sound.beep();
					navController.avoidanceSetter(120, 120, false);
					navController.travelTo(odo.getX(), 120, true, false, false);
					Sound.beep();
					
				}
				if( option == Button.ID_RIGHT){
					break;
				}
			}
			
			
			break;
			default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}

		Button.waitForAnyPress();
		System.exit(0);

	}

	// for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}