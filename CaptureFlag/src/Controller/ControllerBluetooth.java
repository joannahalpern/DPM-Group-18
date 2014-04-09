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

import bluetooth.*;
import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import Display.*;
import Robot.*;

/**
 * This is the main controller. Everything is controlled from here
 *
 */
public class ControllerBluetooth {

	private static boolean bluetoothEnabled = true;
	public static StartCorner corner = StartCorner.BOTTOM_LEFT;
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

		LCD.drawString("   Controller   ", 0, 1);
		LCD.drawString("   Press left   ", 0, 3);
		LCD.drawString("    to begin    ", 0, 4);
		LCD.drawString("redflag: " + ourFlagColour.toString(), 0, 5);
		
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
		
//		initializeRConsole();
//		RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot);

			
//////////----MAIN CODE-------//////////		
		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot, csPollerLineReader);
		
		//Localization
		localizer.doUSLocalization();
		localizer.doLSLocalization();
		
		//Start OdoCorrection
		odoCorrection.start();
		
		//Full Search and Nav
		int x0,y0,x1,y1;
		int xf, yf;
		double square = 30.48;
		Colour flag = Colour.RED;
		
		x0 = ourZoneLL_X;
		y0 = ourZoneLL_Y;
		x1 = ourZoneUR_X;
		y1 = ourZoneUR_Y;
		xf = ourDZone_X; 
		yf = ourDZone_Y;
		
		//If top Right corner
		if ((odo.getX() > x1 && odo.getY() > y1)){
			if((x1-x0) > (y1-y0)){
				navController.longX = true;
			}
			else if((x1-x0) < (y1-y0)){
				navController.longX = false;
			}
			
			navController.inv = -1;
			
			//Find zone
			navController.avoidanceSetter(x1*square -10, y1*square -10, false);
			navController.travelTo(0, y1*square - 10, true,  false);
			navController.travelTo(x0*square-10, y0*square-10, true,  false);

			//Search and Exit zone
			navController.search(x1*square, y1*square,x0*square,y0*square, true, flag);
			
			navController.travelTo(x0*square, y0*square, false,  false);
			
			//Travel to destination
			Sound.beepSequenceUp();
			navController.avoidanceSetter(xf*square, yf*square, false);
			navController.travelTo(odo.getX(), yf*square, true,  false);
			navController.travelTo(xf*square, yf*square, true,  false);
			
		}
		//If any other corner
		else{
			
			if((x1-x0) > (y1-y0)){
				navController.longX = true;
			}
			
			
			else if((x1-x0) < (y1-y0)){
				navController.longX = false;
			}
			
			navController.avoidanceSetter(x0*square - 10, y0*square -10, false);
			navController.travelTo(0, y0*square-10, true,  false);
			navController.travelTo(x0*square-10, y0*square-10, true,  false);
			//Search
			navController.search(x0*square, y0*square,x1*square,y1*square, true, flag);
			
			
			//Leave search zone
			navController.travelTo(x1*square, y1*square, false,  false);
			
			//Travel to Final destination
			navController.avoidanceSetter(xf*square, yf*square, false);
			navController.travelTo(odo.getX(), yf*square, true,  false);
			navController.travelTo(xf*square, yf*square, true,  false);
		}
	}

	//for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
