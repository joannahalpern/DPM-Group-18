package Controller;

import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
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
		LCD.drawString("     LAB 5      ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup everything
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor lsLeft = new ColorSensor(SensorPort.S1);
		ColorSensor lsRight = new ColorSensor(SensorPort.S3);
		ColorSensor colourDetectorSensor = new ColorSensor(SensorPort.S4);
		UltrasonicPoller usPoller = new UltrasonicPoller(us);
		LightPoller lsPollerLeft = new LightPoller(lsLeft, Colour.BLUE);
		LightPoller lsPollerRight = new LightPoller(lsRight, Colour.BLUE);
		LightPoller colourDetector = new LightPoller(colourDetectorSensor, Colour.BLUE);

		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B, Motor.C);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Navigation nav = new Navigation(odo);
		OdometryCorrection odoCorection = new OdometryCorrection(odo, lsPollerLeft, lsPollerRight);
		
		Localization localizer = new Localization(odo, nav, usPoller, lsPollerLeft, lsPollerRight);
		
		ObstacleAvoidance avoider = new ObstacleAvoidance(nav, usPoller, odo);
		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(usPoller, colourDetector, nav);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(usPoller, colourDetector, nav);
		
		
		initializeRConsole();
		RConsoleDisplay rcd = new RConsoleDisplay(odo, colourDetector, usPoller);
//		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller, usLocalizer);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}
	/**
	 * Claw of robot grab block
	 */
	private static void grabBlock() {
	}
	/**
	 * claw releases block
	 */
	private static void dropBlock() {
	}

	//for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
