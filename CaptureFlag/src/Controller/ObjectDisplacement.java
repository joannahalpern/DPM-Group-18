package Controller;

import lejos.nxt.NXTRegulatedMotor;
import Robot.*;

/**
 * The robot will pick up the flag block
 *
 */
public class ObjectDisplacement {
	private TwoWheeledRobot robot;
	private NXTRegulatedMotor blockGrabber;
	private Navigation nav;
	
	public ObjectDisplacement(TwoWheeledRobot robot, Navigation nav) {
		this.robot = robot;
		this.blockGrabber = robot.getBlockGrabber();
		this.nav = nav;
	}
}
