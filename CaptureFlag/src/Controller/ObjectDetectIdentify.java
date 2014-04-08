package Controller;

import Robot.*;
import lejos.nxt.*;

/**
 * This class is used to identify a block. If an object is detected with the
 * ultrasonic sensor within DETECTION_THRESHOLD, then the robot moves forward
 * until the object is within COLOUR_READING_THRESHOLD. At this point the robot
 * stops and does identifyBlock(). If the block is within foam limit, it is
 * identified as a styrofoam block and block=true. Otherwise block=false.
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
	
	//Search variables
	private int csCount = 0;
	private double lowValue = 300;	// Needs to be calibrated for upstairs
	private double csValue;
	private Colour flagColour;
	public boolean block = false;
	private double[] values = new double[3];
	
	

	// Constructor
	public ObjectDetectIdentify(TwoWheeledRobot robot, Navigation nav,
			ObjectDisplacement od) {
		this.csFlagPoller = csFlagPoller;
		this.robot = robot;
		this.nav = nav;
		this.cs = robot.getColourSensorFlag();
		this.od = od;

		this.usLeft = this.robot.getLeftUSSensor();
		this.usRight = this.robot.getRightUSSensor();
	}


	/**Continuously checks light cs for a value greater than base value
	 * 
	 *
	 * @return: true if more than 3 consecutive high values
	 */
	public boolean isBlock(){
		double baseRed= cs.getRawColor().getRed();
		double baseBlue = cs.getRawColor().getBlue();
		double baseGreen = cs.getRawColor().getGreen();
		LCD.clear();
		LCD.drawString("baseRed  " + baseRed, 0, 4);   
		LCD.drawString("baseBlue  " + baseBlue, 0, 5);
		LCD.drawString("BaseGreen  " + baseGreen, 0, 6);
		if( baseRed > lowValue || baseBlue > lowValue || baseGreen > lowValue){
			csCount++;
			if(csCount > 5){
				csCount = 0;
				LCD.drawString("True", 0, 3);
				return true;
			}
		}
		LCD.drawString("false", 0, 3);
	return false;
	}

	/**
	 * Reads 5 values for each colour and returns median Compares values to
	 * determine which colour flag it is
	 * 
	 * Having issues Using light poller, kills both threads when called. 
	 * 
	 * @return: colour enum
	 */
	public Colour getColour() {
		block = true;
		LCD.clear();
		// Calls and returns medians for each value
		
		
//		csFlagPoller.run(Colour.GREEN);
//		double green = csFlagPoller.getMedian();
//		try { Thread.sleep((250)); } catch(Exception e){}
	
		
		double green = cs.getRawColor().getGreen();
		LCD.drawString("Green Value: " + green, 0, 0);

//		csFlagPoller.run(Colour.BLUE);
//		double blue = csFlagPoller.getMedian()
		double blue = cs.getRawColor().getBlue();;
		LCD.drawString("Blue value: " + blue, 0, 1);

//		csFlagPoller.run(Colour.RED);
//		double red = csFlagPoller.getMedian();
		double red = cs.getRawColor().getRed();
		LCD.drawString("Red Vale: : " + red, 0, 2);
		/*
		 * double green = cs.getRawColor().getGreen(); double red =
		 * cs.getRawColor().getRed(); double blue = cs.getRawColor().getBlue();
		 * double backgroundLumination = cs.getRawColor().getBackground();
		 */

		if ((blue > green )  && (blue > 270) && (blue < 400) && (red < 250) && (red > 150) ) {
			LCD.drawString("Dark blue: ", 0, 3);
			// LCD.drawString("LCDblue: " + blue, 0, 3);
			// LCD.drawString("LCDgreen: " + green, 0, 4);
			// LCD.drawString("LCDred: " + red, 0, 5);
			return Colour.DARK_BLUE;
		} 
		else if ( (red > green) && (green > blue) && (red > 300) && (green > 200) && (blue < 200)) {
			LCD.drawString("Yellow: ", 0, 3);
			return Colour.YELLOW;

		} 
		else if ( ( (red > blue) && (red > green) && (red > 400) ) && (blue < 200) && (green < 200)) {
			LCD.drawString("Red: ", 0, 3);
			return Colour.RED;

		} 
		else if ( ((red > 450) && (green > 400) && (blue > 450))  && ((blue +40) > red	)	) { 
			LCD.drawString("Light Blue: ", 0, 3);
			return Colour.LIGHT_BLUE;
		} 
		else if ((red > blue) && (red > green) && (red > 500 ) && (green > 400) && (blue > 400)){ 		 // ((red && green && blue) >300) && ( (red + 20) > (green &&
					// blue) )
			LCD.drawString("White: ", 0, 3);
			return Colour.WHITE;
		}
		else{
			LCD.drawString("Nothing", 0, 3);
			block = false;
			return Colour.OFF;
		}

	}


}