package Robot;

import Controller.Coordinate;
import Controller.Colour;

/**
 * Bluetooth message specifying robot's starting corner, the location of its Home zone, the 
location of its opponent’s Home zone, the location of the destination for the captured 
flag, the location of the opponent’s destination for the captured flag, the color of the 
robot’s Home flag, and the color of the opponent’s Home flag. 

For example, assume a 
robot is placed in corner X4, with Home corresponding to the green rectangle and 
Opponent Home corresponding to the red. The Bluetooth class provided will return 15 
integers (4, 2, -1, 4, 2, 6, 8, 8, 11, 1, 3, 8, 4, 2, 3) corresponding to the corner ID, the 
coordinates of the lower left and upper right corners of the Home and Opponent zones 
respectively, the location of the lower left corner in which to place the captured flag for 
the robot and Opponent respectively, the and 2 integers corresponding to the Home and 
opponent’s Home flag respectively
 * 
 * This class should set these things in the Controller
 * 
 * @author Joanna
 *
 */
public class Bluetooth {
	public Coordinate ourStartPos;
	public Coordinate ourZoneBottomLeft;
	public Coordinate ourZoneTopRight;
	public Coordinate ourDropZoneBottomLeftCorner;
	public Colour OurFlagType;
	
	//Opponent 
	public Coordinate opponentStartPos;
	public Coordinate opponentZoneBottomLeft;
	public Coordinate opponentZoneTopRight;
	public Coordinate opponentDropZoneBottomLeftCorner;
	public Colour opponentFlagType;
	
	public Bluetooth() {
		this.ourStartPos = ourStartPos = new Coordinate(0, 0);
		this.ourZoneBottomLeft = new Coordinate(0, 0);
		this.ourZoneTopRight = new Coordinate(0, 0);
		this.ourDropZoneBottomLeftCorner = new Coordinate(0, 0);
		OurFlagType = Colour.RED;
		this.opponentStartPos = new Coordinate(0, 0);
		this.opponentZoneBottomLeft = new Coordinate(0, 0);
		this.opponentZoneTopRight = new Coordinate(0, 0);
		this.opponentDropZoneBottomLeftCorner = new Coordinate(0, 0);
		this.opponentFlagType = Colour.RED;
	}

	public Coordinate getOurStartPos() {
		return ourStartPos;
	}

	public void setOurStartPos(Coordinate ourStartPos) {
		this.ourStartPos = ourStartPos;
	}

	public Coordinate getOurZoneBottomLeft() {
		return ourZoneBottomLeft;
	}

	public void setOurZoneBottomLeft(Coordinate ourZoneBottomLeft) {
		this.ourZoneBottomLeft = ourZoneBottomLeft;
	}

	public Coordinate getOurZoneTopRight() {
		return ourZoneTopRight;
	}

	public void setOurZoneTopRight(Coordinate ourZoneTopRight) {
		this.ourZoneTopRight = ourZoneTopRight;
	}

	public Coordinate getOurDropZoneBottomLeftCorner() {
		return ourDropZoneBottomLeftCorner;
	}

	public void setOurDropZoneBottomLeftCorner(
			Coordinate ourDropZoneBottomLeftCorner) {
		this.ourDropZoneBottomLeftCorner = ourDropZoneBottomLeftCorner;
	}

	public Colour getOurFlagType() {
		return OurFlagType;
	}

	public void setOurFlagType(Colour ourFlagType) {
		OurFlagType = ourFlagType;
	}

	public Coordinate getOpponentStartPos() {
		return opponentStartPos;
	}

	public void setOpponentStartPos(Coordinate opponentStartPos) {
		this.opponentStartPos = opponentStartPos;
	}

	public Coordinate getOpponentZoneBottomLeft() {
		return opponentZoneBottomLeft;
	}

	public void setOpponentZoneBottomLeft(Coordinate opponentZoneBottomLeft) {
		this.opponentZoneBottomLeft = opponentZoneBottomLeft;
	}

	public Coordinate getOpponentZoneTopRight() {
		return opponentZoneTopRight;
	}

	public void setOpponentZoneTopRight(Coordinate opponentZoneTopRight) {
		this.opponentZoneTopRight = opponentZoneTopRight;
	}

	public Coordinate getOpponentDropZoneBottomLeftCorner() {
		return opponentDropZoneBottomLeftCorner;
	}

	public void setOpponentDropZoneBottomLeftCorner(
			Coordinate opponentDropZoneBottomLeftCorner) {
		this.opponentDropZoneBottomLeftCorner = opponentDropZoneBottomLeftCorner;
	}

	public Colour getOpponentFlagType() {
		return opponentFlagType;
	}

	public void setOpponentFlagType(Colour opponentFlagType) {
		this.opponentFlagType = opponentFlagType;
	}
	

}
