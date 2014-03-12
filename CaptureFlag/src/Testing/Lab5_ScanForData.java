package Testing;


import Controller.*;
import Display.*;
import Robot.*;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab5_ScanForData {
	public static Colour colour = Colour.OFF;
	
	public static void main(String[] args) {
		
		LCD.clear();
		
		LCD.drawString("     LAB 5      ", 0, 0);
		LCD.drawString("Press center for", 0, 2);
		LCD.drawString(" translational  ", 0, 3);
		LCD.drawString("     scan       ", 0, 4);
		

		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B,Motor.C);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Odometer marshmallow = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);

		// perform the light sensor localization
		LightPoller lsPoller = new LightPoller( ls, Colour.BLUE);
		
		initializeRConsole();
		RConsoleDisplay rcd = new RConsoleDisplay(odo, lsPoller, usPoller);
//		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller, usLocalizer);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			nav.turnTo(0, true);
			break;
		case Button.ID_RIGHT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			nav.turnTo(179, true);
			nav.turnTo(181, true);
			nav.turnTo(0, true);
			break;
		case Button.ID_ENTER:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			break;
		case Button.ID_ESCAPE:
//			try { Thread.sleep(1000); } catch(Exception e){}
			colour = Colour.OFF;
			lsPoller.start();
			usPoller.start();
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}

	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}