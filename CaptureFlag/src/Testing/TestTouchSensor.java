package Testing;

import Lab5.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class TestTouchSensor {

	public static void main(String[] args) {
		
		LCD.clear();
		LCD.drawString("Test TouchSensor", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B, Motor.C);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
//		TouchSensor ts = new TouchSensor(SensorPort.S3);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);
		LightPoller lsPoller = new LightPoller(ls, Colour.BLUE);
//		TouchPoller tPoller = new TouchPoller(ts);

//		initializeRConsole();
//		RConsoleDisplay rcd = new RConsoleDisplay(odo, lsPoller, usPoller);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		LCDTouchSensor lcd = new LCDTouchSensor(odo, lsPoller, usPoller/*, tPoller*/);

		switch(option) {
		case Button.ID_LEFT:
			try { Thread.sleep(1000); } catch(Exception e){}
			lsPoller.start();
			usPoller.start();
//			tPoller.start();
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
