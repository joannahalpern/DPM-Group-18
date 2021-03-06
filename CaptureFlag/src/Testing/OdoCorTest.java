package Testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import Controller.*;
import Robot.*;
import Display.*;

public class OdoCorTest {
	
	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {//this is a lock
	}
	static public theLock lock = new theLock();
	
	public static void main(String[] args) {
		
		LCD.clear();
		LCD.drawString(" Odo Correction ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup everything
		UltrasonicSensor usLeft = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usRight = new UltrasonicSensor(SensorPort.S2);
		ColorSensor csFlagReader = new ColorSensor(SensorPort.S3);
		ColorSensor csLineReader = new ColorSensor(SensorPort.S4);
		
//		UltrasonicPoller usPollerLeft = new UltrasonicPoller(usLeft);
//		UltrasonicPoller usPollerRight = new UltrasonicPoller(usRight);
//		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.BLUE);
//		LightPoller colourDetector = new LightPoller(csFlagReader, Colour.BLUE);
//
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C, Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Navigation nav = new Navigation(odo, fuzzyPinkRobot);
		OdometryCorrection odoCorection = new OdometryCorrection(odo, csPollerLineReader);
//		
//		Localization localizer = new Localization(odo, nav, usPollerLeft, usPollerRight, csPollerLineReader);
//		
//		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot, nav);
//		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement);
		
		
//		initializeRConsole();
//		RConsoleDisplay rcd = new RConsoleDisplay(odo, colourDetector, usPollerLeft);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
			case Button.ID_LEFT:
				LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot, csPollerLineReader);
				
			//PUT MAIN CODE HERE
				nav.travelTo(0,90);
				nav.travelTo(30, 90);
				nav.travelTo(30, 0);
				nav.travelTo(0, 0);
				
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
