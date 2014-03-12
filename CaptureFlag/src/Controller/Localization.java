package Controller;

import Robot.*;

/**
 * Does both ultrasonic and light localization
 */
public class Localization {
	private Navigation nav;
	private Odometer odo;
	private UltrasonicPoller usPoller;
	private LightPoller lsPollerLeft;
	private LightPoller lsPollerRight;

	public Localization(Odometer odo, Navigation nav, UltrasonicPoller usPoller, 
						LightPoller lsPollerLeft, LightPoller lsPollerRight) {
		this.nav = nav;
		this.odo = odo;
		this.usPoller = usPoller;
		this.lsPollerLeft = lsPollerLeft;
		this.lsPollerRight = lsPollerRight;
	}
}

