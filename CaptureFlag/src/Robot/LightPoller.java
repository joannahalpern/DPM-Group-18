package Robot;

import java.util.Queue;

import Controller.*;
import lejos.nxt.ColorSensor;


public class LightPoller extends Thread{
	public static final int LINE_THRESHOLD = 300;
	public static final int QUEUE_SIZE = 9;
	public static long POLLING_PERIOD = 30; // (1 poll per 50ms)
	private ColorSensor ls;
	private double colourVal = 99999;
	private Colour colour;
	private Queue<Double> coloursQueue;
	public boolean lineSeen = false;
	
	public LightPoller(ColorSensor ls, Colour colour) {
		this.ls = ls;
		this.colour = colour;
		start();
		
		initializeQueue();
	}

	public void run() {
		setFloodLight(colour);
		while(true){
			colourVal = ls.getRawLightValue();
			coloursQueue.push(colourVal);
			coloursQueue.pop();
			if (ls.getRawLightValue()<=LINE_THRESHOLD){
				lineSeen = true;
			}
			else { lineSeen = false; }
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

	//initialize queue with 5 values
	private void initializeQueue() {
		coloursQueue = new Queue<Double>();
		for (int i = 0; i<QUEUE_SIZE; i++){
			coloursQueue.addElement(9999.9);
		}
	}
	
	/**
	 * computes mean of all the values in the coloursQueue
	 */
	public double getMean(){
		Double sum = 0.0;
		Double temp = 0.0;
		
		for (int i = 0; i<QUEUE_SIZE; i++){
			temp = ((Double) coloursQueue.pop());
			sum = sum + temp; //sum everything in queue
			coloursQueue.push(temp); //put values back in queue afterwards
		}
		double mean = (double) (sum/QUEUE_SIZE); //mean formula
		return mean;
	}
	
	/**
	 * computes median of all the values in the coloursQueue
	 * by putting queue into array, sorting the array with QuickSort,
	 * then returning the middle value of that array.
	 * 
	 * If the array is an even number size, it will return the larger
	 * of the two middle numbers
	 */
	public double getMedian(){
		double array[] = new double[QUEUE_SIZE];
		Double temp;
		
		//put all colour values from the queue into an array
		for (int i = 0; i<QUEUE_SIZE; i++){
			temp = ((Double) coloursQueue.pop());
			array[i] = temp; 
			coloursQueue.push(temp); //put values back in queue afterwards
		}
		
		//sort the array
		QuickSort.quickSort(array, 0, (QUEUE_SIZE-1));
		double median = array[(QUEUE_SIZE/2 + 1)]; //median is the middle number of the sorted array
		
		return median;
	}
}
