package Controller;

import Controller.*;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import Robot.*;

/**
 * Does both ultrasonic and light localization 
 */
public class Localization extends Thread{
	public static final int CLOSE_THRESHOLD = 40;
	
	private double usA, usB;
	private double Tstart, Trange, Tfinal;
	private double T1,T2,T3,T4;
	private double distL, distR;
	private double newDistL, newDistR;
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
		
		
//		double pos[] = new double[3];
//		boolean update[] = {false,false,false};
//		double errorTheta = 99999;
//		boolean orthogonal = false;
//		turns.setAngle(0);
//		Sound.beepSequenceUp();
//		
//		try{Thread.sleep(250);} catch (Exception e){}; //give the poller some time to get values
//		
//		robot.setSpeeds(0, 20);
//		while(!wallFound){ //wallFound starts false
//			angle = turns.getAngle();
//			if (usPollerLeft.getMedianDistance()<= CLOSE_THRESHOLD || usPollerRight.getMedianDistance()<= CLOSE_THRESHOLD){
//				wallFound = true;
//			}
//			else{ 
//				wallFound = false; //this is here in case something changes wallFound from somewhere else
//			}
//		}
//		robot.setSpeeds(0, 0);
//		Sound.beepSequence();
//		
//		if (Math.abs(angle) <= 1 ){//there has been no turn
//			Sound.twoBeeps();
//			while(errorTheta >= 1){ //errorTheta starts at 9999
//				errorTheta = calculateErrorTheta(usPollerLeft, usPollerRight);
//				nav.turnTo((angle-errorTheta), true, true); //turn to orthogonal
//			}
//			//now the robot is orthogonal, we need to check which wall we are facing
//			nav.turnTo(odo.getAngle()-90, true, true); //turn 90 degrees ccw
//			//check to make sure the robot is facing either a wall (head on) or nothingness
//			if ( 	(usPollerLeft.getMedianDistance()<= CLOSE_THRESHOLD && usPollerRight.getMedianDistance()<= CLOSE_THRESHOLD) 
//					||
//					(usPollerLeft.getMedianDistance()> CLOSE_THRESHOLD && usPollerRight.getMedianDistance()> CLOSE_THRESHOLD) 
//				){
//				orthogonal = true;
//			}
//			else{
//				orthogonal = false;
//			}
//			if (orthogonal){
//				if (usPollerLeft.getMedianDistance()<= CLOSE_THRESHOLD){//orthogonal, so pick one. case: facing a wall, still
//					pos[0] = 999999;
//					pos[1] = (usPollerLeft.getMedianDistance() + usPollerRight.getMedianDistance())/2; // just in case
//					pos[2] = 180;
//					update[0] = false;
//					update[1] = true;
//					update[2] = true;
//					odo.setPosition(pos, update);
//				}
//				else { // case: facing openness
//					pos[0] = 999999;
//					pos[1] = 999999;
//					pos[2] = 90;
//					update[0] = false;
//					update[1] = false;
//					update[2] = true;
//					odo.setPosition(pos, update);
//				}
//			}
//			else{//not orthogonal, this is improbable. Deal with this later
//				Sound.buzz();
//				try{Thread.sleep(100);} catch (Exception e){};
//				Sound.buzz(); //these are bad buzzes to hear
//			}
//		}
//		else if (Math.abs(angle) > 2){// now facing the bottom wall
//			Sound.beep();
//			while(errorTheta >= 1){ //remember, errorTheta STARTS at 9999
//				errorTheta = calculateErrorTheta(usPollerLeft, usPollerRight);
//				nav.turnTo((angle-errorTheta), true, true); //turn to orthogonal
//			}
//			pos[0] = 999999;
//			pos[1] = (usPollerLeft.getMedianDistance() + usPollerRight.getMedianDistance())/2; // just in case
//			pos[2] = 180;
//			update[0] = false;
//			update[1] = true;
//			update[2] = true;
//			odo.setPosition(pos, update);
//		}
	}
		
	
	
	private double calculateErrorTheta(UltrasonicPoller left, UltrasonicPoller right){
		try{Thread.sleep(100);} catch (Exception e){};
		
		double lengthL = left.getMedianDistance();
		double lengthR = right.getMedianDistance();
		
		if (lengthL > 200){
			return 70;
		}
		if (lengthR > 200){
			return -70;
		}

		return Math.atan((lengthR-lengthL)/TwoWheeledRobot.SENSOR_WIDTH)*180/Math.PI; //returns pos or neg radians
	}
