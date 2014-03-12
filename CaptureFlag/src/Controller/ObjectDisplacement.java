package Controller;

import Robot.*;

/**
 * The robot will pick up and throw styrofome block which is not the flag
 * @author Joanna
 *
 */
public class ObjectDisplacement {
	private UltrasonicPoller usPoller;
	private LightPoller colourDetector;
	private Navigation nav;
	
	public ObjectDisplacement(UltrasonicPoller usPoller, LightPoller colourDetector, Navigation nav) {
		this.usPoller = usPoller;
		this.colourDetector = colourDetector;
		this.nav = nav;
	}

	public static void throwBlock(){
	}
	
}
