//TODO: make light threshold variable, dependent on initial light readings. 
  
package Controller; 
  
import Controller.*; 
import lejos.nxt.Sound; 
import Robot.*; 
  
/** 
 * Does both ultrasonic and light localization  
 */
public class Localization extends Thread{ 
    public static final int CLOSE_THRESHOLD = 50; 
      
    public static double intlReading = 350; //tentative value, will be as soon as light localization starts 
    public static double usA, usB; 
    private double Tstart, Trange, Tfinal; 
    private double T1,T2,T3,T4; 
    private double lightSensorDist =  
            Math.sqrt(TwoWheeledRobot.GROUND_LS_X_OFFSET*TwoWheeledRobot.GROUND_LS_X_OFFSET +  
                    TwoWheeledRobot.GROUND_LS_Y_OFFSET*TwoWheeledRobot.GROUND_LS_Y_OFFSET); 
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
          
        nav.setSpeeds(0, -50); 
        while(usPollerLeft.getMedianDistance()<CLOSE_THRESHOLD || usPollerRight.getMedianDistance()<CLOSE_THRESHOLD){     } 
        while(usPollerRight.getMedianDistance()>=CLOSE_THRESHOLD){   } 
          
        turns.setAngle(0); 
        usA = turns.getAngle(); 
          
        try{Thread.sleep(150);} catch (Exception e){}; 
          
        nav.setSpeeds(0, 50); 
        while(usPollerLeft.getMedianDistance()<CLOSE_THRESHOLD || usPollerRight.getMedianDistance()<CLOSE_THRESHOLD){     } 
        try{Thread.sleep(350);} catch (Exception e){}; 
        while(usPollerLeft.getMedianDistance()>=CLOSE_THRESHOLD){    } 
        nav.setSpeeds(0, 0); 
          
        usB = turns.getAngle(); 
          
        angle = 45 + (usB-usA)/2; 
        odo.setAngle(angle); 
          
    } 
          
    public void doLSLocalization(){ 
          
        nav.turnTo(45, false, true);//travel forward until a line is seen, then travel back a little bit 
        robot.setForward(250); 
        boolean stop = false;  
        while(!stop){        
            if(csPoller.lineSeen){ 
                nav.setSpeeds(0,0); 
                stop = true; 
            } 
        } 
        nav.travelDistance(-4); 
          
        try{Thread.sleep(250);} catch (Exception e){}; 
          
        turns.setAngle(0);       
        nav.setSpeeds(0,35); 
        while(!csPoller.lineSeen){ 
        } 
        T1 = turns.getAngleRadians(); 
        while(csPoller.lineSeen){ 
        } 
            Sound.beep(); 
        try{Thread.sleep(100);} catch (Exception e){}; 
      
//      nav.setSpeeds(0,30); 
        while(!csPoller.lineSeen){ 
        } 
        T2 = turns.getAngleRadians(); 
        while(csPoller.lineSeen){ 
        } 
            Sound.beep(); 
        try{Thread.sleep(100);} catch (Exception e){}; 
          
//      nav.setSpeeds(0,30); 
        while(!csPoller.lineSeen){ 
        } 
        T3 = turns.getAngleRadians(); 
        odo.setAngle( (-1*Math.atan(TwoWheeledRobot.GROUND_LS_X_OFFSET/TwoWheeledRobot.GROUND_LS_Y_OFFSET) + ((T2-T1)+(T3-T2))/2 + Math.PI*3 )*180/Math.PI); 
        while(csPoller.lineSeen){ 
        } 
            Sound.beep(); 
        try{Thread.sleep(100);} catch (Exception e){}; 
          
//      nav.setSpeeds(0,30); 
        while(!csPoller.lineSeen){ 
        } 
        T4 = turns.getAngleRadians(); 
        while(csPoller.lineSeen){ 
        } 
            Sound.beep(); 
        try{Thread.sleep(100);} catch (Exception e){}; 
          
        nav.setSpeeds(0,0); 
        nav.turnTo(0, true, true);
          
        calculateCurrentPosition(); 
        calibratePosition(); 
    } 
      
    public void calibratePosition(){ //takes the starting zone into account to adjust position and heading accordingly 
        double[] pos = new double[3]; 
        boolean[] update = new boolean[3]; 
        switch(Controller.corner){  
        case BOTTOM_LEFT:  
            break;  
        case BOTTOM_RIGHT:  
            pos[0] = odo.getX() + 304.8; 
            pos[1] = 0; 
            pos[2] = odo.getAngle() + 270.0; 
            update[0] = true; 
            update[1] = false; 
            update[2] = true; 
            synchronized(Controller.lock){ 
                odo.setPosition(pos, update); 
            } 
            break; 
        case TOP_LEFT: 
            pos[0] = 999; 
            pos[1] = odo.getY() + 304.8; 
            pos[2] = odo.getAngle() + 90; 
            update[0] = false; 
            update[1] = true; 
            update[2] = true; 
            synchronized(Controller.lock){ 
                odo.setPosition(pos, update); 
            }            
            break;  
        case TOP_RIGHT: 
            pos[0] = odo.getX() + 304.8; 
            pos[1] = odo.getY() + 304.8; 
            pos[2] = odo.getAngle() + 180; 
            update[0] = true; 
            update[1] = true; 
            update[2] = true; 
            synchronized(Controller.lock){ 
                odo.setPosition(pos, update); 
            } 
            break; 
        default: 
            //Same case as BOTTOM_LEFT 
            break; } 
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
          
        try{Thread.sleep(150);} catch (Exception e){}; 
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
        double thirdAngle = T4-T3;// this was corrected from T3-T2 
          
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