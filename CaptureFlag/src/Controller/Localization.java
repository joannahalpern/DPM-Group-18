package Controller;

import Robot.*;

/**
 * Does both ultrasonic and light localization
 */
public class Localization {
	private Navigation nav;
	private Odometer odo;
	private UltrasonicPoller usPollerLeft;
	private UltrasonicPoller usPollerRight;
	private LightPoller csPoller;

	public Localization(Odometer odo, Navigation nav, UltrasonicPoller usPollerLeft, UltrasonicPoller usPollerRight, 
						LightPoller csPoller) {
		this.nav = nav;
		this.odo = odo;
		this.usPollerLeft = usPollerLeft;
		this.usPollerRight = usPollerRight;
		this.csPoller = csPoller;
	}
}

