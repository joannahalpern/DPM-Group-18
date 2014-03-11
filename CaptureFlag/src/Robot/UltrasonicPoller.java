package Robot;


import java.util.Queue;

import all.QuickSort;
import all.USLocalizer;
import lejos.nxt.UltrasonicSensor;

//This code is what was given in lab 1 except that we added myMutex
public class UltrasonicPoller extends Thread{
	public static final int QUEUE_SIZE = 5;
	public static final long POLLING_PERIOD = 28;
	public static final double SENSOR_OFFSET = 1;
	private UltrasonicSensor us;
	private USLocalizer uSLocalizer;
	private double distance = 99999;
	private Queue<Double> distancesQueue;
	
	public UltrasonicPoller(UltrasonicSensor us) {
		this.us = us;
		
		initializeQueue();
	}
	
	public void run() {
		while(true){
			distance = us.getDistance() + SENSOR_OFFSET;
			putDistanceInQueue(distance);
		
			try { Thread.sleep(POLLING_PERIOD); } catch(Exception e){}
		}
	}
	
	private void putDistanceInQueue(double distance) {
		if (distance == 255){
			distance = 60;
		}
		distancesQueue.push(distance);
		distancesQueue.pop();
	}

	private void initializeQueue() {
		distancesQueue = new Queue<Double>();
		for (int i = 0; i<QUEUE_SIZE; i++){
			distancesQueue.addElement(9999.9);
		}
	}
	
	public double getMeanDistance(){
		Double sum = 0.0;
		Double temp = 0.0;
		
		for (int i = 0; i<QUEUE_SIZE; i++){
			temp = ((Double) distancesQueue.pop());
			sum = sum + temp; //sum everything in queue
			distancesQueue.push(temp); //put values back in queue afterwards
		}
		double mean = (double) (sum/QUEUE_SIZE); //mean formula
		return mean;
	}
	public double getMedianDistance(){
		double array[] = new double[QUEUE_SIZE];
		Double temp;
		
		//put all colour values from the queue into an array
		for (int i = 0; i<QUEUE_SIZE; i++){
			temp = ((Double) distancesQueue.pop());
			array[i] = temp; 
			distancesQueue.push(temp); //put values back in queue afterwards
		}
		
		//sort the array
		QuickSort.quickSort(array, 0, (QUEUE_SIZE-1));
		double median = array[(QUEUE_SIZE/2 + 1)]; //median is the middle number of the sorted array
		
		return median;
	}
	public double getDistance(){
		return distance;
	}

}
