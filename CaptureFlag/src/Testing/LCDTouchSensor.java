package Testing;

import Lab5.*;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDTouchSensor implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private LightPoller lightPoller;
	private UltrasonicPoller usPoller;
	private USLocalizer usl;
//	private TouchPoller tPoller;

	public LCDTouchSensor(Odometer odo, LightPoller lightPoller, UltrasonicPoller usPoller/*, TouchPoller tPoller*/) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.lightPoller = lightPoller;
		this.usPoller = usPoller;
//		this.tPoller = tPoller;
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		LCD.clear();

//		LCD.drawString("Touch = " + tPoller.isTouching(), 0, 2);
		LCD.drawString("Distance = " + usPoller.getDistance(), 0, 4);
		LCD.drawString("Colour = " + lightPoller.getColourVal(), 0, 5);

		
	}
}
