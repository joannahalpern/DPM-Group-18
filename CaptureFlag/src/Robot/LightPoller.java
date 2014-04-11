package Robot;

import java.util.Queue;

import Controller.*;
import lejos.nxt.ColorSensor;

/**
 * This is class is used to accurately detect lines and it uses the method isLine() to do this. 
 * This line detection is then used in both odometry correction and localization
 * @author Joanna
 *
 */
public class LightPoller extends Thread{
	public static final int LINE_THRESHOLD_DIFFERENCE = 68;
//	public static final double LINE_THRESHOLD = Localization.intlReading-LINE_THRESHOLD_DIFFERENCE;
	public static final int QUEUE_SIZE = 9;
	public static long POLLING_PERIOD = 50; // (1 poll per 50ms)
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
	/**
	 * The way the light poller works is that each POLLING_PERIOD, the colour sensor records 
	 * the light value from the floor. It saves that value as well as the two previous values.
	 */
	public void run() {
		long correctionStart, correctionEnd;
		setFloodLight(colour);
		while(true){
			correctionStart = System.currentTimeMillis();
			value1 = value2;
			value2 = value3;
			
			colourVal = ls.getRawLightValue();
			value3 = colourVal;
			
			lineSeen = isLine();
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < POLLING_PERIOD) {
				try {
					Thread.sleep(POLLING_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
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
	/**
	 * A line is detected if there is a drop followed by a rise, and either the drop or the rise 
	 * is greater than LINE_TRESHOLD_DIFFERENCE.
	 * @return
	 */
	public boolean isLine(){
		double negativeDiff = value2-value1;
		double positiveDiff = value3-value2;
		if ((negativeDiff<0) && (positiveDiff>0)){ //If it's a dip like \/
			if( ((-1 * negativeDiff)>LINE_THRESHOLD_DIFFERENCE) || (positiveDiff > LINE_THRESHOLD_DIFFERENCE)){
				return true;
			}
		}
		return false;
	}

	public static void setPOLLING_PERIOD(long POLLING_PERIOD) {
		POLLING_PERIOD = POLLING_PERIOD;
	}
}
