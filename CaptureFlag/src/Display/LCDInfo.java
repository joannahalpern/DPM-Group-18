package Display;

import Controller.*;
import Robot.*;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private LightPoller lightPoller;
	private UltrasonicPoller usPoller;
	private USLocalizer usl;

	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, LightPoller lightPoller, UltrasonicPoller usPoller, USLocalizer usl) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.lightPoller = lightPoller;
		this.usPoller = usPoller;
		this.usl = usl;

		
		
		
		// initialize the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
//		LCD.drawString("X: ", 0, 0);
//		LCD.drawString("Y: ", 0, 1);
//		LCD.drawString("H: ", 0, 2);
		LCD.drawString("Theta = " + odo.getAng(), 0, 1);
		
//		LCD.drawString("Colour = " + lightPoller.getColourVal(), 0, 3);
		LCD.drawString("Median = " + usPoller.getMedianDistance(), 0, 2);
		LCD.drawString("Distance = " + usPoller.getDistance(), 0, 3);
		LCD.drawString("AngleA = " + usl.getAngleA() , 0, 5);
		LCD.drawString("AngleB = " + usl.getAngleB() , 0, 6);
		
//		for (int i = 1; i<4; i++){
//			LCD.drawString("Line " + i + "= " +LightLocalizer.getAngle(i), 0, i+4);
//		}
		
	}
}
