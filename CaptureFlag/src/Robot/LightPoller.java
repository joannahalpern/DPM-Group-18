package Robot;

import java.util.Queue;

import Controller.*;
import lejos.nxt.ColorSensor;


public class LightPoller extends Thread{
	public static final int LINE_THRESHOLD_DIFFERENCE = 90;
	public static final double LINE_THRESHOLD = Localization.intlReading-LINE_THRESHOLD_DIFFERENCE;
	public static final int QUEUE_SIZE = 9;
	public static long POLLING_PERIOD = 30; // (1 poll per 50ms)
	private ColorSensor ls;
	private double colourVal = 99999;
	private Colour colour;
	private Queue<Double> coloursQueue;
	private double value1, value2, value3; 
	public boolean lineSeen = false;
	
	public LightPoller(ColorSensor ls, Colour colour) {
		this.ls = ls;
		this.colour = colour;
		start();
	}

	public void run() {
		setFloodLight(colour);
		while(true){
			value1 = value2;
			value2 = value3;
			
			colourVal = ls.getRawLightValue();
			value3 = colourVal;
			
			lineSeen = isLine();
			try { Thread.sleep(POLLING_PERIOD); } catch(Exception e){}
		}
	}

	public void setFloodLight(Colour colour) {
		ls.setFloodlight(true);
		switch (colour){
			case RED:
				ls.setFloodlight(false);
				ls.setFloodlight(ColorSensor.Color.RED);
				break;
				
			case GREEN:
				ls.setFloodlight(false);
				ls.setFloodlight(ColorSensor.Color.GREEN);
				break;
				
			case BLUE:
				ls.setFloodlight(false);
				ls.setFloodlight(ColorSensor.Color.BLUE);
				break;
			
			default:
				ls.setFloodlight(false);
				break;
		}
	}

	public double getColourVal() {
		return colourVal;
	}

	public boolean isLine(){
		double negativeDiff = value2-value1;
		double positiveDiff = value3-value2;
		if ((negativeDiff<0) && (positiveDiff>0)){ //If it's a dip like \/
			if( ((-1 * negativeDiff)>LINE_THRESHOLD) || (positiveDiff > LINE_THRESHOLD)){
				return true;
			}
		}
		return false;
	}
}
