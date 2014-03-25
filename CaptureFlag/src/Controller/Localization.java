package Controller;

import Robot.*;

/**
 * Does both ultrasonic and light localization
 */
public class Localization {
	private final double BTWN_LINES = 1;
	private final double ON_LINES = 0.5;
	private final double US_TURNING = 0.75;
	
	private double T1,T2,T3,T4;
	private double distL, distR;
	 
	private Navigation nav;
	private Odometer odo, turns; //turns used to simplify getting angle measurements
	private UltrasonicPoller usPollerLeft;
	private UltrasonicPoller usPollerRight;
	private LightPoller csPoller;

	public Localization(Odometer odo, Navigation nav, UltrasonicPoller usPollerLeft, UltrasonicPoller usPollerRight, 
						LightPoller csPoller) {
		this.nav = nav;
		this.odo = odo;
		this.usPollerLeft = usPollerLeft;
		this.usPollerRight = usPollerRight;
		this.csPoller = csPoller;
	}
	
	public void doUSLocalization(){
		//compare left and right us readings to decide which direction to start turning
		distL = usPollerLeft.getMedianDistance();
		distR = usPollerRight.getMedianDistance();
		
		if(distL>distR){
			turns.setAngle(0);
			while(!isOrthogonal()){
				nav.setRotationCW(true, US_TURNING);
			}
			if (Math.abs(turns.getAngle()) >= 90){
				odo.setAngle(180);
			}
		}
		else if (distL<distR){
			turns.setAngle(0);
			while(!isOrthogonal()){
				nav.setRotationCW(false, US_TURNING);
			}
			if (Math.abs(turns.getAngle()) >= 90){
				odo.setAngle(270);
			}
		}
		
	}
	
	public void doLSLocalization(){
		nav.travelTo(-5, -5, false, false);
		nav.turnTo(Math.PI/4, true);
		
		turns.setAngle(0);		
		while(!csPoller.lineSeen){
			nav.setRotationCW(true, BTWN_LINES);
		}
		T1 = accurateLineDetection();
	
		while(!csPoller.lineSeen){
			nav.setRotationCW(true, BTWN_LINES);
		}
		T2 = accurateLineDetection();
		
		while(!csPoller.lineSeen){
			nav.setRotationCW(true, BTWN_LINES);
		}
		T3 = accurateLineDetection();
		odo.setAngle( -Math.atan(TwoWheeledRobot.GROUND_LS_X_OFFSET/TwoWheeledRobot.GROUND_LS_Y_OFFSET) + (T2+T3)/2 + 180);
		
		while(!csPoller.lineSeen){
			nav.setRotationCW(true, BTWN_LINES);
		}
		T4 = accurateLineDetection();
		
		calculateCurrentPosition();

	}
	//when a line is found, go back and get the exact range of angles where the line exists
	private double accurateLineDetection(){
		double Tstart, Trange, Tfinal;
		LightPoller.POLLING_PERIOD = 10; //speed up polling
		
		while(csPoller.lineSeen){
			nav.setRotationCW(false, ON_LINES);
		}
		Tstart = turns.getAngle();
		turns.setAngle(0);
		
		while(!csPoller.lineSeen){
			nav.setRotationCW(true, ON_LINES);
		}
		while(csPoller.lineSeen){
			nav.setRotationCW(true, ON_LINES);
		}
		Trange = turns.getAngle();
		
		Tfinal = Trange/2 + Tstart;
		
		LightPoller.POLLING_PERIOD = 50; //turn down polling speed again
		return Tfinal;
	}
	
	private void calculateCurrentPosition(){
		double thetaX = (360- (T2+T3) )*Math.PI/180;
		double thetaY = (360- (T3+T4) )*Math.PI/180;
		double x = -TwoWheeledRobot.GROUND_LS_Y_OFFSET* Math.cos(thetaX/2);//DIST_BTW_LS_AXLE is the radius of the circle
		double y = -TwoWheeledRobot.GROUND_LS_Y_OFFSET* Math.cos(thetaY/2);
		
		double[] pos = {x,y,0};
		boolean[] update = {true,true,false};
		
		synchronized(odo.lock){ 
			odo.setPosition(pos, update);//update the odometer's values accordingly
		}
		
	}
	
	private boolean isOrthogonal(){
		distL = usPollerLeft.getMedianDistance();
		distR = usPollerRight.getMedianDistance();

		if (Math.abs(distL-distR) < 1){
			return true;
		}
		return false;
	}
}

