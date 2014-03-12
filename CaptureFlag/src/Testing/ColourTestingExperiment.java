package Testing;
/*
 * Lab5- Group 53 - ColourTestingExperiment
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.*;
import Controller.*;
import Display.*;
import Robot.*;

public class ColourTestingExperiment {
	
	public static void main(String[] args) {
		
		LCD.clear();

		// ask the user whether the motors should drive in a square or float
		LCD.drawString("<--RED | GREEN->", 0, 0);
		LCD.drawString("     |Blue|     ", 0, 1);
		LCD.drawString("     | OFF|      ", 0, 2);
		LCD.drawString("second push exits", 0, 5);
		

		
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
		

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller);
		
		switch(option) {
		case Button.ID_LEFT:
			lsPoller.setFloodLight(Colour.RED);
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_RIGHT:
			lsPoller.setFloodLight(Colour.GREEN);
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_ENTER:
			lsPoller.setFloodLight(Colour.BLUE);
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_ESCAPE:
			lsPoller.setFloodLight(Colour.OFF);
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
	public static Colour chooseColour(int counter){
		Colour colour;
		if (counter == 0){
			colour = Colour.RED;
		}
		else if(counter == 1){
			colour = Colour.GREEN;
		}
		else if(counter == 2){
			colour = Colour.BLUE;
		}
		else{
			colour = Colour.OFF;
		}
		return colour;
	}
}