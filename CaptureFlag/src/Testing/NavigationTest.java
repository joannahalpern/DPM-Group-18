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

		UltrasonicPoller usPollerLeft = new UltrasonicPoller(usLeft);
		UltrasonicPoller usPollerRight = new UltrasonicPoller(usRight);

		
		LightPoller csPollerLineReader = new LightPoller(csLineReader, Colour.GREEN);
		//LightPoller colourDetector = new LightPoller(csFlagReader,Colour.BLUE);

		
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.C,Motor.B, usLeft, usRight, csFlagReader, csLineReader);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Navigation nav = new Navigation(odo, fuzzyPinkRobot);
		
		ObstacleAvoidance obstacleAvoidance =  new ObstacleAvoidance(fuzzyPinkRobot, nav, odo);
		ObjectDisplacement objectDisplacement = new ObjectDisplacement(fuzzyPinkRobot,nav);
		ObjectDetectIdentify objectDetection = new ObjectDetectIdentify(fuzzyPinkRobot, nav, objectDisplacement);

		NavController navController = new NavController(odo, fuzzyPinkRobot,objectDisplacement, nav, objectDetection, obstacleAvoidance);
		OdometryCorrection odoCorrection = new OdometryCorrection(odo, csPollerLineReader);
		
		Localization localizer = new Localization(odo, nav, usPollerLeft,usPollerRight, csPollerLineReader, fuzzyPinkRobot);


		// usPollerLeft);
		
		
		
		//initializeRConsole();
		//RConsoleDisplay rcd = new RConsoleDisplay(odo, fuzzyPinkRobot);
		
		
		
		LCDInfo lcd = new LCDInfo(odo, fuzzyPinkRobot, csPollerLineReader);

		
		
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
					
					
					//With Localization
					//Localization
					localizer.doUSLocalization();
					localizer.doLSLocalization();
					
//					nav.travelTo(0,0);
//					nav.turnTo(0, true, true);
//					odoCorrection.start();
					
					//Search
	/*				
					int x0,y0,x1,y1;
					int xf, yf;
					double square = 30.48;
					navController.flagColour= Colour.DARK_BLUE;
					x0 = 1;
					y0 = 1;
					x1 = 4;
					y1 = 3;
					xf = 1;
					yf = 1;
//					while(true){
//						objectDetection.isBlock(); 
//					}
					navController.longX = true;
					navController.search(x0*square, y0*square,x1*square,y1*square, true, Colour.YELLOW);
					navController.travelTo(xf,yf,true,false);
	*/


					
					//Full Search and Nav
					int x0,y0,x1,y1;
					int xf, yf;
					double square = 30.48;
					Colour flag = Colour.RED;
					x0 = 1;
					y0 = 1;
					x1 = 3;
					y1 = 4;
					xf = 1;
					yf = 1;
					
					
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
						
						//odo correction 5 cm
						
						else if((x1-x0) < (y1-y0)){
							navController.longX = false; //wider than long
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