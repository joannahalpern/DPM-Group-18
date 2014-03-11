package Controller;
 
import Display.*;
import Robot.*;

import lejos.nxt.*;

/**
 * This class is used to identify a block. If an object is detected with the ultrasonic sensor within DETECTION_THRESHOLD,
 * then the robot moves forward until the object is within COLOUR_READING_THRESHOLD. At this point the robot stops and
 *  does identifyBlock(). If the block is within foam limit, it is identified as a styrofoam block and block=true. 
 *  Otherwise block=false.
 */
public class ObjectDetectIdentify extends Thread {
	private static final double COLOUR_READING_THRESHOLD = 9;
	private static final double DETECTION_THRESHOLD = 30;
	private static final double LOWER_FOAM_LIMIT = 380;
	private static final double UPPER_FOAM_LIMIT = 540;
	private static final double LOWER_WOOD_LIMIT = 230;
	private static final double UPPER_WOOD_LIMIT = LOWER_FOAM_LIMIT;
	
	private UltrasonicPoller usPoller;
	private LightPoller lsPoller;
	private Navigation nav;
	private boolean objectDetected;
	private boolean block;
	private BlockType blockType;
	
	public ObjectDetectIdentify(UltrasonicPoller usPoller, LightPoller lsPoller, Navigation nav){
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		this.nav = nav;
		this.objectDetected = false;
		this.block = false;
		this.blockType = BlockType.UNKNOWN;
	}
	
	public void run() {
		while(true){
			doBlockDetection();
			try { Thread.sleep(100); } catch(Exception e){}
			objectDetected = false;
		}
	}
	
	/**
	 *  Returns true if styrofoam block is detected and false otherwise
	 */
	public boolean doBlockDetection(){
			if (isBlockinRange(usPoller, DETECTION_THRESHOLD)){
				objectDetected = true; //LCD will now display "Object Detected"

				//check to see block is close enough to read colour. If not, then move forward.
				while (!isBlockinRange(usPoller, COLOUR_READING_THRESHOLD)){
					nav.setSpeeds(50, 50);
				}
				nav.setSpeeds(0, 0);//stop
				blockType = identifyBlock(lsPoller);
				switch (blockType){
					case STYROFOAM:
						block = true; //displays "Block" on LCD
						break;
					case WOOD:
						block = false; //displays "Not Block" on LCD
						break;
					case UNKNOWN:
						block = false; //displays "Not Block" on LCD
						break;
					default:
						block = false;
						break;
				}
			}
			return block;
	}

	/**
	 * returns true if ultrasonc sensor detects that block is within given threshold. Else returns false
	 */
	public boolean isBlockinRange(UltrasonicPoller usPoller, double threshold){
		double distance = usPoller.getMedianDistance(); 
		if (distance< threshold){
			return true;
		}
		return false;
	}
	
	public BlockType identifyBlock(LightPoller lsPoller){
		try { Thread.sleep(lsPoller.POLLING_PERIOD*5); } catch(Exception e){}
		double colourVal = lsPoller.getMedian();
		BlockType blockType;
		
		if ((LOWER_FOAM_LIMIT < colourVal) && (colourVal < UPPER_FOAM_LIMIT)){
			blockType = BlockType.STYROFOAM;
		}
		else if ((LOWER_WOOD_LIMIT) < colourVal && colourVal < (UPPER_WOOD_LIMIT)){
			blockType = BlockType.WOOD;
		}
		else{
			blockType = BlockType.UNKNOWN;
		}
		
		return blockType;
	}

	public boolean isObjectDetected() {
		return objectDetected;
	}

	public boolean isBlock() {
		return block;
	}
	
	public BlockType getBlockType(){
		return blockType;
	}

}