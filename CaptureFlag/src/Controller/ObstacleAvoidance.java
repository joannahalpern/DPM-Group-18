package Controller;

import Display.*;
import Robot.*;

/**
 * While Lab5.obstacleAvoidance == true:
 * 
 * if us sensor sees something within threshold, it stops navigation (navigation = false), and turns right.
 * It then checks if something's in front, if it is it turns right again. If not, moves forward an amount d (d should be bigger than a block),
 * then it sets navigation to true again. (some of this should be in a synchronized lock)
 */
public class ObstacleAvoidance extends Thread {
	private double blockThreshold;
	Navigation nav;
	UltrasonicPoller usPoller;
	Odometer odo;
	
	
	public ObstacleAvoidance(Navigation nav, UltrasonicPoller usPoller, Odometer odo) {
		this.nav = nav;
		this.usPoller = usPoller;
		this.odo = odo;
		this.blockThreshold = 43;
	}
	
	public void run(){
	}
}