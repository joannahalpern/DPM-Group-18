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
		avoidObstacles();
	}
	public void avoidObstacles(){
		while (Lab5.obstacleAvoidance == true){
			if (isBlockinRange(usPoller, blockThreshold)){
				
				synchronized (Lab5.lock) {
					Lab5.navigate = false;
					do{
						nav.turnTo( (90+odo.getAng()), true);
					} while (isBlockinRange(usPoller, blockThreshold));
					nav.goForward(blockThreshold-2);
					Lab5.navigate = true;
				}
			}
			try { Thread.sleep(500); } catch(Exception e){}
			
		}
	}


	public boolean isBlockinRange(UltrasonicPoller usPoller, double threshold){
		double distance = usPoller.getMeanDistance();
		if (distance< threshold){
			return true;
		}
		return false;
	}
	
	public void setBlockThreshold(double blockThreshod){
		this.blockThreshold = blockThreshod;
	}
}