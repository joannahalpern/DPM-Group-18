package Testing;
/*
 *  RConsole
 *  	-goto C:\Program Files (x86)\leJOS NXJ\bin\nxjconsoleviewer
 */

import bluetooth.*;
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
import Controller.*;

/**
 * This is the main controller. Everything is controlled from here
 *
 */
public class OdoCorTest2 {

	private static boolean bluetoothEnabled = false;
	public static StartCorner corner;
	public static int ourZoneLL_X = 4;
	public static int ourZoneLL_Y = 4;
	public static int ourZoneUR_X = 6;
	public static int ourZoneUR_Y = 6;
	public static int opponentZoneLL_X;
	public static int opponentZoneLL_Y;
	public static int opponentZoneUR_X;
	public static int opponentZoneUR_Y;
	public static int ourDZone_X;
	public static int ourDZone_Y;
	public static int opponentDZone_X;
	public static int opponentDZone_Y;
	public static int ourFlag = 1;
	public static int opponentFlag;
	
	//our flag
	static Colour ourFlagColour;
	
	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {//this is a lock
	}
	static public theLock lock = new theLock();
	
	public static void main(String[] args) {

		if (bluetoothEnabled){
			BluetoothConnection conn = new BluetoothConnection();
	
			Transmission t = conn.getTransmission();
			LCD.clear();
			if (t == null) {
				LCD.drawString("Failed to read transmission", 0, 0);
			} 
			else{
				switch (t.role) {
					case RED: 
						ourZoneLL_X = t.redZoneLL_X;
						ourZoneLL_Y = t.redZoneLL_Y;
						ourZoneUR_X = t.redZoneUR_X;
						ourZoneUR_Y = t.redZoneUR_Y;
						opponentZoneLL_X = t.greenZoneLL_X;
						opponentZoneLL_Y = t.greenZoneLL_Y;
						opponentZoneUR_X = t.greenZoneUR_X;
						opponentZoneUR_Y = t.greenZoneUR_Y;
						ourDZone_X = t.redDZone_X;
						ourDZone_Y = t.redDZone_Y;
						opponentDZone_X = t.greenDZone_X;
						opponentDZone_Y = t.greenDZone_Y;
						ourFlag = t.redFlag;
						opponentFlag = t.greenFlag;
						break;
					case GREEN:
						ourZoneLL_X = t.greenZoneLL_X;
						ourZoneLL_Y = t.greenZoneLL_Y;
						ourZoneUR_X = t.greenZoneUR_X;
						ourZoneUR_Y = t.greenZoneUR_Y;
						opponentZoneLL_X = t.redZoneLL_X;
						opponentZoneLL_Y = t.redZoneLL_Y;
						opponentZoneUR_X = t.redZoneUR_X;
						opponentZoneUR_Y = t.redZoneUR_Y;
						ourDZone_X = t.greenDZone_X;
						ourDZone_Y = t.greenDZone_Y;
						opponentDZone_X = t.redDZone_X;
						opponentDZone_Y = t.redDZone_Y;
						ourFlag = t.greenFlag;
						opponentFlag = t.redFlag;
						break;
					default:
						ourZoneLL_X = t.redZoneLL_X;
						ourZoneLL_Y = t.redZoneLL_Y;
						ourZoneUR_X = t.redZoneUR_X;
						ourZoneUR_Y = t.redZoneUR_Y;
						opponentZoneLL_X = t.greenZoneLL_X;
						opponentZoneLL_Y = t.greenZoneLL_Y;
						opponentZoneUR_X = t.greenZoneUR_X;
						opponentZoneUR_Y = t.greenZoneUR_Y;
						ourDZone_X = t.redDZone_X;
						ourDZone_Y = t.redDZone_Y;
						opponentDZone_X = t.greenDZone_X;
						opponentDZone_Y = t.greenDZone_Y;
						ourFlag = t.redFlag;
						opponentFlag = t.greenFlag;
						break;
				}
			}
		}
			
		if (ourFlag == 1){
			ourFlagColour = Colour.LIGHT_BLUE;
		}
		else if (ourFlag == 2){
			ourFlagColour = Colour.RED;
		}
		else if (ourFlag == 3){
			ourFlagColour = Colour.YELLOW;
		}
		else if (ourFlag == 4){
			ourFlagColour = Colour.WHITE;
		}
		else{
			ourFlagColour = Colour.DARK_BLUE; 
		}

		LCD.drawString("<--RED | GREEN->", 0, 0);
		LCD.drawString("     |Blue|     ", 0, 1);
		LCD.drawString("     | OFF|      ", 0, 2);
		LCD.drawString("second push exits", 0, 5);
		
		// setup everything
		UltrasonicSensor usLeft = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usRight = new UltrasonicSensor(SensorPort.S2);
		ColorSensor csFlagReader = new ColorSensor(SensorPort.S3);
		ColorSensor csLineReader = new ColorSensor(SensorPort.S4);
		
		UltrasonicPoller usPollerLeft = new UltrasonicPoller(usLeft);
		UltrasonicPoller usPollerRight = new UltrasonicPoller(usRight);
		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.BLUE);

		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C, Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		
		Navigation nav = new Navigation(odo, fuzzyPinkRobot);
		OdometryCorrection odoCorrection = new OdometryCorrection(odo, csPollerLineReader);
		Localization localizer = new Localization(odo, nav, usPollerLeft, usPollerRight, csPollerLineReader, fuzzyPinkRobot);

		ObstacleAvoidance ostacleAvoidance = new ObstacleAvoidance(fuzzyPinkRobot, nav, odo);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot, nav);
		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement);
		
		
		NavController navController = new NavController(odo, fuzzyPinkRobot,objectDisplacement, nav, objectDetection, ostacleAvoidance);
		
		initializeRConsole();
		RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot, csPollerLineReader);

//		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot, csPollerLineReader);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
			csPollerLineReader.setFloodLight(Colour.GREEN);
			navController.travelTo(0,(11*30.48), true, false, false);
			
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
