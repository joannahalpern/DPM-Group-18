package Robot;


import java.util.Queue;

import lejos.nxt.TouchSensor;

//This code is what was given in lab 1 except that we added myMutex
public class TouchPoller extends Thread{
	private static final long POLLING_PERIOD = 20;
	private TouchSensor ts;

	private boolean isTouching = false;
	
	public TouchPoller(TouchSensor ts) {
		this.ts = ts;
	}
	
	public void run() {
		while(true){
			isTouching = ts.isPressed();
			
			try { Thread.sleep(POLLING_PERIOD); } catch(Exception e){}
		}
	}

	public boolean isTouching() {
		return isTouching;
	}
}