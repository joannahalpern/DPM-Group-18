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
		//
		// LightPoller csPollerLineReader = new LightPoller(csLineReader,
		// Colour.BLUE);
		LightPoller colourDetector = new LightPoller(csFlagReader,Colour.BLUE);
		//
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C,
				Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot);
		Navigation nav = new Navigation(odo, fuzzyPinkRobot, objectDisplacement, colourDetector);
		// OdometryCorrection odoCorection = new OdometryCorrection(odo,
		// csPollerLineReader);
		//
		// Localization localizer = new Localization(odo, nav, usPollerLeft,
		// usPollerRight, csPollerLineReader);

		// ObjectDetectIdentify objectDetection = new
		// ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement);

		// usPollerLeft);
		
		
		
		//initializeRConsole();
		//RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot);
		
		
		
		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();

		switch (option) {
		case Button.ID_LEFT:

			// PUT MAIN CODE HERE
			
			//Turning Testing
			//nav.turnTo(360, true, true); // pass
			
			nav.travelTo(0,90, false, false, false);
			nav.travelTo(30, 90, false, false, false);
			nav.travelTo(30, 0 , false,  false, false);
			
			
			//nav.turnTo(90, true); // fail- Poorly coded to radians loop, loop was if > 90. 
			//nav.travelTo(0, 90, false, false); // pass
			//nav.travelTo(30, 60, false, false); // pass

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