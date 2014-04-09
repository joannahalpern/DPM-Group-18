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
public class ControllerBl {
	
	//When bluetooth is not enabled, all of the values should be set here
	private static boolean bluetoothEnabled = true;
	public static StartCorner corner = StartCorner.BOTTOM_LEFT;
	public static int ourZoneLL_X = 2;
	public static int ourZoneLL_Y = 2;
	public static int ourZoneUR_X = 5;
	public static int ourZoneUR_Y = 4;
	public static int opponentZoneLL_X;
	public static int opponentZoneLL_Y;
	public static int opponentZoneUR_X;
	public static int opponentZoneUR_Y;
	public static int ourDZone_X = 1;
	public static int ourDZone_Y = 1;
	public static int opponentDZone_X;
	public static int opponentDZone_Y;
	public static int ourFlag = 1; 
	public static int opponentFlag;
	public static Task task = Task.LOCALIZING;
	
	//our flag
	public static Colour ourFlagColour;
	
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
		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.GREEN);

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

			LCD.clear();
			LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot, csPollerLineReader);
			
			//Localization
			task = Task.LOCALIZING;
			localizer.doUSLocalization();
			localizer.doLSLocalization();
			//Start OdoCorrection
//			odoCorrection.start();
			
			//Full Search and Nav
			task = Task.NAVIGATING;
			int x0,y0,x1,y1;
			int xf, yf;
			final double SQUARE = 30.48;
			Colour flag = ourFlagColour;
			
			
			x0 = ourZoneLL_X;
			y0 = ourZoneLL_Y;
			x1 = ourZoneUR_X;
			y1 = ourZoneUR_Y;
			xf = ourDZone_X; 
			yf = ourDZone_Y;
			
			//If top Right corner, inverts search zone so travels to top right corner instead of bvottom left
			if ((odo.getX() > x1 && odo.getY() > y1)){

				//Changes search algorithm depending if search zone is wider than it is long
				if((x1-x0) > (y1-y0)){
					navController.longX = true;
				}
				else if((x1-x0) < (y1-y0)){
					navController.longX = false;
				}

				navController.inv = -1;

				//Find zone
				//Avoidance setter sets destiantion of TravelTo and original heading
				navController.avoidanceSetter(x1*SQUARE +15, y1*SQUARE +15, false);
				//Travel to call travels along y until object
				navController.travelTo(0, y1*SQUARE + 15, true,  false);

				//If robot never encounters obstacles, will then travel to destination
				if(navController.xReached){
					navController.travelTo(x0*SQUARE+15, y0*SQUARE+15, true,  false);
				}

				//Search and Exit zone    
				task = Task.SEARCHING;
				//Iniaties search, travels aroudn edge of search zone, then through middle, and then gradually increases ntil it finds the falg
				navController.search(x1*SQUARE, y1*SQUARE,x0*SQUARE,y0*SQUARE, true, ourFlagColour);


				//Travels out of the zone without obstacle avoidance
				//This should be improved, but not neccesary
				//May have to travel back through zone ot get to final destnation
				task = Task.DROPPING_OFF;
				navController.travelTo(x0*SQUARE, y0*SQUARE, false,  false);

				//Travel to destination
				Sound.beepSequenceUp();
				//Sets final values for navitgation
				navController.avoidanceSetter(xf*SQUARE, yf*SQUARE, false);
				//Travels along y axis until reaches an obstacle or yf
				navController.travelTo(odo.getX(), yf*SQUARE, true,  false);
				//If never turns need to reach final destination
				//Again, this shoudl probably be in an if statement
				if(!navController.xReached){
						navController.travelTo(xf*SQUARE, yf*SQUARE, true,  false);
				}
				objectDisplacement.release();

			}
			//If any other corner travels to x0,y0
			else{
				//if wider than long, changes search algorrithm according
				if((x1-x0) > (y1-y0)){
					navController.longX = true;
				}


				else if((x1-x0) < (y1-y0)){
					navController.longX = false;
				}

				navController.avoidanceSetter(x0*SQUARE - 15, y0*SQUARE -15, false);
				navController.travelTo(0, y0*SQUARE-15, true,  false);
				if(navController.xReached){
					navController.travelTo(x0*SQUARE-15, y0*SQUARE-15, true,  false);
				}

				//Search
				navController.search(x0*SQUARE, y0*SQUARE,x1*SQUARE,y1*SQUARE, true, flag);


				//Leave search zone
				navController.travelTo(x1*SQUARE, y1*SQUARE, false,  false);

				//Travel to Final destination
				navController.avoidanceSetter(xf*SQUARE, yf*SQUARE, false);
				navController.travelTo(odo.getX(), yf*SQUARE, true,  false);
				if(navController.xReached){
					navController.travelTo(xf*SQUARE, yf*SQUARE, true,  false);
				}
			}
	}
	//for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
//		RConsole.openBluetooth(20000);
		RConsole.println("Connected");
	}
}
