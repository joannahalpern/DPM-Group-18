package Controller;
 
import Robot.*;
import lejos.nxt.*;

/**
 * This class is used to identify a block. If an object is detected with the ultrasonic sensor within DETECTION_THRESHOLD,
 * then the robot moves forward until the object is within COLOUR_READING_THRESHOLD. At this point the robot stops and
 *  does identifyBlock(). If the block is within foam limit, it is identified as a styrofoam block and block=true. 
 *  Otherwise block=false.
 */
public class ObjectDetectIdentify {
	private Navigation nav;
	private TwoWheeledRobot robot;
	private ColorSensor cs;
	private UltrasonicSensor usLeft, usRight;
	private ObjectDisplacement od;
	
	private LightPoller csFlagPoller;
	private UltrasonicPoller usPollerLeft;
	private UltrasonicPoller usPollerRight;

	//Constructor
	public ObjectDetectIdentify(TwoWheeledRobot robot, Navigation nav, ObjectDisplacement od){
		this.robot = robot;
		this.nav = nav;
		this.cs = robot.getColourSensorFlag();
		this.od = od;

		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();
	}
	
	public boolean search(double x0, double y0, double x1, double y1){
		double angle = nav.calculateAngle(x0 + 15, y0);
		nav.turnTo(angle, true, false);
		travelDistance(y1 - y0);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//this method will do the object detection
	public void doDetection(){
		
		boolean doing = true;
		while(doing){
			//this should use nav, not the motors
			leftMotor.forward();
			rightMotor.forward();
			
			doUSBlockDetection();
			
			leftMotor.rotate(90);
			rightMotor.rotate(-90);
					
			doUSBlockDetection();
			
		}
	}
		
	
	public void doUSBlockDetection() {
			if ( ( (usLeft.getDistance() < 13) || (usRight.getDistance()  < 13) ) )
			{
				LCD.drawString("Object Detected!", 0, 1);
				doCSBlockDetection();
			}
			
		}	
	
			
			
			public void doCSBlockDetection() {
				
			
			/*
			 * color sensor can distinguish the blue foam and wood block
			 * by calculating GREEN/RED's value 
			 */
				boolean isNotBlock = true;
				boolean isBlock = false;
			    double green = cs.getRawColor().getGreen();
				double red = cs.getRawColor().getRed();
				double blue = cs.getRawColor().getBlue();
				double backgroundLumination = cs.getRawColor().getBackground();
				
				if( (blue > green ) && (green > red) ) {
					LCD.drawString("Dark blue: ", 0, 2);
//					LCD.drawString("LCDblue: " + blue, 0, 3);
//					LCD.drawString("LCDgreen: " + green, 0, 4);
//					LCD.drawString("LCDred: " + red, 0, 5);
					isBlock = true;
					od.ObjectDisplacement();
				}
				else if ( (red > green) && (green > blue)) {
					LCD.drawString("Yellow: ", 0, 2);
					isBlock = true;
					od.ObjectDisplacement();
					
				}
				else if ( ( (red > blue) && (red > green) ) ) {
					LCD.drawString("Red: ", 0, 2);
					isBlock = true;
					od.ObjectDisplacement();
					
				}
				else if ( ((red > 300) && (green > 300) && (blue > 300)) && (( (blue +70) > green) && ( (blue +70) > red) )	) { //((red && green && blue) >300) && ( (blue + 70) ) > (green && red) )
					LCD.drawString("Light Blue: ", 0, 2);
					isBlock = true;
					od.ObjectDisplacement();
				}
				else { 			//((red && green && blue) >300) && ( (red + 20)  > (green && blue) )
				LCD.drawString("White: ", 0, 2);
				isBlock = true;
				od.ObjectDisplacement();	
				}
									
			}
		
			
			
			
			
/*		 
	public boolean isBlock(){
		return this.isBlock;
	}
	
	//return true if the robot detects a block
	public boolean isNotBlock(){
		return this.isNotBlock;
	}
	*/
	
}