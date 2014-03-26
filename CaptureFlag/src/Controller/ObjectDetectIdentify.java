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
	private double lowValue = 150;
	private double csValue;
	private Colour flagColour;
	public boolean block = false;

	// Constructor
	public ObjectDetectIdentify(TwoWheeledRobot robot, Navigation nav,
			ObjectDisplacement od, LightPoller csFlagPoller) {
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
		csValue= cs.getRawLightValue();
		LCD.clear();
		LCD.drawString("csValue  " + csValue, 0, 3);
		if(csValue >= lowValue){
			csCount++;
			if(csCount > 3){
				csCount = 0;
				return true;
			}
		}
	return false;
	}

	/**
	 * Reads 5 values for each colour and returns median Compares values to
	 * determine which colour flag it is
	 * 
	 * @return: colour enum
	 */
	public Colour getColour() {
		block = true;
		
		// Calls and returns medians for each value

		csFlagPoller.run(Colour.GREEN);
		double green = csFlagPoller.getMedian();


		csFlagPoller.run(Colour.BLUE);
		double blue = csFlagPoller.getMedian();


		csFlagPoller.run(Colour.RED);
		double red = csFlagPoller.getMedian();

		/*
		 * double green = cs.getRawColor().getGreen(); double red =
		 * cs.getRawColor().getRed(); double blue = cs.getRawColor().getBlue();
		 * double backgroundLumination = cs.getRawColor().getBackground();
		 */

		if ((blue > green )  && (blue > 300) && (blue < 400) && (red > 180) ) {
			LCD.drawString("Dark blue: ", 0, 3);
			// LCD.drawString("LCDblue: " + blue, 0, 3);
			// LCD.drawString("LCDgreen: " + green, 0, 4);
			// LCD.drawString("LCDred: " + red, 0, 5);
			return Colour.DARK_BLUE;
		} 
		else if ( (red > green) && (green > blue) && (red > 300) && (green > 300)) {
			LCD.drawString("Yellow: ", 0, 3);
			return Colour.YELLOW;

		} 
		else if ( ( (red > blue) && (red > green) && (red > 300) ) && (blue < 200) ) {
			LCD.drawString("Red: ", 0, 3);
			return Colour.RED;

		} 
		else if ( ((red > 500) && (green > 450) && (blue > 500))  && ((blue +40) > red	)	) { 
			LCD.drawString("Light Blue: ", 0, 3);
			return Colour.LIGHT_BLUE;
		} 
		else if (red >590 ){ 		 // ((red && green && blue) >300) && ( (red + 20) > (green &&
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