package Controller;

import lejos.nxt.NXTRegulatedMotor;
import Robot.*;

/**
 * The robot will pick up the flag block
 *
 */
public class ObjectDisplacement {
	private TwoWheeledRobot robot;
	private NXTRegulatedMotor clamp;
	
	private final int CLAMP_SPEED = 300;
	
	public ObjectDisplacement(TwoWheeledRobot robot, Navigation nav) {
		this.robot = robot;
		this.clamp = robot.getBlockGrabber();

	}
	
	public void run(){
		
		clamp.setSpeed(CLAMP_SPEED);
		clamp.backward();
		clamp.rotate(180);
		clamp.stop();
		return;
		
	}
}
