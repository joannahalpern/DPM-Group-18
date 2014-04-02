package Controller;

import Controller.*;
import lejos.nxt.Sound;
import Robot.*;

/**
 * Does both ultrasonic and light localization 
 */
public class Localization extends Thread{
	public static final int CLOSE_THRESHOLD = 40;
	
	public static double usA, usB;
	private double Tstart, Trange, Tfinal;
	private double T1,T2,T3,T4;
	private double lightSensorDist = Math.sqrt(TwoWheeledRobot.GROUND_LS_X_OFFSET*TwoWheeledRobot.GROUND_LS_X_OFFSET + TwoWheeledRobot.GROUND_LS_Y_OFFSET*TwoWheeledRobot.GROUND_LS_Y_OFFSET);
	public double angle = 0;
	public boolean wallFound = false;
	 
	private TwoWheeledRobot robot;
	private Navigation nav;
	private Odometer odo, turns; //turns used to simplify getting angle measurements 
	private UltrasonicPoller usPollerLeft;
	private UltrasonicPoller usPollerRight;
	private LightPoller csPoller;
 
	public Localization(Odometer odo, Navigation nav, UltrasonicPoller usPollerLeft, UltrasonicPoller usPollerRight, 
						LightPoller csPoller, TwoWheeledRobot robot) {
		this.robot = robot;
		this.nav = nav;
		this.odo = odo;
		this.usPollerLeft = usPollerLeft;
		this.usPollerRight = usPollerRight;
		this.csPoller = csPoller;
		turns = new Odometer(robot, true);
	}
	
	
	
	public void doUSLocalization(){
		
		try{Thread.sleep(250);} catch (Exception e){}; //give the poller some time to get values
		
		nav.setSpeeds(0, -40);
		while(usPollerLeft.getMedianDistance()<CLOSE_THRESHOLD || usPollerRight.getMedianDistance()<CLOSE_THRESHOLD){		}
		while(usPollerRight.getMedianDistance()>=CLOSE_THRESHOLD){	}
		
		turns.setAngle(0);
		usA = turns.getAngle();
		
		nav.setSpeeds(0, 40);
		while(usPollerLeft.getMedianDistance()<CLOSE_THRESHOLD || usPollerRight.getMedianDistance()<CLOSE_THRESHOLD){		}
		while(usPollerLeft.getMedianDistance()>=CLOSE_THRESHOLD){	}
		nav.setSpeeds(0, 0);
		
		usB = turns.getAngle();
		
		angle = 45 + (usB-usA)/2;
		odo.setAngle(angle);
		
	}
		
	public void doLSLocalization(){
		
		nav.turnTo(45, true, true);
		nav.travelDistance(8);
		
		turns.setAngle(0);		
		nav.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
//		double angle1 = odo.getAngleRadians() - Trange/2;
		T1 = accurateLineDetection();
		Sound.beep();
	
		nav.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T2 = accurateLineDetection();
		Sound.beep();
		
		nav.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T3 = accurateLineDetection();
		odo.setAngle( (-1*Math.atan(TwoWheeledRobot.GROUND_LS_X_OFFSET/TwoWheeledRobot.GROUND_LS_Y_OFFSET) + ((T2-T1)+(T3-T2))/2 + Math.PI*3 )*180/Math.PI);
		Sound.beep();
		
		nav.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T4 = accurateLineDetection();
		Sound.beep();
		
		nav.setSpeeds(0,0);
		
		calculateCurrentPosition();
	}
	//when a line is found, go back and get the exact range of angles where the line exists
	private double accurateLineDetection(){
		double tend;
		LightPoller.POLLING_PERIOD = 10; //speed up polling
		
		nav.setSpeeds(0,-20);
		while(!csPoller.lineSeen){
		}
		while(csPoller.lineSeen){
		}
		Tstart = turns.getAngleRadians();
		
		try{Thread.sleep(100);} catch (Exception e){};
		nav.setSpeeds(0,20);
		while(!csPoller.lineSeen){
		}
		while(csPoller.lineSeen){
		}

		tend = turns.getAngleRadians();
		Trange = tend-Tstart;
		
		Tfinal = Trange/2 + Tstart;
		
		LightPoller.POLLING_PERIOD = 30; //turn down polling speed again
		
		return Tfinal;
	}
	
	private void calculateCurrentPosition(){
		double firstAngle = T2-T1;
		double secondAngle = T3-T2;
		double thirdAngle = T3-T2;
		
		double thetaY = firstAngle+secondAngle;//(2*Math.PI - (firstAngle+secondAngle) );
		double thetaX = secondAngle+thirdAngle;//(2*Math.PI- (secondAngle+thirdAngle) );
		double x = -lightSensorDist* Math.cos(thetaX/2);//DIST_BTW_LS_AXLE is the radius of the circle
		double y = -lightSensorDist* Math.cos(thetaY/2);
		
		double[] pos = {x,y,99999090};
		boolean[] update = {true,true,false};
		
		synchronized(Controller.lock){ 
			odo.setPosition(pos, update);//update the odometer's values accordingly
			
		}
		
	}
	
}