//			
//			//Robot turns clockwise until it see a wall
//			//if it already doesn't see a wall, it skips this first while loop
//		robot.setSpeeds(0, 30);
//		while (usPollerLeft.getMedianDistance()>30){}
//		while (usPollerLeft.getMedianDistance()<=30){
//			//Now the robot is facing a wall and it continues rotating clockwise until
//			//it no longer sees a wall
//			//It then stops and latches it's current angle
//		}
//		robot.setSpeeds(0, 0);
//			
//		turns.getPosition(pos);
//		usA = pos[2];
//			
//			//The robot then turns counter clockwise until is sees a wall
//		robot.setSpeeds(0, -30);
//		while (usPollerLeft.getMedianDistance()>30){
//		}
//		while (usPollerLeft.getMedianDistance()<30){
//		}
//		robot.setSpeeds(0, 0);
//			//Then the robot continues counter clockwise until it doesn't see a wall
//			//It will then latch that angle
//			
//		turns.getPosition(pos);
//		usB = pos[2];
//			
//			//Using the 2 latched angles, it calculates what it's current angle must be and puts that between 0 and 360 degrees
//		double currentAngle = Odometer.fixDegAngle(-calculateChangeAngleToZero(usA, usB));
//			
//		odo.setPosition(new double [] {0.0, 0.0, currentAngle}, new boolean [] {true, true, true});
//	
//	}
//		
//	
//	
	public void doLSLocalization(){
		
		nav.turnTo(45, true, true);
		nav.travelDistance(5);
		
		turns.setAngle(0);		
		robot.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		double angle1 = odo.getAngleRadians() - Trange/2;
		T1 = accurateLineDetection();
		Sound.beep();
	
		robot.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T2 = accurateLineDetection();
		Sound.beep();
		
		robot.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T3 = accurateLineDetection();
		odo.setAngle( (-1*Math.atan(TwoWheeledRobot.GROUND_LS_X_OFFSET/TwoWheeledRobot.GROUND_LS_Y_OFFSET) + ((T2-T1)+(T3-T2))/2 + Math.PI*3 )*180/Math.PI);
		Sound.beep();
		
		robot.setSpeeds(0,30);
		while(!csPoller.lineSeen){
		}
		T4 = accurateLineDetection();
		Sound.beep();
		
		robot.setSpeeds(0,0);
		
		calculateCurrentPosition();
	}
	
//		nav.turnTo(90,true,true);
//		nav.travelDistance(odo.getX());
//		nav.turnTo(0, true, true);
//		nav.travelDistance(odo.getY());

	
	
	private double calculateChangeAngleToZero(double angleA, double angleB) {
		double deltaTheta=999999; //this value will be changed in the cases
		
		double middleAngle = (angleA+angleB)/2;

				if(angleA>180){ //This is Rising Edge where the robot started facing away from wall
					deltaTheta = (135-(angleB-middleAngle));
				}
				else{//Rising edge where the robot started facing away from wall
					deltaTheta = (-45-(angleB-middleAngle));
				}

		return deltaTheta;

	}
	
	//when a line is found, go back and get the exact range of angles where the line exists
	private double accurateLineDetection(){
		double tend;
		LightPoller.POLLING_PERIOD = 10; //speed up polling
		
		robot.setSpeeds(0,-15);
		while(csPoller.lineSeen){
		}
		Tstart = turns.getAngleRadians();
		
		robot.setSpeeds(0,15);
		while(!csPoller.lineSeen){
		}
		while(csPoller.lineSeen){
		}

		tend = turns.getAngleRadians();
		Trange = tend-Tstart;
		
		Tfinal = Trange/2 + Tstart;
		
		LightPoller.POLLING_PERIOD = 20; //turn down polling speed again
		
		return Tfinal;
	}
	
	private void calculateCurrentPosition(){
		double firstAngle = T2-T1;
		double secondAngle = T3-T2;
		double thirdAngle = T3-T2;
		
		double thetaX = (2*Math.PI - (firstAngle+secondAngle) );
		double thetaY = (2*Math.PI- (secondAngle+thirdAngle) );
		double x = lightSensorDist* Math.cos(thetaX/2);//DIST_BTW_LS_AXLE is the radius of the circle
		double y = lightSensorDist* Math.cos(thetaY/2);
		
		double[] pos = {x,y,99999090};
		boolean[] update = {true,true,false};
		
		synchronized(odo.lock){ 
			odo.setPosition(pos, update);//update the odometer's values accordingly
			
		}
		
	}
	
	public boolean isOrthogonal(){
		newDistL = usPollerLeft.getMedianDistance();
		newDistR = usPollerRight.getMedianDistance();
		
		if(newDistL>40 || newDistR>40){
			return false;
		}
		if (Math.abs(newDistL-newDistR) < 2){
			return true;
		}
		return false;
	}
	 public static double dTheta(double a, double b) {// from the tutorial
	        double theta = 0;
	        if (a <= b) {
	            theta = 45 - (a + b) / 2;
	        } else {
	            theta = 229 - (a + b) / 2;
	        }
	        return theta;
	    }
}

